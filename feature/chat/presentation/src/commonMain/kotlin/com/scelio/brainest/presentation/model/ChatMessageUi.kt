package com.scelio.brainest.presentation.model

import androidx.compose.runtime.Stable
import com.scelio.brainest.presentation.util.UiText

@Stable
data class ChatMessageUi(
    val id: String,
    val content: String,
    val isFromUser: Boolean,
    val timestamp: UiText,

    val imageUrl: String? = null,
    val imageUrls: List<String>? = null,

    val fileId: String? = null,
    val fileName: String? = null,

    val isLoading: Boolean = false,
    val error: String? = null,
    val metadata: MessageMetadataUi? = null
) {

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
