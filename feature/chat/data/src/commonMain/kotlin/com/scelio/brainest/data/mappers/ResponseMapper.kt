package com.scelio.brainest.data.mappers

import com.scelio.brainest.data.dto.ChatResult
import com.scelio.brainest.data.dto.MessageRoles
import com.scelio.brainest.domain.models.ChatMessage
import com.scelio.brainest.domain.models.MessageMetadata
import kotlin.time.Instant


fun ChatResult.toDomainMessage(chatId: String, messageId: String): ChatMessage {
    val text = extractedOutputText()
    return ChatMessage(
        id = messageId,
        chatId = chatId,
        content = text,
        role = MessageRoles.ASSISTANT,
        createdAt = Instant.fromEpochSeconds(created ?: 0L),
        senderId = "assistant",
        metadata = MessageMetadata(
            model = model,
            tokensUsed = usage?.totalTokens,
            openAIResponseId = id
        )
    )
}