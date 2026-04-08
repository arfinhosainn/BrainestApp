package com.scelio.brainest.flashcards.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "study_sessions_local",
    foreignKeys = [
        ForeignKey(
            entity = DeckEntity::class,
            parentColumns = ["id"],
            childColumns = ["deckId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["userId"]),
        Index(value = ["deckId"]),
        Index(value = ["startedAt"]),
        Index(value = ["endedAt"])
    ]
)
data class StudySessionEntity(
    @PrimaryKey
    val id: String,
    val userId: String,
    val deckId: String,
    val cardsKnown: Int,
    val cardsUnknown: Int,
    val totalSwiped: Int,
    val startedAt: Long,
    val endedAt: Long?,
    @ColumnInfo(defaultValue = "0")
    val isPendingSync: Boolean
)
