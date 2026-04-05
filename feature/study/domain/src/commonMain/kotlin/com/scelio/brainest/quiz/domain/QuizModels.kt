package com.scelio.brainest.quiz.domain

data class QuizQuestion(
    val id: String,
    val deckId: String,
    val question: String,
    val options: List<String>,
    val correctIndex: Int,
    val orderIndex: Int
)

data class QuizQuestionInput(
    val question: String,
    val options: List<String>,
    val correctIndex: Int,
    val orderIndex: Int
)
