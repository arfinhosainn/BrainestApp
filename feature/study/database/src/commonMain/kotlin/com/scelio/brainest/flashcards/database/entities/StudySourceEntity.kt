package com.scelio.brainest.flashcards.database.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "study_sources",
    foreignKeys = [
        ForeignKey(
            entity = DeckEntity::class,
            parentColumns = ["id"],
            childColumns = ["deckId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["deckId"], unique = true)
    ]
)
data class StudySourceEntity(
    @PrimaryKey
    val id: String,
    val deckId: String,
    val sourceType: String,
    val sourceText: String?,
    val sourceFileId: String?,
    val sourceFilename: String?,
    val createdAt: Long,
    val smartNotes: String?,
    val isPendingSync: Boolean
)
