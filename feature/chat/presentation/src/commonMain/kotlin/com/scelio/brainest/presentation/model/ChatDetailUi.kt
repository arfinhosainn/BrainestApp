package com.scelio.brainest.presentation.model

import androidx.compose.runtime.Stable

@Stable
data class ChatDetailUi(
    val chatId: String,
    val title: String,
    val model: String,
    val messages: List<ChatMessageUi>,
    val isLoading: Boolean = false,
    val error: String? = null
)