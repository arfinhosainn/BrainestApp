package com.scelio.brainest.flashcards.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "study_decks")
data class DeckEntity(
    @PrimaryKey
    val id: String,
    val userId: String,
    val title: String,
    val sourceFilename: String?,
    val totalCards: Int,
    val createdAt: Long,
    val isPendingSync: Boolean
)
