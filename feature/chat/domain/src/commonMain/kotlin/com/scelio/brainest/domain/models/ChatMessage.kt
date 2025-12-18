package com.scelio.brainest.domain.models

import kotlinx.datetime.Instant


data class ChatMessage(
    val id: String,
    val chatId: String,
    val content: String,
    val role: String, // "user", "assistant", "system"
    val createdAt: kotlin.time.Instant,
    val senderId: String,
    val imageUrl: String? = null,
    val fileId: String? = null,
    val metadata: MessageMetadata? = null
)