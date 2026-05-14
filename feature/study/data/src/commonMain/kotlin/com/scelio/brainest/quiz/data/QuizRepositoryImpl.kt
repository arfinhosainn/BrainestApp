package com.scelio.brainest.quiz.data

import com.scelio.brainest.domain.logging.BrainestLogger
import com.scelio.brainest.domain.util.DataError
import com.scelio.brainest.domain.util.EmptyResult
import com.scelio.brainest.domain.util.Result
import com.scelio.brainest.data.util.toDataError
import com.scelio.brainest.flashcards.data.SupabaseQuizQuestionDto
import com.scelio.brainest.flashcards.data.SupabaseQuizProgressDto
import com.scelio.brainest.flashcards.data.toDomain
import com.scelio.brainest.flashcards.data.toSupabaseDto
import com.scelio.brainest.flashcards.database.StudyDao
import com.scelio.brainest.flashcards.database.mappers.toDomain
import com.scelio.brainest.flashcards.database.mappers.toEntity
import com.scelio.brainest.flashcards.domain.QuizProgress
import com.scelio.brainest.quiz.domain.QuizQuestion
import com.scelio.brainest.quiz.domain.QuizQuestionInput
import com.scelio.brainest.quiz.domain.QuizRepository
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Order
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
            coroutineScope.launch(Dispatchers.IO) {
                syncQuizProgressToRemote(progress)
            }
            Result.Success(Unit)
        } catch (e: Exception) {
            logger.error("Failed to save quiz progress", e)
            Result.Failure(DataError.Remote.UNKNOWN)
        }
    }

    override suspend fun getQuizProgress(
        deckId: String
    ): Result<List<QuizProgress>, DataError.Remote> {
        val localProgress = studyDao.getQuizProgressByDeckId(deckId).map { it.toDomain() }
        if (localProgress.isNotEmpty()) {
            return Result.Success(localProgress)
        }

        return when (val remoteProgress = fetchRemoteQuizProgress(deckId)) {
            is Result.Success -> {
                if (remoteProgress.data.isNotEmpty()) {
                    studyDao.replaceSyncedQuizProgress(
                        deckId = deckId,
                        progress = remoteProgress.data.map { it.toEntity() }
                    )
                }
                remoteProgress
            }
            is Result.Failure -> {
                // If remote fetch fails, return empty success instead of error
                // since no progress data is not a critical failure
                Result.Success(emptyList())
            }
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

    override suspend fun syncDeckQuizProgress(
        deckId: String
    ): EmptyResult<DataError.Remote> {
        val hasLocalProgress = studyDao.getQuizProgressByDeckId(deckId).isNotEmpty()
        if (hasLocalProgress) {
            return Result.Success(Unit)
        }

        return when (val result = fetchRemoteQuizProgress(deckId)) {
            is Result.Success -> {
                if (result.data.isNotEmpty()) {
                    studyDao.replaceSyncedQuizProgress(
                        deckId = deckId,
                        progress = result.data.map { it.toEntity() }
                    )
                }
                Result.Success(Unit)
            }

            is Result.Failure -> {
                logger.error("Failed to sync remote quiz progress for $deckId: ${result.error}")
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

    private suspend fun syncQuizProgressToRemote(progress: QuizProgress) {
        try {
            supabase.from("quiz_progress").upsert(progress.toSupabaseDto())
        } catch (e: Exception) {
            logger.error("Failed to sync quiz progress", e)
        }
    }

    private suspend fun fetchRemoteQuizProgress(
        deckId: String
    ): Result<List<QuizProgress>, DataError.Remote> {
        return try {
            val progress = supabase.from("quiz_progress")
                .select {
                    filter { eq("deck_id", deckId) }
                    order(column = "completed_at", order = Order.DESCENDING)
                }
                .decodeList<SupabaseQuizProgressDto>()
                .map { it.toDomain() }
            Result.Success(progress)
        } catch (e: Exception) {
            logger.error("Failed to fetch quiz progress", e)
            Result.Failure(e.toDataError())
        }
    }
}
