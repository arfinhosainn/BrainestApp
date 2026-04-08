package com.scelio.brainest.flashcards.database.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "flashcard_progress",
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
        Index(value = ["cardId"]),
        Index(value = ["updatedAt"])
    ]
)
data class FlashcardProgressEntity(
    @PrimaryKey
    val id: String,
    val deckId: String,
    val cardId: String,
    val swipesCount: Int,
    val lastResult: String?,
    val updatedAt: Long
)
