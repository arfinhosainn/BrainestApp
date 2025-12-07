package com.scelio.brainest.presentation.model

data class ChatDetailUi(
    val chatId: String,
    val title: String,
    val model: String,
    val messages: List<ChatMessageUi>,
    val isLoading: Boolean = false,
    val error: String? = null
)