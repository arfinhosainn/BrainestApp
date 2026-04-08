package com.scelio.brainest.quiz.domain

import com.scelio.brainest.domain.util.DataError
import com.scelio.brainest.domain.util.EmptyResult
import com.scelio.brainest.domain.util.Result
import com.scelio.brainest.flashcards.domain.QuizProgress
import kotlin.time.Instant

interface QuizRepository {
    suspend fun addQuizQuestions(
        deckId: String,
        questions: List<QuizQuestionInput>
    ): EmptyResult<DataError.Remote>

    suspend fun getQuizQuestions(
        deckId: String
    ): Result<List<QuizQuestion>, DataError.Remote>

    suspend fun recordQuizCompletion(
        deckId: String,
        totalQuestions: Int,
        answeredQuestions: Int,
        correctAnswers: Int,
        completedAt: Instant = kotlin.time.Clock.System.now()
    ): EmptyResult<DataError.Remote>

    suspend fun getQuizProgress(
        deckId: String
    ): Result<List<QuizProgress>, DataError.Remote>

    suspend fun syncDeckQuizQuestions(
        deckId: String
    ): EmptyResult<DataError.Remote>
}
