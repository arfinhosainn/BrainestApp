package com.scelio.brainest.flashcards.domain

import kotlin.time.Instant

data class FlashcardProgress(
    val id: String,
    val deckId: String,
    val cardId: String,
    val swipesCount: Int,
    val lastResult: FlashcardResult?,
    val updatedAt: Instant
)

data class QuizProgress(
    val id: String,
    val deckId: String,
    val totalQuestions: Int,
    val answeredQuestions: Int,
    val correctAnswers: Int,
    val completedAt: Instant
)

data class DeckStudyProgressSummary(
    val deckId: String,
    val flashcardsSwiped: Int,
    val quizzesCompleted: Int
)
