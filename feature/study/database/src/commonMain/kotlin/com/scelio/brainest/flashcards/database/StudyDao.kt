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
import com.scelio.brainest.flashcards.database.entities.StudySourceEntity
import com.scelio.brainest.flashcards.database.entities.UserAchievementsEntity
import com.scelio.brainest.flashcards.database.entities.WeeklyPointsEntity
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

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertFlashcardProgressList(progress: List<FlashcardProgressEntity>)

    @Query("DELETE FROM flashcard_progress WHERE deckId = :deckId")
    suspend fun deleteFlashcardProgressByDeckId(deckId: String)

    @Query("DELETE FROM flashcard_progress WHERE deckId = :deckId")
    suspend fun deleteSyncedFlashcardProgressByDeckId(deckId: String)

    @Transaction
    suspend fun replaceSyncedFlashcardProgress(deckId: String, progress: List<FlashcardProgressEntity>) {
        deleteSyncedFlashcardProgressByDeckId(deckId)
        if (progress.isNotEmpty()) {
            upsertFlashcardProgressList(progress)
        }
    }

    @Query("DELETE FROM quiz_progress WHERE deckId = :deckId")
    suspend fun deleteQuizProgressByDeckId(deckId: String)

    @Query("DELETE FROM quiz_progress WHERE deckId = :deckId")
    suspend fun deleteSyncedQuizProgressByDeckId(deckId: String)

    @Transaction
    suspend fun replaceSyncedQuizProgress(deckId: String, progress: List<QuizProgressEntity>) {
        deleteSyncedQuizProgressByDeckId(deckId)
        if (progress.isNotEmpty()) {
            upsertQuizProgressList(progress)
        }
    }

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

    // Weekly Points DAO methods
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertWeeklyPoints(weeklyPoints: WeeklyPointsEntity)

    @Query("SELECT * FROM user_weekly_points WHERE userId = :userId AND weekStartDate = :weekStartDate")
    suspend fun getWeeklyPoints(userId: String, weekStartDate: String): WeeklyPointsEntity?

    @Query("DELETE FROM user_weekly_points WHERE userId = :userId AND weekStartDate = :weekStartDate")
    suspend fun deleteWeeklyPoints(userId: String, weekStartDate: String)

    // User achievements cache methods
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertUserAchievements(achievements: UserAchievementsEntity)

    @Query("SELECT * FROM user_achievements WHERE userId = :userId LIMIT 1")
    suspend fun getUserAchievements(userId: String): UserAchievementsEntity?
}
