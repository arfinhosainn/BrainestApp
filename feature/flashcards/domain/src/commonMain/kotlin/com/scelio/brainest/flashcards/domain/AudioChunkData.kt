package com.scelio.brainest.flashcards.domain

data class AudioChunkData(
    val bytes: ByteArray,
    val mimeType: String,
    val fileName: String,
    val language: String? = null
)
