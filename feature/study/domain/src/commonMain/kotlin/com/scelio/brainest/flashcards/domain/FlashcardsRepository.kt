package com.scelio.brainest.flashcards.domain

import com.scelio.brainest.domain.util.DataError
import com.scelio.brainest.domain.util.EmptyResult
import com.scelio.brainest.domain.util.Result
import kotlin.time.Instant

interface FlashcardsRepository {
    suspend fun createDeck(
        userId: String,
        title: String,
        sourceFilename: String?
    ): Result<Deck, DataError.Remote>

    suspend fun addCards(
        deckId: String,
        cards: List<FlashcardInput>
    ): EmptyResult<DataError.Remote>

    suspend fun listDecks(
        userId: String
    ): Result<List<Deck>, DataError.Remote>

    suspend fun listStudySessions(
        userId: String
    ): Result<List<StudySession>, DataError.Remote>

    suspend fun getDeckCards(
        deckId: String
    ): Result<List<Flashcard>, DataError.Remote>

    suspend fun startSession(
        userId: String,
        deckId: String,
        startedAt: Instant = kotlin.time.Clock.System.now()
    ): Result<StudySession, DataError.Remote>

    suspend fun finishSession(
        sessionId: String,
        summary: SessionSummary,
        records: List<SessionRecordInput>
    ): EmptyResult<DataError.Remote>
}
