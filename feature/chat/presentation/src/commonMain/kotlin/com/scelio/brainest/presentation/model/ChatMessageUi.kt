package com.scelio.brainest.presentation.model

data class ChatMessageUi(
    val id: String,
    val content: String,
    val isFromUser: Boolean,
    val timestamp: String, // Formatted: "10:30 AM" or "Yesterday"
    val imageUrl: String? = null,
    val fileId: String? = null,
    val status: MessageStatus = MessageStatus.SENT,
    val tokensUsed: Int? = null,
    val isLoading: Boolean = false
)
