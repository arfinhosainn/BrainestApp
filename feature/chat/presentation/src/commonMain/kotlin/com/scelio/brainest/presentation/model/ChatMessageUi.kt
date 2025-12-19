package com.scelio.brainest.presentation.model

import com.scelio.brainest.presentation.util.UiText

data class ChatMessageUi(
    val id: String,
    val content: String,
    val isFromUser: Boolean,
    val timestamp: UiText,

    // Legacy single image
    val imageUrl: String? = null,

    // NEW: Multiple images
    val imageUrls: List<String>? = null,

    // Document info
    val fileId: String? = null,
    val fileName: String? = null,

    val isLoading: Boolean = false,
    val error: String? = null,
    val metadata: MessageMetadataUi? = null
) {
    /**
     * Helper to get all image URLs (combines single and multiple)
     */
    fun getAllImageUrls(): List<String> {
        val urls = mutableListOf<String>()
        imageUrl?.let { urls.add(it) }
        imageUrls?.let { urls.addAll(it) }
        return urls.distinct()
    }

    val hasImages: Boolean
        get() = getAllImageUrls().isNotEmpty()

    val hasDocument: Boolean
        get() = fileId != null || fileName != null
}
