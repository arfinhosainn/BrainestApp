package com.scelio.brainest.domain.util

/**
 * Validates a document for upload based on size and MIME type.
 *
 * Supported formats: PDF, DOCX, TXT
 * Maximum size: 20MB
 *
 * @param fileName The document file name (used for extension check)
 * @param mimeType The document MIME type
 * @param sizeBytes The document size in bytes
 * @return Error message if invalid, null if valid
 */
fun validateDocumentForUpload(
    fileName: String,
    mimeType: String,
    sizeBytes: Int
): String? {
    val maxBytes = 20 * 1024 * 1024 // 20MB
    if (sizeBytes > maxBytes) {
        return "Document is too large. Max size is 20MB."
    }

    val normalizedFileName = fileName.lowercase()
    val normalizedMimeType = mimeType.lowercase()
    val isSupported = normalizedMimeType in SUPPORTED_MIME_TYPES ||
        normalizedFileName.endsWith(".pdf") ||
        normalizedFileName.endsWith(".docx") ||
        normalizedFileName.endsWith(".txt")

    return if (isSupported) null else "Unsupported file type. Use PDF, DOCX, or TXT."
}

private val SUPPORTED_MIME_TYPES = setOf(
    "application/pdf",
    "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
    "text/plain"
)
