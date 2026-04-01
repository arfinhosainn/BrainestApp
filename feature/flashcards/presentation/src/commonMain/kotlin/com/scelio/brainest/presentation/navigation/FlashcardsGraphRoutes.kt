package com.scelio.brainest.presentation.navigation

import kotlinx.serialization.Serializable

sealed interface FlashcardsGraphRoutes {
    @Serializable data object Graph : FlashcardsGraphRoutes
    @Serializable data object Generate : FlashcardsGraphRoutes
    @Serializable data class Session(val deckId: String) : FlashcardsGraphRoutes
    @Serializable data object AudioRecording : FlashcardsGraphRoutes
}
