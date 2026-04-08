package com.scelio.brainest.flashcards.data

import com.scelio.brainest.domain.logging.BrainestLogger
import com.scelio.brainest.domain.util.DataError
import com.scelio.brainest.domain.util.EmptyResult
import com.scelio.brainest.domain.util.Result
import com.scelio.brainest.flashcards.database.StudyDao
import com.scelio.brainest.flashcards.database.mappers.toDomain
import com.scelio.brainest.flashcards.database.mappers.toEntity
import com.scelio.brainest.flashcards.domain.Deck
import com.scelio.brainest.flashcards.domain.DeckStudyProgressSummary
import com.scelio.brainest.flashcards.domain.Flashcard
import com.scelio.brainest.flashcards.domain.FlashcardInput
import com.scelio.brainest.flashcards.domain.FlashcardProgress
import com.scelio.brainest.flashcards.domain.FlashcardResult
import com.scelio.brainest.flashcards.domain.FlashcardsRepository
import com.scelio.brainest.flashcards.domain.SessionRecordInput
import com.scelio.brainest.flashcards.domain.SessionSummary
import com.scelio.brainest.flashcards.domain.StudySetSummary
import com.scelio.brainest.flashcards.domain.StudySource
import com.scelio.brainest.flashcards.domain.StudySession
import com.scelio.brainest.quiz.domain.QuizRepository
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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.datetime.Instant as KxInstant

@OptIn(ExperimentalUuidApi::class)
class FlashcardsRepositoryImpl(
    private val supabase: SupabaseClient,
    private val logger: BrainestLogger,
    private val studyDao: StudyDao,
    private val coroutineScope: CoroutineScope,
    private val quizRepository: QuizRepository
) : FlashcardsRepository {

    override fun observeStudySetSummaries(
        userId: String
    ): Flow<List<StudySetSummary>> {
        return studyDao.observeStudySetSummaries(userId)
            .map { rows -> rows.map { it.toDomain() } }
    }

    override fun observeDeckStudyProgressSummaries(
        userId: String
    ): Flow<List<DeckStudyProgressSummary>> {
        return studyDao.observeDeckProgressSummaries(userId)
            .map { rows -> rows.map { it.toDomain() } }
    }

    override suspend fun syncStudySetSummaries(
        userId: String
    ): EmptyResult<DataError.Remote> {
        return when (val remoteDecks = fetchRemoteDecks(userId)) {
            is Result.Success -> {
                cacheRemoteDecks(userId, remoteDecks.data)
                remoteDecks.data.forEach { deck ->
                    quizRepository.syncDeckQuizQuestions(deck.id)
                }
                Result.Success(Unit)
            }

            is Result.Failure -> remoteDecks
        }
    }

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
            studyDao.upsertDeck(deck.toEntity(isPendingSync = true))
            coroutineScope.launch(Dispatchers.IO) {
                syncDeckToRemote(deck)
            }
            Result.Success(deck)
        } catch (e: Exception) {
            logger.error("Failed to create local deck", e)
            Result.Failure(DataError.Remote.UNKNOWN)
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
            updateRemoteDeckTotalCards(deckId, dtoList.size)
            updateLocalDeckTotalCards(deckId, dtoList.size)
            Result.Success(Unit)
        } catch (e: Exception) {
            logger.error("Failed to add flashcards", e)
            Result.Failure(e.toDataError())
        }
    }

    override suspend fun listDecks(
        userId: String
    ): Result<List<Deck>, DataError.Remote> {
        val localDecks = studyDao.getDecksByUserId(userId).map { it.toDomain() }
        if (localDecks.isNotEmpty()) {
            coroutineScope.launch(Dispatchers.IO) {
                syncStudySetSummaries(userId)
            }
            return Result.Success(localDecks)
        }

        return when (val remoteDecks = fetchRemoteDecks(userId)) {
            is Result.Success -> {
                cacheRemoteDecks(userId, remoteDecks.data)
                remoteDecks.data.forEach { deck ->
                    coroutineScope.launch(Dispatchers.IO) {
                        quizRepository.syncDeckQuizQuestions(deck.id)
                    }
                }
                Result.Success(remoteDecks.data)
            }

            is Result.Failure -> remoteDecks
        }
    }

    override suspend fun getDeck(
        deckId: String
    ): Result<Deck?, DataError.Remote> {
        val localDeck = studyDao.getDeck(deckId)?.toDomain()
        if (localDeck != null) {
            return Result.Success(localDeck)
        }

        return when (val remoteDeck = fetchRemoteDeck(deckId)) {
            is Result.Success -> {
                remoteDeck.data?.let { studyDao.upsertDeck(it.toEntity()) }
                remoteDeck
            }

            is Result.Failure -> remoteDeck
        }
    }

    override suspend fun listStudySessions(
        userId: String
    ): Result<List<StudySession>, DataError.Remote> {
        return try {
            val sessions = supabase.from("study_sessions")
                .select {
                    filter { eq("user_id", userId) }
                    order(column = "started_at", order = Order.DESCENDING)
                }
                .decodeList<SupabaseStudySessionDto>()
                .map { it.toDomain() }
            Result.Success(sessions)
        } catch (e: Exception) {
            logger.error("Failed to list study sessions", e)
            Result.Failure(e.toDataError())
        }
    }

    override suspend fun saveStudySource(
        source: StudySource
    ): EmptyResult<DataError.Remote> {
        return try {
            studyDao.upsertStudySource(source.toEntity(isPendingSync = true))
            coroutineScope.launch(Dispatchers.IO) {
                syncStudySourceToRemote(source)
            }
            Result.Success(Unit)
        } catch (e: Exception) {
            logger.error("Failed to save local study source", e)
            Result.Failure(DataError.Remote.UNKNOWN)
        }
    }

    override suspend fun getStudySource(
        deckId: String
    ): Result<StudySource?, DataError.Remote> {
        val localSource = studyDao.getStudySource(deckId)?.toDomain()
        if (localSource != null) {
            return Result.Success(localSource)
        }

        return when (val remoteSource = fetchRemoteStudySource(deckId)) {
            is Result.Success -> {
                remoteSource.data?.let { studyDao.upsertStudySource(it.toEntity()) }
                remoteSource
            }

            is Result.Failure -> remoteSource
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

    override suspend fun recordFlashcardSwipe(
        deckId: String,
        cardId: String,
        result: FlashcardResult,
        swipedAt: Instant
    ): EmptyResult<DataError.Remote> {
        return try {
            val progress = FlashcardProgress(
                id = Uuid.random().toString(),
                deckId = deckId,
                cardId = cardId,
                swipesCount = 1,
                lastResult = result,
                updatedAt = swipedAt
            )
            studyDao.upsertFlashcardProgress(progress.toEntity())
            Result.Success(Unit)
        } catch (e: Exception) {
            logger.error("Failed to save flashcard progress", e)
            Result.Failure(DataError.Remote.UNKNOWN)
        }
    }

    override suspend fun getFlashcardProgress(
        deckId: String
    ): Result<List<FlashcardProgress>, DataError.Remote> {
        return try {
            Result.Success(
                studyDao.getFlashcardProgressByDeckId(deckId).map { it.toDomain() }
            )
        } catch (e: Exception) {
            logger.error("Failed to read flashcard progress", e)
            Result.Failure(DataError.Remote.UNKNOWN)
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

    private suspend fun syncDeckToRemote(deck: Deck) {
        try {
            supabase.from("decks").upsert(deck.toSupabaseDto())
            studyDao.upsertDeck(deck.toEntity(isPendingSync = false))
        } catch (e: Exception) {
            logger.error("Failed to sync deck", e)
        }
    }

    private suspend fun syncStudySourceToRemote(source: StudySource) {
        try {
            supabase.from("study_sources").upsert(source.toSupabaseDto())
            studyDao.upsertStudySource(source.toEntity(isPendingSync = false))
        } catch (e: Exception) {
            logger.error("Failed to sync study source", e)
        }
    }

    private suspend fun cacheRemoteDecks(
        userId: String,
        remoteDecks: List<Deck>
    ) {
        studyDao.upsertDecks(remoteDecks.map { it.toEntity(isPendingSync = false) })
        if (remoteDecks.isEmpty()) {
            studyDao.deleteSyncedDecksByUserId(userId)
        } else {
            studyDao.deleteSyncedDecksByUserIdExcluding(
                userId = userId,
                deckIds = remoteDecks.map { it.id }
            )
        }
    }

    private suspend fun updateRemoteDeckTotalCards(deckId: String, addedCount: Int) {
        val existingCount = fetchRemoteDeckTotalCards(deckId)
        val updatedCount = (existingCount ?: 0) + addedCount

        supabase.from("decks").update({
            set("total_cards", updatedCount)
        }) {
            filter { eq("id", deckId) }
        }
    }

    private suspend fun updateLocalDeckTotalCards(deckId: String, addedCount: Int) {
        val existingDeck = studyDao.getDeck(deckId) ?: return
        studyDao.upsertDeck(
            existingDeck.copy(totalCards = existingDeck.totalCards + addedCount)
        )
    }

    private suspend fun fetchRemoteDecks(
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

    private suspend fun fetchRemoteDeck(
        deckId: String
    ): Result<Deck?, DataError.Remote> {
        return try {
            val decks = supabase.from("decks")
                .select {
                    filter { eq("id", deckId) }
                }
                .decodeList<SupabaseDeckDto>()
                .map { it.toDomain() }
            Result.Success(decks.firstOrNull())
        } catch (e: Exception) {
            logger.error("Failed to fetch deck", e)
            Result.Failure(e.toDataError())
        }
    }

    private suspend fun fetchRemoteStudySource(
        deckId: String
    ): Result<StudySource?, DataError.Remote> {
        return try {
            val sources = supabase.from("study_sources")
                .select {
                    filter { eq("deck_id", deckId) }
                    order(column = "created_at", order = Order.DESCENDING)
                }
                .decodeList<SupabaseStudySourceDto>()
                .map { it.toDomain() }
            Result.Success(sources.firstOrNull())
        } catch (e: Exception) {
            logger.error("Failed to load study source", e)
            Result.Failure(e.toDataError())
        }
    }

    private suspend fun fetchRemoteDeckTotalCards(deckId: String): Int? {
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
