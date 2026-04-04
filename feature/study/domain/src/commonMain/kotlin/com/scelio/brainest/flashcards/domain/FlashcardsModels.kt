package com.scelio.brainest.flashcards.domain

import kotlin.time.Instant

data class Deck(
    val id: String,
    val userId: String,
    val title: String,
    val sourceFilename: String?,
    val totalCards: Int,
    val createdAt: Instant
)

data class Flashcard(
    val id: String,
    val deckId: String,
    val front: String,
    val back: String,
    val orderIndex: Int
)

data class StudySession(
    val id: String,
    val userId: String,
    val deckId: String,
    val cardsKnown: Int,
    val cardsUnknown: Int,
    val totalSwiped: Int,
    val startedAt: Instant,
    val endedAt: Instant?
)

data class SessionRecord(
    val id: String,
    val sessionId: String,
    val flashcardId: String,
    val result: FlashcardResult,
    val respondedAt: Instant
)

data class FlashcardInput(
    val front: String,
    val back: String,
    val orderIndex: Int
)

data class SessionRecordInput(
    val flashcardId: String,
    val result: FlashcardResult,
    val respondedAt: Instant
)

data class SessionSummary(
    val cardsKnown: Int,
    val cardsUnknown: Int,
    val totalSwiped: Int,
    val endedAt: Instant
)

enum class FlashcardResult(val dbValue: String) {
    KNOWN("known"),
    UNKNOWN("unknown");

    companion object {
        fun fromDbValue(value: String): FlashcardResult {
            return entries.firstOrNull { it.dbValue == value } ?: UNKNOWN
        }
    }
}
