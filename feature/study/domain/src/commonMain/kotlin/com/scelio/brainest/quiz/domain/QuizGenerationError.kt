package com.scelio.brainest.quiz.domain

import com.scelio.brainest.domain.util.DataError
import com.scelio.brainest.domain.util.Error

sealed interface QuizGenerationError : Error {
    data class Parse(val message: String) : QuizGenerationError
    data class Empty(val message: String) : QuizGenerationError
    data class Remote(val error: DataError.Remote) : QuizGenerationError
}
