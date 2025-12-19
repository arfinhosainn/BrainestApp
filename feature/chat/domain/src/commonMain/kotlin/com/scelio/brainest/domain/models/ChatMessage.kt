package com.scelio.brainest.domain.models

import kotlin.time.Instant


data class ChatMessage(
    val id: String,
    val chatId: String,
    val content: String,
    val role: String, // "user", "assistant", "system"
    val createdAt: Instant,
    val senderId: String,

    // Legacy single image support (for backward compatibility)
    val imageUrl: String? = null,

    // NEW: Multiple images support
    val imageUrls: List<String>? = null,

    // Document support
    val fileId: String? = null,
    val fileName: String? = null,

    val metadata: MessageMetadata? = null
)