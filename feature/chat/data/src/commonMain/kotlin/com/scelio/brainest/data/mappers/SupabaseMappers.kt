package com.scelio.brainest.data.mappers


import com.scelio.brainest.domain.models.Chat
import com.scelio.brainest.domain.models.ChatMessage
import kotlinx.serialization.Serializable
import kotlin.time.Instant

@Serializable
data class SupabaseChatDto(
    val id: String,
    val userId: String,
    val title: String,
    val model: String,
    val systemPrompt: String? = null,
    val createdAt: Long,
    val latActivityAt: Long,
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
    val imageUrl: String? = null,
    val fileId: String? = null,
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
    latActivityAt = lastActivityAt.toEpochMilliseconds(),
    messageCount= messageCount
)

fun ChatMessage.toSupabaseDto() = SupabaseMessageDto(
    id = id,
    chatId = chatId,
    content = content,
    role = role,
    createdAt = createdAt.toEpochMilliseconds(),
    senderId = senderId,
    imageUrl = imageUrl,
    fileId = fileId,
    metadata = metadata?.let {
        """{"model":"${it.model}","tokensUsed":${it.tokensUsed},"openAIResponseId":"${it.openAIResponseId}"}"""
    }
)

// Supabase to Domain
fun SupabaseChatDto.toDomain() = Chat(
    id = id,
    userId = userId,
    title = title,
    model = model,
    systemPrompt = systemPrompt,
    createdAt = Instant.fromEpochMilliseconds(createdAt),
    lastActivityAt = Instant.fromEpochMilliseconds(latActivityAt),
    messageCount = messageCount
)

fun SupabaseMessageDto.toDomain() = ChatMessage(
    id = id,
    chatId = chatId,
    content = content,
    role = role,
    createdAt = Instant.fromEpochMilliseconds(createdAt),
    senderId = senderId,
    imageUrl = imageUrl,
    fileId = fileId,
    metadata = null // TODO: Parse JSON if needed
)