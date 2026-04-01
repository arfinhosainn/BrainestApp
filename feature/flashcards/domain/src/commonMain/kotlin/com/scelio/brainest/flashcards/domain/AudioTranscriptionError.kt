package com.scelio.brainest.flashcards.domain

import com.scelio.brainest.domain.util.DataError
import com.scelio.brainest.domain.util.Error

sealed interface AudioTranscriptionError : Error {
    data class Remote(val error: DataError.Remote) : AudioTranscriptionError
    data class Empty(val message: String) : AudioTranscriptionError
    data class Invalid(val message: String) : AudioTranscriptionError
}
