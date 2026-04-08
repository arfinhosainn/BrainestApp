package com.scelio.brainest.presentation.flashcards

import com.scelio.brainest.domain.util.Error

sealed interface DocumentTextExtractionError : Error {
    data class Unsupported(val message: String) : DocumentTextExtractionError
    data class Failed(val message: String) : DocumentTextExtractionError
}
