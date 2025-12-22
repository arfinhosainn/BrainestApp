package com.scelio.brainest.data.chat

import com.scelio.brainest.data.logging.KermitLogger
import com.scelio.brainest.data.mappers.SupabaseChatDto
import com.scelio.brainest.data.mappers.toDomain
import com.scelio.brainest.data.mappers.toSupabaseDto
import com.scelio.brainest.domain.chat.SupabaseChatService
import com.scelio.brainest.domain.logging.BrainestLogger
import com.scelio.brainest.domain.models.Chat
import com.scelio.brainest.domain.models.ChatMessage
import com.scelio.brainest.domain.util.DataError
import com.scelio.brainest.domain.util.EmptyResult
import com.scelio.brainest.domain.util.Result
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.exceptions.RestException
import io.github.jan.supabase.exceptions.UnknownRestException
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Order
import io.ktor.client.network.sockets.ConnectTimeoutException
import io.ktor.client.network.sockets.SocketTimeoutException

class SupabaseChatServiceImpl(
    private val supabase: SupabaseClient
) : SupabaseChatService {

    override suspend fun syncChat(chat: Chat): EmptyResult<DataError.Remote> {
        return try {
            val response = supabase.from("chats").upsert(chat.toSupabaseDto())
            KermitLogger.info("Sync Successful")
            Result.Success(Unit)

        } catch (e: RestException) {
            KermitLogger.error("Supabase RestException: ${e.description} (Code: ${e.statusCode})")

            Result.Failure(e.toDataError())
        } catch (e: Exception) {
            e.printStackTrace()
            Result.Failure(e.toDataError())
        }
    }

    override suspend fun syncMessage(message: ChatMessage): EmptyResult<DataError.Remote> {
        return try {
            supabase.from("messages").upsert(message.toSupabaseDto())
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Failure(e.toDataError())
        }
    }

    override suspend fun fetchUserChats(userId: String): Result<List<Chat>, DataError.Remote> {
        return try {
            val chats = supabase.from("chats")
                .select {
                    filter { eq("user_id", userId) }
                    order(column = "last_activity_at", order = Order.DESCENDING)
                }
                .decodeList<SupabaseChatDto>() // This will fail if DTO doesn't match SQL
                .map { it.toDomain() }
            KermitLogger.info("Fetch Chats Detailed")

            Result.Success(chats)
        } catch (e: Exception) {
            KermitLogger.error("Fetch Chats Detailed Error", e)
            Result.Failure(e.toDataError())
        }
    }

    override suspend fun fetchChatMessages(
        chatId: String,
        limit: Int,
        offset: Int
    ): Result<List<ChatMessage>, DataError.Remote> {
        return try {
            val messages = supabase.from("messages")
                .select {
                    filter { eq("chat_id", chatId) }
                    order(column = "created_at", order = Order.DESCENDING)
                    limit(limit.toLong())
                    range(offset.toLong()..(offset + limit - 1).toLong())
                }
                .decodeList<com.scelio.brainest.data.mappers.SupabaseMessageDto>()
                .map { it.toDomain() }

            Result.Success(messages)
        } catch (e: Exception) {
            Result.Failure(e.toDataError())
        }
    }

    override suspend fun deleteChat(chatId: String): EmptyResult<DataError.Remote> {
        return try {
            supabase.from("chats").delete {
                filter { eq("id", chatId) }
            }
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Failure(e.toDataError())
        }
    }

    override suspend fun deleteMessage(messageId: String): EmptyResult<DataError.Remote> {
        return try {
            supabase.from("messages").delete {
                filter { eq("id", messageId) }
            }
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Failure(e.toDataError())
        }
    }

    override suspend fun updateChatTitle(
        chatId: String,
        title: String
    ): EmptyResult<DataError.Remote> {
        return try {
            supabase.from("chats").update({
                set("title", title)
            }) {
                filter { eq("id", chatId) }
            }
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Failure(e.toDataError())
        }
    }

    private fun Exception.toDataError(): DataError.Remote {
        return when (this) {
            is RestException -> {
                when (statusCode) {
                    400 -> DataError.Remote.BAD_REQUEST
                    401 -> DataError.Remote.UNAUTHORIZED
                    403 -> DataError.Remote.FORBIDDEN
                    404 -> DataError.Remote.NOT_FOUND
                    408 -> DataError.Remote.REQUEST_TIMEOUT
                    409 -> DataError.Remote.CONFLICT
                    413 -> DataError.Remote.PAYLOAD_TOO_LARGE
                    429 -> DataError.Remote.TOO_MANY_REQUESTS
                    in 500..502 -> DataError.Remote.SERVER_ERROR
                    503 -> DataError.Remote.SERVICE_UNAVAILABLE
                    else -> DataError.Remote.UNKNOWN
                }
            }

            is UnknownRestException -> DataError.Remote.UNKNOWN
            is ConnectTimeoutException -> DataError.Remote.SERVER_ERROR
            is SocketTimeoutException -> DataError.Remote.REQUEST_TIMEOUT
            else -> {
                if (this.message?.contains("Unable to resolve host") == true ||
                    this.message?.contains("Network is unreachable") == true
                ) {
                    DataError.Remote.NO_INTERNET
                } else {
                    DataError.Remote.UNKNOWN
                }
            }
        }
    }
}