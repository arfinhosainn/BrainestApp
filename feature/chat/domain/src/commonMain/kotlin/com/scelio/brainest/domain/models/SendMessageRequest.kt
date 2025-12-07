package com.scelio.brainest.domain.models

data class SendMessageRequest(
    val chatId: String,
    val userId: String,
    val content: String,
    val imageUrl: String? = null,
    val fileId: String? = null
)