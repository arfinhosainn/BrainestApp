package com.scelio.brainest.flashcards.database

import androidx.room.AutoMigration
import androidx.room.ConstructedBy
import androidx.room.Database
import androidx.room.RoomDatabase
import com.scelio.brainest.flashcards.database.entities.DeckEntity
import com.scelio.brainest.flashcards.database.entities.FlashcardProgressEntity
import com.scelio.brainest.flashcards.database.entities.QuizQuestionEntity
import com.scelio.brainest.flashcards.database.entities.QuizProgressEntity
import com.scelio.brainest.flashcards.database.entities.StudySourceEntity
import com.scelio.brainest.flashcards.database.entities.UserAchievementsEntity
import com.scelio.brainest.flashcards.database.entities.WeeklyPointsEntity

@Database(
    entities = [
        DeckEntity::class,
        StudySourceEntity::class,
        QuizQuestionEntity::class,
        FlashcardProgressEntity::class,
        QuizProgressEntity::class,
        WeeklyPointsEntity::class,
        UserAchievementsEntity::class
    ],
    version = 4,
    autoMigrations = [AutoMigration(from = 3, to = 4)],
    exportSchema = true
)
@ConstructedBy(BrainestStudyDatabaseConstructor::class)
abstract class BrainestStudyDatabase : RoomDatabase() {
    abstract fun studyDao(): StudyDao

    companion object {
        const val DB_NAME = "brainest-study.db"
    }
}
