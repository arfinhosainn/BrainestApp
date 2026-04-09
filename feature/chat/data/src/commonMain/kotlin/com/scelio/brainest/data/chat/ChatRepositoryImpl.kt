package com.scelio.brainest.data.chat

import com.scelio.brainest.data.dto.ChatRequest
import com.scelio.brainest.data.dto.ContentDto
import com.scelio.brainest.data.dto.MessageRoles
import com.scelio.brainest.data.dto.MessageDto
import com.scelio.brainest.data.mappers.toOpenAIRequest
import com.scelio.brainest.database.ChatDao
import com.scelio.brainest.database.mappers.toDomain
import com.scelio.brainest.database.mappers.toEntity
import com.scelio.brainest.domain.chat.ChatRepository
import com.scelio.brainest.domain.chat.SupabaseChatService
import com.scelio.brainest.domain.models.Chat
import com.scelio.brainest.domain.models.ChatMessage
import com.scelio.brainest.domain.models.ChatWithLastMessage
import com.scelio.brainest.domain.models.ConversationHistory
import com.scelio.brainest.domain.models.CreateChatRequest
import com.scelio.brainest.domain.models.MessageMetadata
import com.scelio.brainest.domain.models.SendMessageStreamEvent
import com.scelio.brainest.domain.models.SendMessageRequest
import com.scelio.brainest.domain.util.DataError
import com.scelio.brainest.domain.util.EmptyResult
import com.scelio.brainest.domain.util.Result
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import kotlin.time.Clock
import kotlin.time.Instant
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
class ChatRepositoryImpl(
    private val chatDao: ChatDao,
    private val openAI: OpenAIApiService,
    private val supabaseService: SupabaseChatService,
    private val coroutineScope: CoroutineScope
) : ChatRepository {

    override suspend fun createChat(request: CreateChatRequest): Chat {
        val now = Clock.System.now()
        val chat = Chat(
            id = Uuid.random().toString(),
            userId = request.userId,
            title = request.title,
            model = request.model,
            systemPrompt = request.systemPrompt,
            createdAt = now,
            lastActivityAt = now,
            messageCount = 0
        )

        // 1. Save to local DB first (offline-first)
        chatDao.insertChat(chat.toEntity())
        // 2. Sync to Supabase in background
        coroutineScope.launch(Dispatchers.IO) {
            when (val result = supabaseService.syncChat(chat)) {
                is Result.Success -> {

                }

                is Result.Failure -> {
                    println("Failed to sync chat to Supabase: ${result.error}")
                }
            }
        }

        return chat
    }

    override suspend fun sendMessage(request: SendMessageRequest): ChatMessage {
        var assistantMessage: ChatMessage? = null
        sendMessageStream(request).collect { event ->
            if (event is SendMessageStreamEvent.Completed) {
                assistantMessage = event.message
            }
        }
        return assistantMessage ?: error("Assistant message was not produced")
    }

    override fun sendMessageStream(request: SendMessageRequest): Flow<SendMessageStreamEvent> = flow {
        val chatEntity = chatDao.getChat(request.chatId)
            ?: error("Chat not found: ${request.chatId}")
        val chat = chatEntity.toDomain()

        val userMessageTime = Clock.System.now()
        val assistantMessageId = Uuid.random().toString()

        val userMessage = ChatMessage(
            id = Uuid.random().toString(),
            chatId = request.chatId,
            content = request.content,
            role = MessageRoles.USER,
            createdAt = userMessageTime,
            senderId = request.userId,
            imageUrl = request.imageUrl,
            fileId = request.fileId
        )
        chatDao.insertMessage(userMessage.toEntity())
        syncMessageInBackground(userMessage, "user")

        emit(
            SendMessageStreamEvent.Started(
                userMessage = userMessage,
                assistantMessageId = assistantMessageId
            )
        )

        val allEntities = chatDao.getMessagesByChatId(request.chatId)
        val recentDomainMessages = allEntities
            .map { it.toDomain() }
            .sortedBy { it.createdAt.toEpochMilliseconds() }
            .takeLast(20)

        val history = ConversationHistory(
            chatId = request.chatId,
            messages = recentDomainMessages
        )

        val openAiRequest = history.toOpenAIRequest(
            model = chat.model,
            systemPrompt = chat.systemPrompt,
            stream = true
        )

        val assistantText = StringBuilder()
        var completedResponseId: String? = null
        var completedModel: String? = null
        var totalTokens: Int? = null

        openAI.streamChat(openAiRequest).collect { event ->
            when (event) {
                is OpenAiStreamEvent.OutputTextDelta -> {
                    assistantText.append(event.delta)
                    emit(
                        SendMessageStreamEvent.AssistantPartial(
                            messageId = assistantMessageId,
                            content = assistantText.toString()
                        )
                    )
                }

                is OpenAiStreamEvent.Completed -> {
                    completedResponseId = event.response?.id
                    completedModel = event.response?.model
                    totalTokens = event.response?.usage?.totalTokens

                    if (assistantText.isEmpty()) {
                        assistantText.append(event.response?.extractedOutputText().orEmpty())
                    }
                }
            }
        }

        val aiMessageTime = Clock.System.now()
        val finalAiTime = if (aiMessageTime <= userMessageTime) {
            Instant.fromEpochMilliseconds(userMessageTime.toEpochMilliseconds() + 1)
        } else {
            aiMessageTime
        }

        val finalizedAssistantContent = repairMathFormattingIfNeeded(
            content = assistantText.toString(),
            model = completedModel ?: chat.model
        )

        val assistantMessage = ChatMessage(
            id = assistantMessageId,
            chatId = request.chatId,
            content = finalizedAssistantContent,
            role = MessageRoles.ASSISTANT,
            createdAt = finalAiTime,
            senderId = "assistant",
            metadata = MessageMetadata(
                model = completedModel ?: chat.model,
                tokensUsed = totalTokens,
                openAIResponseId = completedResponseId
            )
        )

        chatDao.insertMessage(assistantMessage.toEntity())

        val count = chatDao.getMessageCount(request.chatId)
        chatDao.updateChatStats(
            chatId = request.chatId,
            timestampMs = finalAiTime.toEpochMilliseconds(),
            messageCount = count
        )

        syncAssistantMessageAndChatInBackground(request.chatId, assistantMessage)
        emit(SendMessageStreamEvent.Completed(assistantMessage))
    }

    override suspend fun getChatHistory(chatId: String): ConversationHistory {
        val localMessages = chatDao.getMessagesByChatId(chatId)
            .map { it.toDomain() }
            .sortedBy { it.createdAt.toEpochMilliseconds() }

        val mergedMessages = when (val remoteResult = fetchAllRemoteMessages(chatId)) {
            is Result.Success -> {
                val remoteMessages = remoteResult.data
                    .sortedBy { it.createdAt.toEpochMilliseconds() }

                remoteMessages.forEach { remoteMessage ->
                    chatDao.insertMessage(remoteMessage.toEntity())
                }

                (localMessages + remoteMessages)
                    .associateBy { it.id }
                    .values
                    .sortedBy { it.createdAt.toEpochMilliseconds() }
            }

            is Result.Failure -> {
                println("Failed to fetch messages from Supabase: ${remoteResult.error}")
                localMessages
            }
        }

        return ConversationHistory(chatId = chatId, messages = mergedMessages)
    }

    private suspend fun fetchAllRemoteMessages(
        chatId: String,
        pageSize: Int = 100
    ): Result<List<ChatMessage>, DataError.Remote> {
        val allMessages = mutableListOf<ChatMessage>()
        var offset = 0

        while (true) {
            when (
                val result = supabaseService.fetchChatMessages(
                    chatId = chatId,
                    limit = pageSize,
                    offset = offset
                )
            ) {
                is Result.Success -> {
                    if (result.data.isEmpty()) {
                        break
                    }

                    allMessages += result.data
                    if (result.data.size < pageSize) {
                        break
                    }

                    offset += pageSize
                }

                is Result.Failure -> return Result.Failure(result.error)
            }
        }

        return Result.Success(allMessages)
    }

    override suspend fun getChat(chatId: String): Chat? {
        return chatDao.getChat(chatId)?.toDomain()
    }

    override suspend fun getUserChats(userId: String): List<ChatWithLastMessage> {
        // Read from local DB first
        val localChats = chatDao.getChatsWithLastMessage(userId).map { row ->
            ChatWithLastMessage(
                chat = row.chat.toDomain(),
                lastMessage = row.lastMessage?.toDomain()
            )
        }

        // Sync from Supabase in background
        coroutineScope.launch(Dispatchers.IO) {
            when (val result = supabaseService.fetchUserChats(userId)) {
                is Result.Success -> {
                    result.data.forEach { remoteChat ->
                        chatDao.insertChat(remoteChat.toEntity())
                    }
                }

                is Result.Failure -> {
                    println("Failed to fetch chats from Supabase: ${result.error}")
                }
            }
        }

        return localChats
    }

    override suspend fun updateChatTitle(chatId: String, title: String) {
        // Update locally first
        chatDao.updateChatTitle(chatId, title)

        // Sync to Supabase in background
        coroutineScope.launch(Dispatchers.IO) {
            when (val result = supabaseService.updateChatTitle(chatId, title)) {
                is Result.Success -> {
                    // Successfully synced
                }

                is Result.Failure -> {
                    println("Failed to sync title update: ${result.error}")
                }
            }
        }
    }

    override suspend fun deleteChat(chatId: String) {
        chatDao.deleteChat(chatId)

        coroutineScope.launch(Dispatchers.IO) {
            when (val result = supabaseService.deleteChat(chatId)) {
                is Result.Success -> {
                    // Successfully synced
                }

                is Result.Failure -> {
                    println("Failed to sync chat deletion: ${result.error}")
                }
            }
        }
    }

    override suspend fun deleteMessage(messageId: String) {
        // Delete locally first
        chatDao.deleteMessage(messageId)

        // Sync deletion to Supabase in background
        coroutineScope.launch(Dispatchers.IO) {
            when (val result = supabaseService.deleteMessage(messageId)) {
                is Result.Success -> {
                    // Successfully synced
                }

                is Result.Failure -> {
                    println("Failed to sync message deletion: ${result.error}")
                }
            }
        }
    }

    override suspend fun searchChats(userId: String, query: String): List<ChatWithLastMessage> {
        return chatDao.searchChatsWithLastMessage(userId, query).map { row ->
            ChatWithLastMessage(
                chat = row.chat.toDomain(),
                lastMessage = row.lastMessage?.toDomain()
            )
        }
    }


    suspend fun syncFromSupabase(userId: String): EmptyResult<DataError.Remote> {
        return when (val chatsResult = supabaseService.fetchUserChats(userId)) {
            is Result.Success -> {
                chatsResult.data.forEach { chat ->
                    chatDao.insertChat(chat.toEntity())

                    // Fetch messages for each chat
                    when (val messagesResult = supabaseService.fetchChatMessages(chat.id)) {
                        is Result.Success -> {
                            messagesResult.data.forEach { message ->
                                chatDao.insertMessage(message.toEntity())
                            }
                        }

                        is Result.Failure -> {
                            return Result.Failure(messagesResult.error)
                        }
                    }
                }
                Result.Success(Unit)
            }

            is Result.Failure -> Result.Failure(chatsResult.error)
        }
    }

    private fun syncMessageInBackground(
        message: ChatMessage,
        label: String
    ) {
        coroutineScope.launch(Dispatchers.IO) {
            when (val result = supabaseService.syncMessage(message)) {
                is Result.Success -> Unit
                is Result.Failure -> println("Failed to sync $label message: ${result.error}")
            }
        }
    }

    private fun syncAssistantMessageAndChatInBackground(
        chatId: String,
        assistantMessage: ChatMessage
    ) {
        coroutineScope.launch(Dispatchers.IO) {
            when (val syncResult = supabaseService.syncMessage(assistantMessage)) {
                is Result.Success -> Unit
                is Result.Failure -> println("Failed to sync assistant message: ${syncResult.error}")
            }

            chatDao.getChat(chatId)?.toDomain()?.let { updatedChat ->
                when (val chatSyncResult = supabaseService.syncChat(updatedChat)) {
                    is Result.Success -> Unit
                    is Result.Failure -> println("Failed to sync updated chat: ${chatSyncResult.error}")
                }
            }
        }
    }

    private suspend fun repairMathFormattingIfNeeded(
        content: String,
        model: String
    ): String {
        val normalized = MathFormattingGuard.normalize(content)
        if (!MathFormattingGuard.needsRepair(normalized)) {
            return normalized
        }

        val repaired = runCatching {
            openAI.chat(
                ChatRequest(
                    model = model,
                    instructions = MATH_FORMAT_REPAIR_INSTRUCTIONS,
                    input = listOf(
                        MessageDto(
                            role = MessageRoles.USER,
                            content = listOf(ContentDto.Text(normalized))
                        )
                    )
                )
            ).extractedOutputText()
        }.onFailure { error ->
            println("Failed to repair math formatting: ${error.message}")
        }.getOrNull()

        val normalizedRepaired = MathFormattingGuard.normalize(repaired.orEmpty())
        return normalizedRepaired.ifBlank {
            normalized
        }
    }
}

private val MATH_FORMAT_REPAIR_INSTRUCTIONS = """
Rewrite the user's answer without changing the meaning, ordering, or substantive wording more than necessary.

Rules:
- Convert every inline math expression to $...$
- Convert every display equation to $$...$$
- Never use \( \) or \[ \]
- Do not add or remove steps
- Do not add explanations
- Return only the corrected answer text
""".trimIndent()
