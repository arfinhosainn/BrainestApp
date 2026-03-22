package com.scelio.brainest.flashcards.data

import com.scelio.brainest.domain.logging.BrainestLogger
import com.scelio.brainest.domain.util.DataError
import com.scelio.brainest.domain.util.EmptyResult
import com.scelio.brainest.domain.util.Result
import com.scelio.brainest.flashcards.domain.Deck
import com.scelio.brainest.flashcards.domain.Flashcard
import com.scelio.brainest.flashcards.domain.FlashcardInput
import com.scelio.brainest.flashcards.domain.FlashcardsRepository
import com.scelio.brainest.flashcards.domain.SessionRecordInput
import com.scelio.brainest.flashcards.domain.SessionSummary
import com.scelio.brainest.flashcards.domain.StudySession
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.exceptions.RestException
import io.github.jan.supabase.exceptions.UnknownRestException
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Order
import io.ktor.client.network.sockets.ConnectTimeoutException
import io.ktor.client.network.sockets.SocketTimeoutException
import kotlin.time.Clock
import kotlin.time.Instant
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid
import kotlinx.datetime.Instant as KxInstant

@OptIn(ExperimentalUuidApi::class)
class FlashcardsRepositoryImpl(
    private val supabase: SupabaseClient,
    private val logger: BrainestLogger
) : FlashcardsRepository {

    override suspend fun createDeck(
        userId: String,
        title: String,
        sourceFilename: String?
    ): Result<Deck, DataError.Remote> {
        val now = Clock.System.now()
        val deck = Deck(
            id = Uuid.random().toString(),
            userId = userId,
            title = title,
            sourceFilename = sourceFilename,
            totalCards = 0,
            createdAt = now
        )

        return try {
            supabase.from("decks").upsert(deck.toSupabaseDto())
            Result.Success(deck)
        } catch (e: Exception) {
            logger.error("Failed to create deck", e)
            Result.Failure(e.toDataError())
        }
    }

    override suspend fun addCards(
        deckId: String,
        cards: List<FlashcardInput>
    ): EmptyResult<DataError.Remote> {
        if (cards.isEmpty()) {
            return Result.Success(Unit)
        }

        val dtoList = cards.map { card ->
            SupabaseFlashcardDto(
                id = Uuid.random().toString(),
                deckId = deckId,
                front = card.front,
                back = card.back,
                orderIndex = card.orderIndex
            )
        }

        return try {
            supabase.from("flashcards").upsert(dtoList)
            updateDeckTotalCards(deckId, dtoList.size)
            Result.Success(Unit)
        } catch (e: Exception) {
            logger.error("Failed to add flashcards", e)
            Result.Failure(e.toDataError())
        }
    }

    override suspend fun listDecks(
        userId: String
    ): Result<List<Deck>, DataError.Remote> {
        return try {
            val decks = supabase.from("decks")
                .select {
                    filter { eq("user_id", userId) }
                    order(column = "created_at", order = Order.DESCENDING)
                }
                .decodeList<SupabaseDeckDto>()
                .map { it.toDomain() }
            Result.Success(decks)
        } catch (e: Exception) {
            logger.error("Failed to list decks", e)
            Result.Failure(e.toDataError())
        }
    }

    override suspend fun getDeckCards(
        deckId: String
    ): Result<List<Flashcard>, DataError.Remote> {
        return try {
            val cards = supabase.from("flashcards")
                .select {
                    filter { eq("deck_id", deckId) }
                    order(column = "order_index", order = Order.ASCENDING)
                }
                .decodeList<SupabaseFlashcardDto>()
                .map { it.toDomain() }
            Result.Success(cards)
        } catch (e: Exception) {
            logger.error("Failed to fetch deck cards", e)
            Result.Failure(e.toDataError())
        }
    }

    override suspend fun startSession(
        userId: String,
        deckId: String,
        startedAt: Instant
    ): Result<StudySession, DataError.Remote> {
        val session = StudySession(
            id = Uuid.random().toString(),
            userId = userId,
            deckId = deckId,
            cardsKnown = 0,
            cardsUnknown = 0,
            totalSwiped = 0,
            startedAt = startedAt,
            endedAt = null
        )

        return try {
            supabase.from("study_sessions").upsert(session.toSupabaseDto())
            Result.Success(session)
        } catch (e: Exception) {
            logger.error("Failed to start study session", e)
            Result.Failure(e.toDataError())
        }
    }

    override suspend fun finishSession(
        sessionId: String,
        summary: SessionSummary,
        records: List<SessionRecordInput>
    ): EmptyResult<DataError.Remote> {
        return try {
            supabase.from("study_sessions").update({
                set("cards_known", summary.cardsKnown)
                set("cards_unknown", summary.cardsUnknown)
                set("total_swiped", summary.totalSwiped)
                set("ended_at", summary.endedAt.toIsoTimestamp())
            }) {
                filter { eq("id", sessionId) }
            }

            if (records.isNotEmpty()) {
                val recordDtos = records.map { record ->
                    SupabaseSessionRecordDto(
                        id = Uuid.random().toString(),
                        sessionId = sessionId,
                        flashcardId = record.flashcardId,
                        result = record.result.dbValue,
                        respondedAt = record.respondedAt.toIsoTimestamp()
                    )
                }
                supabase.from("session_records").upsert(recordDtos)
            }

            Result.Success(Unit)
        } catch (e: Exception) {
            logger.error("Failed to finish study session", e)
            Result.Failure(e.toDataError())
        }
    }

    private suspend fun updateDeckTotalCards(deckId: String, addedCount: Int) {
        val existingCount = fetchDeckTotalCards(deckId)
        val updatedCount = (existingCount ?: 0) + addedCount

        supabase.from("decks").update({
            set("total_cards", updatedCount)
        }) {
            filter { eq("id", deckId) }
        }
    }

    private suspend fun fetchDeckTotalCards(deckId: String): Int? {
        val decks = supabase.from("decks")
            .select {
                filter { eq("id", deckId) }
            }
            .decodeList<SupabaseDeckDto>()
        return decks.firstOrNull()?.totalCards
    }

    private fun Exception.toDataError(): DataError.Remote {
        return when (this) {
            is RestException -> {
                when (statusCode) {
                    400 -> DataError.Remote.BAD_REQUEST
                    401 -> DataError.Remote.UNAUTHORIZED
                    403 -> DataError.Remote.FORBIDDEN
                    404 -> DataError.Remote.NOT_FOUND
                    408 -> DataError.Remote.REQUEST_TIMEOUT
                    409 -> DataError.Remote.CONFLICT
                    413 -> DataError.Remote.PAYLOAD_TOO_LARGE
                    429 -> DataError.Remote.TOO_MANY_REQUESTS
                    in 500..502 -> DataError.Remote.SERVER_ERROR
                    503 -> DataError.Remote.SERVICE_UNAVAILABLE
                    else -> DataError.Remote.UNKNOWN
                }
            }

            is UnknownRestException -> DataError.Remote.UNKNOWN
            is ConnectTimeoutException -> DataError.Remote.SERVER_ERROR
            is SocketTimeoutException -> DataError.Remote.REQUEST_TIMEOUT
            else -> {
                if (message?.contains("Unable to resolve host") == true ||
                    message?.contains("Network is unreachable") == true
                ) {
                    DataError.Remote.NO_INTERNET
                } else {
                    DataError.Remote.UNKNOWN
                }
            }
        }
    }
}

private fun Instant.toIsoTimestamp(): String {
    return KxInstant.fromEpochMilliseconds(this.toEpochMilliseconds()).toString()
}
