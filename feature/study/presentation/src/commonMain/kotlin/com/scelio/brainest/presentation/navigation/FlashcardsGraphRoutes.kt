package com.scelio.brainest.presentation.navigation

import kotlinx.serialization.Serializable

@Serializable
enum class FlashcardsUploadIntent {
    AUDIO,
    DOCUMENT
}

sealed interface FlashcardsGraphRoutes {
    @Serializable data object Graph : FlashcardsGraphRoutes
    @Serializable
    data class Generate(
        val uploadIntent: FlashcardsUploadIntent? = null,
        val uploadRequestId: Int = 0
    ) : FlashcardsGraphRoutes
    @Serializable data class Session(val deckId: String) : FlashcardsGraphRoutes
    @Serializable data class StudySetDetail(val deckId: String, val promptGeneration: Boolean = false) : FlashcardsGraphRoutes
    @Serializable data class QuizSession(val deckId: String) : FlashcardsGraphRoutes
    @Serializable data object AudioRecording : FlashcardsGraphRoutes
}
