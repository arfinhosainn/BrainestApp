package com.scelio.brainest.domain.models

data class SendMessageRequest(
    val chatId: String,
    val userId: String,
    val content: String,

    // Legacy single image (for backward compatibility)
    val imageUrl: String? = null,

    // NEW: Support for multiple images (as byte arrays to be uploaded)
    val images: List<ByteArray>? = null,

    // NEW: Support for image URLs (if already uploaded)
    val imageUrls: List<String>? = null,

    // Document support
    val fileId: String? = null,
    val fileName: String? = null,
    val fileContent: ByteArray? = null
) {
    /**
     * Helper to get all image URLs (combines single and multiple)
     */
    fun getAllImageUrls(): List<String> {
        val urls = mutableListOf<String>()
        imageUrl?.let { urls.add(it) }
        imageUrls?.let { urls.addAll(it) }
        return urls
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false

        other as SendMessageRequest

        if (chatId != other.chatId) return false
        if (userId != other.userId) return false
        if (content != other.content) return false
        if (imageUrl != other.imageUrl) return false
        if (images != other.images) return false
        if (imageUrls != other.imageUrls) return false
        if (fileId != other.fileId) return false
        if (fileName != other.fileName) return false
        if (!fileContent.contentEquals(other.fileContent)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = chatId.hashCode()
        result = 31 * result + userId.hashCode()
        result = 31 * result + content.hashCode()
        result = 31 * result + (imageUrl?.hashCode() ?: 0)
        result = 31 * result + (images?.hashCode() ?: 0)
        result = 31 * result + (imageUrls?.hashCode() ?: 0)
        result = 31 * result + (fileId?.hashCode() ?: 0)
        result = 31 * result + (fileName?.hashCode() ?: 0)
        result = 31 * result + (fileContent?.contentHashCode() ?: 0)
        return result
    }
}