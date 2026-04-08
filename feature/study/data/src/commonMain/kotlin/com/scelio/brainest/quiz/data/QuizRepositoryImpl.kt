package com.scelio.brainest.quiz.data

import com.scelio.brainest.domain.logging.BrainestLogger
import com.scelio.brainest.domain.util.DataError
import com.scelio.brainest.domain.util.EmptyResult
import com.scelio.brainest.domain.util.Result
import com.scelio.brainest.flashcards.data.SupabaseQuizQuestionDto
import com.scelio.brainest.flashcards.data.toDomain
import com.scelio.brainest.flashcards.database.StudyDao
import com.scelio.brainest.flashcards.database.mappers.toDomain
import com.scelio.brainest.flashcards.database.mappers.toEntity
import com.scelio.brainest.flashcards.domain.QuizProgress
import com.scelio.brainest.quiz.domain.QuizQuestion
import com.scelio.brainest.quiz.domain.QuizQuestionInput
import com.scelio.brainest.quiz.domain.QuizRepository
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.exceptions.RestException
import io.github.jan.supabase.exceptions.UnknownRestException
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Order
import io.ktor.client.network.sockets.ConnectTimeoutException
import io.ktor.client.network.sockets.SocketTimeoutException
import kotlin.time.Instant
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.launch

@OptIn(ExperimentalUuidApi::class)
class QuizRepositoryImpl(
    private val supabase: SupabaseClient,
    private val logger: BrainestLogger,
    private val studyDao: StudyDao,
    private val coroutineScope: CoroutineScope
) : QuizRepository {

    override suspend fun addQuizQuestions(
        deckId: String,
        questions: List<QuizQuestionInput>
    ): EmptyResult<DataError.Remote> {
        if (questions.isEmpty()) {
            return Result.Success(Unit)
        }

        val localQuestions = questions.map { question ->
            QuizQuestion(
                id = Uuid.random().toString(),
                deckId = deckId,
                question = question.question,
                options = question.options,
                correctIndex = question.correctIndex,
                orderIndex = question.orderIndex
            )
        }

        return try {
            studyDao.upsertQuizQuestions(localQuestions.map { it.toEntity(isPendingSync = true) })
            coroutineScope.launch(Dispatchers.IO) {
                syncQuizQuestionsToRemote(deckId, localQuestions)
            }
            Result.Success(Unit)
        } catch (e: Exception) {
            logger.error("Failed to save local quiz questions", e)
            Result.Failure(DataError.Remote.UNKNOWN)
        }
    }

    override suspend fun getQuizQuestions(
        deckId: String
    ): Result<List<QuizQuestion>, DataError.Remote> {
        val localQuestions = studyDao.getQuizQuestions(deckId).map { it.toDomain() }
        if (localQuestions.isNotEmpty()) {
            return Result.Success(localQuestions)
        }

        return when (val remoteQuestions = fetchRemoteQuizQuestions(deckId)) {
            is Result.Success -> {
                studyDao.replaceSyncedQuizQuestions(
                    deckId = deckId,
                    questions = remoteQuestions.data.map { it.toEntity() }
                )
                remoteQuestions
            }

            is Result.Failure -> remoteQuestions
        }
    }

    override suspend fun recordQuizCompletion(
        deckId: String,
        totalQuestions: Int,
        answeredQuestions: Int,
        correctAnswers: Int,
        completedAt: Instant
    ): EmptyResult<DataError.Remote> {
        return try {
            val progress = QuizProgress(
                id = Uuid.random().toString(),
                deckId = deckId,
                totalQuestions = totalQuestions,
                answeredQuestions = answeredQuestions,
                correctAnswers = correctAnswers,
                completedAt = completedAt
            )
            studyDao.upsertQuizProgress(progress.toEntity())
            Result.Success(Unit)
        } catch (e: Exception) {
            logger.error("Failed to save quiz progress", e)
            Result.Failure(DataError.Remote.UNKNOWN)
        }
    }

    override suspend fun getQuizProgress(
        deckId: String
    ): Result<List<QuizProgress>, DataError.Remote> {
        return try {
            Result.Success(
                studyDao.getQuizProgressByDeckId(deckId).map { it.toDomain() }
            )
        } catch (e: Exception) {
            logger.error("Failed to read quiz progress", e)
            Result.Failure(DataError.Remote.UNKNOWN)
        }
    }

    override suspend fun syncDeckQuizQuestions(
        deckId: String
    ): EmptyResult<DataError.Remote> {
        return when (val result = fetchRemoteQuizQuestions(deckId)) {
            is Result.Success -> {
                studyDao.replaceSyncedQuizQuestions(
                    deckId = deckId,
                    questions = result.data.map { it.toEntity(isPendingSync = false) }
                )
                Result.Success(Unit)
            }

            is Result.Failure -> {
                logger.error("Failed to sync remote quiz questions for $deckId: ${result.error}")
                result
            }
        }
    }

    private suspend fun syncQuizQuestionsToRemote(
        deckId: String,
        questions: List<QuizQuestion>
    ) {
        try {
            val dtoList = questions.map { question ->
                SupabaseQuizQuestionDto(
                    id = question.id,
                    deckId = deckId,
                    question = question.question,
                    options = question.options,
                    correctIndex = question.correctIndex,
                    orderIndex = question.orderIndex
                )
            }
            supabase.from("quiz_questions").upsert(dtoList)
            studyDao.upsertQuizQuestions(questions.map { it.toEntity(isPendingSync = false) })
        } catch (e: Exception) {
            logger.error("Failed to sync quiz questions", e)
        }
    }

    private suspend fun fetchRemoteQuizQuestions(
        deckId: String
    ): Result<List<QuizQuestion>, DataError.Remote> {
        return try {
            val questions = supabase.from("quiz_questions")
                .select {
                    filter { eq("deck_id", deckId) }
                    order(column = "order_index", order = Order.ASCENDING)
                }
                .decodeList<SupabaseQuizQuestionDto>()
                .map { it.toDomain() }
            Result.Success(questions)
        } catch (e: Exception) {
            logger.error("Failed to fetch quiz questions", e)
            Result.Failure(e.toDataError())
        }
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
