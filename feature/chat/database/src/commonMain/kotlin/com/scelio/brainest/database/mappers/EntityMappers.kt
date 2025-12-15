package com.scelio.brainest.database.mappers

import com.scelio.brainest.database.entities.ChatEntity
import com.scelio.brainest.database.entities.MessageEntity
import com.scelio.brainest.domain.models.Chat
import com.scelio.brainest.domain.models.ChatMessage
import com.scelio.brainest.domain.models.MessageMetadata
import kotlin.time.Instant

fun Chat.toEntity(): ChatEntity {
    return ChatEntity(
        id = id,
        userId = userId,
        title = title,
        model = model,
        systemPrompt = systemPrompt,
        createdAt = createdAt.toEpochMilliseconds(),
        lastActivityAt = lastActivityAt.toEpochMilliseconds(),
        messageCount = messageCount
    )
}

fun ChatMessage.toEntity(): MessageEntity {
    return MessageEntity(
        id = id,
        chatId = chatId,
        content = content,
        role = role,
        createdAt = createdAt.toEpochMilliseconds(),
        senderId = senderId,
        imageUrl = imageUrl,
        fileId = fileId,
        model = metadata?.model,
        tokensUsed = metadata?.tokensUsed,
        openAIResponseId = metadata?.openAIResponseId
    )
}


fun ChatEntity.toDomain(): Chat {
    return Chat(
        id = id,
        userId = userId,
        title = title,
        model = model,
        systemPrompt = systemPrompt,
        createdAt = Instant.fromEpochMilliseconds(createdAt),
        lastActivityAt = Instant.fromEpochMilliseconds(lastActivityAt),
        messageCount = messageCount
    )
}

fun MessageEntity.toDomain(): ChatMessage {
    return ChatMessage(
        id = id,
        chatId = chatId,
        content = content,
        role = role,
        createdAt = Instant.fromEpochMilliseconds(createdAt),
        senderId = senderId,
        imageUrl = imageUrl,
        fileId = fileId,
        metadata = if (model != null || tokensUsed != null || openAIResponseId != null) {
            MessageMetadata(
                model = model,
                tokensUsed = tokensUsed,
                openAIResponseId = openAIResponseId
            )
        } else null
    )
}