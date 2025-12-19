package com.scelio.brainest.presentation.chat_detail

data class UploadedDocument(
    val fileId: String,
    val fileName: String,
    val content: ByteArray,
    val mimeType: String
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is UploadedDocument) return false
        return fileId == other.fileId
    }

    override fun hashCode(): Int = fileId.hashCode()
}