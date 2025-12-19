package com.scelio.brainest.data.mappers


import com.scelio.brainest.domain.models.Chat
import com.scelio.brainest.domain.models.ChatMessage
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlin.time.Instant

private val json = Json { ignoreUnknownKeys = true }

@Serializable
data class SupabaseChatDto(
    val id: String,
    val userId: String,
    val title: String,
    val model: String,
    val systemPrompt: String? = null,
    val createdAt: Long,
    val lastActivityAt: Long,
    val messageCount: Int
)

@Serializable
data class SupabaseMessageDto(
    val id: String,
    val chatId: String,
    val content: String,
    val role: String,
    val createdAt: Long,
    val senderId: String,

    // Legacy single image
    val imageUrl: String? = null,

    // NEW: Multiple images as JSON array
    val imageUrls: String? = null,  // JSON: ["url1", "url2"]

    val fileId: String? = null,

    // NEW: File name
    val fileName: String? = null,

    val metadata: String? = null
)

// Domain to Supabase
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
    // Convert imageUrls list to JSON string
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

// Supabase to Domain
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
    // Parse imageUrls JSON if present
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