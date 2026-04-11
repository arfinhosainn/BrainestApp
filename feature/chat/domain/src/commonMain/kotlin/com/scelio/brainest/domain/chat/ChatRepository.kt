package com.scelio.brainest.domain.chat

import com.scelio.brainest.domain.models.Chat
import com.scelio.brainest.domain.models.ChatMessage
import com.scelio.brainest.domain.models.ChatWithLastMessage
import com.scelio.brainest.domain.models.ConversationHistory
import com.scelio.brainest.domain.models.CreateChatRequest
import com.scelio.brainest.domain.models.SendMessageStreamEvent
import com.scelio.brainest.domain.models.SendMessageRequest
import kotlinx.coroutines.flow.Flow

interface ChatRepository {
    suspend fun createChat(request: CreateChatRequest): Chat
    suspend fun sendMessage(request: SendMessageRequest): ChatMessage
    fun sendMessageStream(request: SendMessageRequest): Flow<SendMessageStreamEvent>
    suspend fun suggestChatTitle(messageContent: String): String?
    suspend fun getChatHistory(chatId: String): ConversationHistory
    suspend fun getChat(chatId: String): Chat?
    suspend fun getUserChats(userId: String): List<ChatWithLastMessage>
    suspend fun updateChatTitle(chatId: String, title: String)
    suspend fun deleteChat(chatId: String)
    suspend fun deleteMessage(messageId: String)
    suspend fun searchChats(userId: String, query: String): List<ChatWithLastMessage>
}
