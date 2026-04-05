package com.scelio.brainest.quiz.domain

import com.scelio.brainest.domain.util.Result

interface QuizGenerationService {
    suspend fun generateQuizFromText(
        prompt: String,
        count: Int,
        multipleChoice: Boolean
    ): Result<List<QuizQuestionInput>, QuizGenerationError>

    suspend fun generateQuizFromFile(
        fileId: String,
        count: Int,
        multipleChoice: Boolean
    ): Result<List<QuizQuestionInput>, QuizGenerationError>
}
