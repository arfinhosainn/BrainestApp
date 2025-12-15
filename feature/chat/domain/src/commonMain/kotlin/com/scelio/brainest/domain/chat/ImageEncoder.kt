package com.scelio.brainest.domain.chat

expect object ImageEncoder {
    suspend fun encodeToBase64(bytes: ByteArray, mimeType: String = "image/jpeg"): String
}