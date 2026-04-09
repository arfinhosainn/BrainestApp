package com.scelio.brainest.domain.models

sealed interface SendMessageStreamEvent {
    data class Started(
        val userMessage: ChatMessage,
        val assistantMessageId: String
    ) : SendMessageStreamEvent

    data class AssistantPartial(
        val messageId: String,
        val content: String
    ) : SendMessageStreamEvent

    data class Completed(
        val message: ChatMessage
    ) : SendMessageStreamEvent
}
