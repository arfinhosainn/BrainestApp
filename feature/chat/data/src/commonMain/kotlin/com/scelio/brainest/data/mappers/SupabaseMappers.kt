package com.scelio.brainest.data.mappers


import com.scelio.brainest.domain.models.Chat
import com.scelio.brainest.domain.models.ChatMessage
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlin.time.Instant

private val json = Json { ignoreUnknownKeys = true }

@Serializable
data class SupabaseChatDto(
    val id: String,
    @SerialName("user_id")
    val userId: String,
    val title: String,
    val model: String,
    @SerialName("system_prompt")
    val systemPrompt: String? = null,
    @SerialName("created_at")
    val createdAt: Long,
    @SerialName("last_activity_at")
    val lastActivityAt: Long,
    @SerialName("message_count")
    val messageCount: Int
)

@Serializable
data class SupabaseMessageDto(
    val id: String,
    @SerialName("chat_id") val chatId: String,
    val content: String,
    val role: String,
    @SerialName("created_at") val createdAt: Long,
    @SerialName("sender_id") val senderId: String,
    @SerialName("image_url") val imageUrl: String? = null,
    @SerialName("image_urls") val imageUrls: String? = null,
    @SerialName("file_id") val fileId: String? = null,
    @SerialName("file_name") val fileName: String? = null,
    val metadata: String? = null
)



fun Chat.toSupabaseDto() = SupabaseChatDto(
    id = id,
    userId = userId,
    title = title,
    model = model,
    systemPrompt = systemPrompt,
    createdAt = createdAt.toEpochMilliseconds(),
    lastActivityAt = lastActivityAt.toEpochMilliseconds(),
    messageCount = messageCount
)

fun ChatMessage.toSupabaseDto(): SupabaseMessageDto {
    val imageUrlsJson = imageUrls?.let { urls ->
        try {
            json.encodeToString(urls)
        } catch (e: Exception) {
            null
        }
    }

    return SupabaseMessageDto(
        id = id,
        chatId = chatId,
        content = content,
        role = role,
        createdAt = createdAt.toEpochMilliseconds(),
        senderId = senderId,
        imageUrl = imageUrl,
        imageUrls = imageUrlsJson,
        fileId = fileId,
        fileName = fileName,
        metadata = metadata?.let {
            """{"model":"${it.model}","tokensUsed":${it.tokensUsed},"openAIResponseId":"${it.openAIResponseId}"}"""
        }
    )
}

fun SupabaseChatDto.toDomain() = Chat(
    id = id,
    userId = userId,
    title = title,
    model = model,
    systemPrompt = systemPrompt,
    createdAt = Instant.fromEpochMilliseconds(createdAt),
    lastActivityAt = Instant.fromEpochMilliseconds(lastActivityAt),
    messageCount = messageCount
)

fun SupabaseMessageDto.toDomain(): ChatMessage {
    val parsedImageUrls = imageUrls?.let {
        try {
            json.decodeFromString<List<String>>(it)
        } catch (e: Exception) {
            null
        }
    }

    return ChatMessage(
        id = id,
        chatId = chatId,
        content = content,
        role = role,
        createdAt = Instant.fromEpochMilliseconds(createdAt),
        senderId = senderId,
        imageUrl = imageUrl,
        imageUrls = parsedImageUrls,
        fileId = fileId,
        fileName = fileName,
        metadata = null // TODO: Parse JSON if needed
    )
}