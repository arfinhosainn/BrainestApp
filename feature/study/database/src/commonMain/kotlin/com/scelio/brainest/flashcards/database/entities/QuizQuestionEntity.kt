package com.scelio.brainest.flashcards.database.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "study_quiz_questions",
    foreignKeys = [
        ForeignKey(
            entity = DeckEntity::class,
            parentColumns = ["id"],
            childColumns = ["deckId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["deckId"]),
        Index(value = ["deckId", "orderIndex"])
    ]
)
data class QuizQuestionEntity(
    @PrimaryKey
    val id: String,
    val deckId: String,
    val question: String,
    val optionsJson: String,
    val correctIndex: Int,
    val orderIndex: Int,
    val isPendingSync: Boolean
)
