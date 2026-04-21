package com.scelio.brainest.flashcards.domain

import kotlin.time.Instant

data class StudySetSummary(
    val id: String,
    val title: String,
    val createdAt: Instant,
    val flashcardsCount: Int,
    val quizCount: Int
    ,
    val sourceFilename: String? = null
)
