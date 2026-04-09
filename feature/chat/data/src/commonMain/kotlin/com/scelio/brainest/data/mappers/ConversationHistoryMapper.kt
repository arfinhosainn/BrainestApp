package com.scelio.brainest.data.mappers

import com.scelio.brainest.data.dto.ChatRequest
import com.scelio.brainest.data.dto.ContentDto
import com.scelio.brainest.data.dto.MessageDto
import com.scelio.brainest.data.dto.MessageRoles
import com.scelio.brainest.domain.models.ConversationHistory

fun ConversationHistory.toOpenAIRequest(
    model: String,
    systemPrompt: String? = null,
    stream: Boolean = false
): ChatRequest {
    val messages = this.messages.toOpenAIMessages()

    return ChatRequest(
        model = model,
        input = messages,
        instructions = systemPrompt,
        stream = stream
    )
}
