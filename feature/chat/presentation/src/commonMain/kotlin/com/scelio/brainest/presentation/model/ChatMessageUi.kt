package com.scelio.brainest.presentation.model

import com.scelio.brainest.presentation.util.UiText

data class ChatMessageUi(
    val id: String,
    val content: String,
    val isFromUser: Boolean,
    val timestamp: UiText,
    val imageUrl: String? = null,
    val isLoading: Boolean = false,
    val error: String? = null,
    val metadata: MessageMetadataUi? = null
)