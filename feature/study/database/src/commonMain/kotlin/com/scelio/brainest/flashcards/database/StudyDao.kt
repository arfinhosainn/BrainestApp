package com.scelio.brainest.flashcards.database

import androidx.room.Dao
import androidx.room.Embedded
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.scelio.brainest.flashcards.database.entities.DeckEntity
import com.scelio.brainest.flashcards.database.entities.QuizQuestionEntity
import com.scelio.brainest.flashcards.database.entities.StudySourceEntity
import kotlinx.coroutines.flow.Flow

data class StudySetSummaryRow(
    @Embedded val deck: DeckEntity,
    val quizCount: Int
)

@Dao
interface StudyDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertDeck(deck: DeckEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
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
}
