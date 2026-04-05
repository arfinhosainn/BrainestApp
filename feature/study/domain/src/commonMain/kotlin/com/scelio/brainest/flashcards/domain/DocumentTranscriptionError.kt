package com.scelio.brainest.flashcards.domain

import com.scelio.brainest.domain.util.DataError
import com.scelio.brainest.domain.util.Error

sealed interface DocumentTranscriptionError : Error {
    data class Remote(val error: DataError.Remote) : DocumentTranscriptionError
    data class Empty(val message: String) : DocumentTranscriptionError
    data class Invalid(val message: String) : DocumentTranscriptionError
}
