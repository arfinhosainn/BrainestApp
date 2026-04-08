package com.scelio.brainest.flashcards.database

import androidx.room.Dao
import androidx.room.Embedded
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import com.scelio.brainest.flashcards.database.entities.DeckEntity
import com.scelio.brainest.flashcards.database.entities.FlashcardProgressEntity
import com.scelio.brainest.flashcards.database.entities.QuizQuestionEntity
import com.scelio.brainest.flashcards.database.entities.QuizProgressEntity
import com.scelio.brainest.flashcards.database.entities.StudySessionEntity
import com.scelio.brainest.flashcards.database.entities.StudySourceEntity
import kotlinx.coroutines.flow.Flow

data class StudySetSummaryRow(
    @Embedded val deck: DeckEntity,
    val quizCount: Int
)

data class DeckProgressSummaryRow(
    val deckId: String,
    val flashcardsSwiped: Int,
    val quizzesCompleted: Int
)

@Dao
interface StudyDao {

    @Upsert
    suspend fun upsertDeck(deck: DeckEntity)

    @Upsert
    suspend fun upsertDecks(decks: List<DeckEntity>)

    @Query("SELECT * FROM study_decks WHERE id = :deckId")
    suspend fun getDeck(deckId: String): DeckEntity?

    @Query("SELECT * FROM study_decks WHERE userId = :userId ORDER BY createdAt DESC")
    suspend fun getDecksByUserId(userId: String): List<DeckEntity>

    @Query(
        """
        SELECT d.*, COUNT(q.id) AS quizCount
        FROM study_decks d
        LEFT JOIN study_quiz_questions q ON q.deckId = d.id
        WHERE d.userId = :userId
        GROUP BY d.id
        ORDER BY d.createdAt DESC
        """
    )
    fun observeStudySetSummaries(userId: String): Flow<List<StudySetSummaryRow>>

    @Query("DELETE FROM study_decks WHERE userId = :userId AND isPendingSync = 0")
    suspend fun deleteSyncedDecksByUserId(userId: String)

    @Query("DELETE FROM study_decks WHERE userId = :userId AND isPendingSync = 0 AND id NOT IN (:deckIds)")
    suspend fun deleteSyncedDecksByUserIdExcluding(userId: String, deckIds: List<String>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertStudySource(source: StudySourceEntity)

    @Query("SELECT * FROM study_sources WHERE deckId = :deckId LIMIT 1")
    suspend fun getStudySource(deckId: String): StudySourceEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertQuizQuestions(questions: List<QuizQuestionEntity>)

    @Query("SELECT * FROM study_quiz_questions WHERE deckId = :deckId ORDER BY orderIndex ASC")
    suspend fun getQuizQuestions(deckId: String): List<QuizQuestionEntity>

    @Query("DELETE FROM study_quiz_questions WHERE deckId = :deckId")
    suspend fun deleteQuizQuestionsByDeckId(deckId: String)

    @Query("DELETE FROM study_quiz_questions WHERE deckId = :deckId AND isPendingSync = 0")
    suspend fun deleteSyncedQuizQuestionsByDeckId(deckId: String)

    @Transaction
    suspend fun replaceSyncedQuizQuestions(deckId: String, questions: List<QuizQuestionEntity>) {
        deleteSyncedQuizQuestionsByDeckId(deckId)
        if (questions.isNotEmpty()) {
            upsertQuizQuestions(questions)
        }
    }

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertFlashcardProgress(progress: FlashcardProgressEntity)

    @Query(
        """
        SELECT * FROM flashcard_progress
        WHERE deckId = :deckId
        ORDER BY updatedAt DESC
        """
    )
    suspend fun getFlashcardProgressByDeckId(deckId: String): List<FlashcardProgressEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertQuizProgress(progress: QuizProgressEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertQuizProgressList(progress: List<QuizProgressEntity>)

    @Query(
        """
        SELECT * FROM quiz_progress
        WHERE deckId = :deckId
        ORDER BY completedAt DESC
        """
    )
    suspend fun getQuizProgressByDeckId(deckId: String): List<QuizProgressEntity>

    @Query(
        """
        SELECT * FROM quiz_progress
        WHERE deckId = :deckId AND isPendingSync = 1
        ORDER BY completedAt ASC
        """
    )
    suspend fun getPendingQuizProgressByDeckId(deckId: String): List<QuizProgressEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertStudySession(session: StudySessionEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertStudySessions(sessions: List<StudySessionEntity>)

    @Query("SELECT * FROM study_sessions_local WHERE id = :sessionId LIMIT 1")
    suspend fun getStudySession(sessionId: String): StudySessionEntity?

    @Query(
        """
        SELECT * FROM study_sessions_local
        WHERE userId = :userId
        ORDER BY startedAt DESC
        """
    )
    suspend fun getStudySessionsByUserId(userId: String): List<StudySessionEntity>

    @Query(
        """
        SELECT * FROM study_sessions_local
        WHERE userId = :userId AND isPendingSync = 1
        ORDER BY startedAt ASC
        """
    )
    suspend fun getPendingStudySessionsByUserId(userId: String): List<StudySessionEntity>

    @Query(
        """
        SELECT
            d.id AS deckId,
            COALESCE(fp.flashcardsSwiped, 0) AS flashcardsSwiped,
            COALESCE(qp.quizzesCompleted, 0) AS quizzesCompleted
        FROM study_decks d
        LEFT JOIN (
            SELECT deckId, SUM(swipesCount) AS flashcardsSwiped
            FROM flashcard_progress
            GROUP BY deckId
        ) fp ON fp.deckId = d.id
        LEFT JOIN (
            SELECT deckId, COUNT(*) AS quizzesCompleted
            FROM quiz_progress
            GROUP BY deckId
        ) qp ON qp.deckId = d.id
        WHERE d.userId = :userId
        ORDER BY d.createdAt DESC
        """
    )
    fun observeDeckProgressSummaries(userId: String): Flow<List<DeckProgressSummaryRow>>
}
