package com.scelio.brainest.data.chat

import com.scelio.brainest.data.dto.MessageRoles
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
import com.scelio.brainest.domain.models.SendMessageRequest
import com.scelio.brainest.domain.util.Result
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
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
                    // Successfully synced
                }
                is Result.Failure -> {
                    // Log error but don't fail the operation
                    println("Failed to sync chat to Supabase: ${result.error}")
                }
            }
        }

        return chat
    }

    override suspend fun sendMessage(request: SendMessageRequest): ChatMessage {
        val chatEntity = chatDao.getChat(request.chatId)
            ?: error("Chat not found: ${request.chatId}")
        val chat = chatEntity.toDomain()

        val now = Clock.System.now()

        // Create and save user message locally
        val userMessage = ChatMessage(
            id = Uuid.random().toString(),
            chatId = request.chatId,
            content = request.content,
            role = MessageRoles.USER,
            createdAt = now,
            senderId = request.userId,
            imageUrl = request.imageUrl,
            fileId = request.fileId
        )
        chatDao.insertMessage(userMessage.toEntity())

        // Sync user message to Supabase in background
        coroutineScope.launch(Dispatchers.IO) {
            when (val result = supabaseService.syncMessage(userMessage)) {
                is Result.Success -> {
                    // Successfully synced
                }
                is Result.Failure -> {
                    println("Failed to sync user message: ${result.error}")
                }
            }
        }

        // Get recent messages for context
        val allEntities = chatDao.getMessagesByChatId(request.chatId)
        val recentDomainMessages = allEntities
            .map { it.toDomain() }
            .takeLast(20)

        val history = ConversationHistory(
            chatId = request.chatId,
            messages = recentDomainMessages
        )

        val openAiRequest = history.toOpenAIRequest(
            model = chat.model,
            systemPrompt = chat.systemPrompt
        )

        // Get AI response
        val result = openAI.chat(openAiRequest)
        val assistantText = result.extractedOutputText()

        // Create and save assistant message locally
        val assistantMessage = ChatMessage(
            id = Uuid.random().toString(),
            chatId = request.chatId,
            content = assistantText,
            role = MessageRoles.ASSISTANT,
            createdAt = Instant.fromEpochSeconds(result.created ?: 0L),
            senderId = "assistant",
            metadata = MessageMetadata(
                model = result.model,
                tokensUsed = result.usage?.totalTokens,
                openAIResponseId = result.id
            )
        )

        chatDao.insertMessage(assistantMessage.toEntity())

        // Update chat stats
        val count = chatDao.getMessageCount(request.chatId)
        chatDao.updateChatStats(
            chatId = request.chatId,
            timestampMs = now.toEpochMilliseconds(),
            messageCount = count
        )

        // Sync assistant message and updated chat to Supabase in background
        coroutineScope.launch(Dispatchers.IO) {
            when (val syncResult = supabaseService.syncMessage(assistantMessage)) {
                is Result.Success -> {
                    // Successfully synced
                }
                is Result.Failure -> {
                    println("Failed to sync assistant message: ${syncResult.error}")
                }
            }

            // Re-fetch updated chat and sync
            chatDao.getChat(request.chatId)?.toDomain()?.let { updatedChat ->
                when (val chatSyncResult = supabaseService.syncChat(updatedChat)) {
                    is Result.Success -> {
                        // Successfully synced
                    }
                    is Result.Failure -> {
                        println("Failed to sync updated chat: ${chatSyncResult.error}")
                    }
                }
            }
        }

        return assistantMessage
    }

    override suspend fun getChatHistory(chatId: String): ConversationHistory {
        // Read from local DB (offline-first)
        val messages = chatDao.getMessagesByChatId(chatId).map { it.toDomain() }

        // Optionally sync from Supabase in background to get any missing messages
        coroutineScope.launch(Dispatchers.IO) {
            when (val result = supabaseService.fetchChatMessages(chatId)) {
                is Result.Success -> {
                    // Sync any messages that don't exist locally
                    result.data.forEach { remoteMessage ->
                        val exists = messages.any { it.id == remoteMessage.id }
                        if (!exists) {
                            chatDao.insertMessage(remoteMessage.toEntity())
                        }
                    }
                }
                is Result.Failure -> {
                    println("Failed to fetch messages from Supabase: ${result.error}")
                }
            }
        }

        return ConversationHistory(chatId = chatId, messages = messages)
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
        // Delete locally first
        chatDao.deleteChat(chatId)

        // Sync deletion to Supabase in background
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


    suspend fun syncFromSupabase(userId: String): com.scelio.brainest.domain.util.EmptyResult<com.scelio.brainest.domain.util.DataError.Remote> {
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
}