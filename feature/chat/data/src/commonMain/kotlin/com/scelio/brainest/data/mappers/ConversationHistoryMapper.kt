package com.scelio.brainest.data.mappers

import com.scelio.brainest.data.dto.ContentDto
import com.scelio.brainest.data.dto.MessageDto
import com.scelio.brainest.data.dto.MessageRoles
import com.scelio.brainest.data.dto.ResponseRequest
import com.scelio.brainest.domain.models.ConversationHistory

fun ConversationHistory.toOpenAIRequest(
    model: String,
    systemPrompt: String? = null
): ResponseRequest {
    val messages = mutableListOf<MessageDto>()

    // Add system prompt if provided
    systemPrompt?.let {
        messages.add(
            MessageDto(
                role = MessageRoles.SYSTEM,
                content = listOf(ContentDto.Text(text = it))
            )
        )
    }

    // Add conversation messages
    messages.addAll(this.messages.toOpenAIMessages())

    return ResponseRequest(
        model = model,
        input = messages
    )
}