package com.scelio.brainest.data.chat

import com.scelio.brainest.data.dto.MessageRoles
import com.scelio.brainest.database.mappers.toDomain
import com.scelio.brainest.database.mappers.toEntity
import com.scelio.brainest.data.mappers.toOpenAIRequest
import com.scelio.brainest.database.ChatDao
import com.scelio.brainest.domain.chat.ChatRepository
import com.scelio.brainest.domain.models.Chat
import com.scelio.brainest.domain.models.ChatMessage
import com.scelio.brainest.domain.models.ChatWithLastMessage
import com.scelio.brainest.domain.models.ConversationHistory
import com.scelio.brainest.domain.models.CreateChatRequest
import com.scelio.brainest.domain.models.MessageMetadata
import com.scelio.brainest.domain.models.SendMessageRequest
import kotlin.time.Clock
import kotlin.time.Instant
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid


@OptIn(ExperimentalUuidApi::class)

class ChatRepositoryImpl(
    private val chatDao: ChatDao,
    private val openAI: OpenAIApiService,
    private val clock: Clock = Clock.System
) : ChatRepository {

    override suspend fun createChat(request: CreateChatRequest): Chat {
        val now = clock.now()
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
        chatDao.insertChat(chat.toEntity())
        return chat
    }

    override suspend fun sendMessage(request: SendMessageRequest): ChatMessage {
        val chatEntity = chatDao.getChat(request.chatId)
            ?: error("Chat not found: ${request.chatId}")
        val chat = chatEntity.toDomain()

        val now = clock.now()

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

        val allEntities = chatDao.getMessagesByChatId(request.chatId)
        val recentDomainMessages = allEntities
            .map { it.toDomain() }
            .takeLast(20) // keep token usage reasonable

        val history = ConversationHistory(
            chatId = request.chatId,
            messages = recentDomainMessages
        )

        val openAiRequest = history.toOpenAIRequest(
            model = chat.model,
            systemPrompt = chat.systemPrompt
        )

        val result = openAI.chat(openAiRequest)

        val assistantText = result.extractedOutputText()

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

        val count = chatDao.getMessageCount(request.chatId)
        chatDao.updateChatStats(
            chatId = request.chatId,
            timestampMs = now.toEpochMilliseconds(),
            messageCount = count
        )

        return assistantMessage
    }

    override suspend fun getChatHistory(chatId: String): ConversationHistory {
        val messages = chatDao.getMessagesByChatId(chatId).map { it.toDomain() }
        return ConversationHistory(chatId = chatId, messages = messages)
    }

    override suspend fun getChat(chatId: String): Chat? {
        return chatDao.getChat(chatId)?.toDomain()
    }

    override suspend fun getUserChats(userId: String): List<ChatWithLastMessage> {
        return chatDao.getChatsWithLastMessage(userId).map { row ->
            ChatWithLastMessage(
                chat = row.chat.toDomain(),
                lastMessage = row.lastMessage?.toDomain()
            )
        }
    }

    override suspend fun updateChatTitle(chatId: String, title: String) {
        chatDao.updateChatTitle(chatId, title)
    }

    override suspend fun deleteChat(chatId: String) {

        chatDao.deleteChat(chatId)
    }

    override suspend fun deleteMessage(messageId: String) {
        chatDao.deleteMessage(messageId)
    }

    override suspend fun searchChats(userId: String, query: String): List<ChatWithLastMessage> {
        return chatDao.searchChatsWithLastMessage(userId, query).map { row ->
            ChatWithLastMessage(
                chat = row.chat.toDomain(),
                lastMessage = row.lastMessage?.toDomain()
            )
        }
    }
}