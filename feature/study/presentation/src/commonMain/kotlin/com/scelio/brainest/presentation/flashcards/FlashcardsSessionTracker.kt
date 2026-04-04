package com.scelio.brainest.presentation.flashcards

import com.scelio.brainest.flashcards.domain.FlashcardResult
import com.scelio.brainest.flashcards.domain.SessionRecordInput
import com.scelio.brainest.flashcards.domain.SessionSummary
import kotlin.time.Clock
import kotlin.time.Instant

class FlashcardsSessionTracker {
    private var knownCount = 0
    private var unknownCount = 0
    private var totalSwiped = 0
    private val records = mutableListOf<SessionRecordInput>()

    fun markKnown(cardId: String, respondedAt: Instant = Clock.System.now()) {
        knownCount++
        totalSwiped++
        records.add(
            SessionRecordInput(
                flashcardId = cardId,
                result = FlashcardResult.KNOWN,
                respondedAt = respondedAt
            )
        )
    }

    fun markUnknown(cardId: String, respondedAt: Instant = Clock.System.now()) {
        unknownCount++
        totalSwiped++
        records.add(
            SessionRecordInput(
                flashcardId = cardId,
                result = FlashcardResult.UNKNOWN,
                respondedAt = respondedAt
            )
        )
    }

    fun buildSummary(endedAt: Instant = Clock.System.now()): SessionSummary {
        return SessionSummary(
            cardsKnown = knownCount,
            cardsUnknown = unknownCount,
            totalSwiped = totalSwiped,
            endedAt = endedAt
        )
    }

    fun snapshotRecords(): List<SessionRecordInput> = records.toList()
}
