package com.scelio.brainest.data.mappers

import com.scelio.brainest.data.dto.MessageRoles
import com.scelio.brainest.data.dto.Response
import com.scelio.brainest.domain.models.ChatMessage
import com.scelio.brainest.domain.models.MessageMetadata
import kotlin.time.Instant

fun Response.toDomainMessage(
    chatId: String,
    messageId: String
): ChatMessage {
    return ChatMessage(
        id = messageId,
        chatId = chatId,
        content = outputText ?: "",
        role = MessageRoles.ASSISTANT,
        createdAt = Instant.fromEpochSeconds(created),
        senderId = "assistant",
        metadata = MessageMetadata(
            model = model,
            tokensUsed = usage?.totalTokens,
            openAIResponseId = id
        )
    )
}