package com.scelio.brainest.flashcards.domain

data class AudioTranscriptionResult(
    val text: String,
    val language: String? = null,
    val durationSeconds: Double? = null
)
