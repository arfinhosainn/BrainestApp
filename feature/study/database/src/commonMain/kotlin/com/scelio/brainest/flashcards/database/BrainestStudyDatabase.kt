package com.scelio.brainest.flashcards.database

import androidx.room.ConstructedBy
import androidx.room.Database
import androidx.room.RoomDatabase
import com.scelio.brainest.flashcards.database.entities.DeckEntity
import com.scelio.brainest.flashcards.database.entities.QuizQuestionEntity
import com.scelio.brainest.flashcards.database.entities.StudySourceEntity

@Database(
    entities = [
        DeckEntity::class,
        StudySourceEntity::class,
        QuizQuestionEntity::class
    ],
    version = 1,
    exportSchema = true
)
@ConstructedBy(BrainestStudyDatabaseConstructor::class)
abstract class BrainestStudyDatabase : RoomDatabase() {
    abstract fun studyDao(): StudyDao

    companion object {
        const val DB_NAME = "brainest-study.db"
    }
}
