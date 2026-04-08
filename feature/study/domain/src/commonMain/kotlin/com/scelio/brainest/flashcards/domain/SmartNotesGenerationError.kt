package com.scelio.brainest.flashcards.domain

import com.scelio.brainest.domain.util.DataError
import com.scelio.brainest.domain.util.Error

sealed interface SmartNotesGenerationError : Error {
    data class Remote(val error: DataError.Remote) : SmartNotesGenerationError
    data class Empty(val message: String) : SmartNotesGenerationError
    data class Invalid(val message: String) : SmartNotesGenerationError
}
