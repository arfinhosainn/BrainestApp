package com.scelio.brainest.domain.chat


import com.scelio.brainest.domain.models.Chat
import com.scelio.brainest.domain.models.ChatMessage
import com.scelio.brainest.domain.util.DataError
import com.scelio.brainest.domain.util.EmptyResult
import com.scelio.brainest.domain.util.Result


interface SupabaseChatService {

    suspend fun syncChat(chat: Chat): EmptyResult<DataError.Remote>


    suspend fun syncMessage(message: ChatMessage): EmptyResult<DataError.Remote>


    suspend fun fetchUserChats(userId: String): Result<List<Chat>, DataError.Remote>


    suspend fun fetchChatMessages(
        chatId: String,
        limit: Int = 20,
        offset: Int = 0
    ): Result<List<ChatMessage>, DataError.Remote>


    suspend fun deleteChat(chatId: String): EmptyResult<DataError.Remote>


    suspend fun deleteMessage(messageId: String): EmptyResult<DataError.Remote>


    suspend fun updateChatTitle(chatId: String, title: String): EmptyResult<DataError.Remote>
}