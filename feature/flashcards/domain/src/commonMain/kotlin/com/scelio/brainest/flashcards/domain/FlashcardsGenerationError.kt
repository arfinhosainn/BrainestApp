package com.scelio.brainest.flashcards.domain

import com.scelio.brainest.domain.util.DataError
import com.scelio.brainest.domain.util.Error

sealed interface FlashcardsGenerationError : Error {
    data class Remote(val error: DataError.Remote) : FlashcardsGenerationError
    data class Parse(val message: String) : FlashcardsGenerationError
    data class Empty(val message: String) : FlashcardsGenerationError
}
