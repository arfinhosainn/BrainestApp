package com.scelio.brainest.domain.models

data class ConversationHistory(
    val chatId: String,
    val messages: List<ChatMessage>
) {
    fun getRecentMessages(limit: Int = 20): List<ChatMessage> {
        return messages.takeLast(limit)
    }
}