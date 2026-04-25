package com.scelio.brainest.database

import androidx.room.ConstructedBy
import androidx.room.Database
import androidx.room.RoomDatabase
import com.scelio.brainest.database.entities.ChatEntity
import com.scelio.brainest.database.entities.MessageEntity

@Database(
    entities = [ChatEntity::class, MessageEntity::class],
    version = 1,
    exportSchema = true
)
@ConstructedBy(BrainestChatDatabaseConstructor::class)
abstract class BrainestChatDatabase : RoomDatabase() {
    abstract fun chatDao(): ChatDao

    companion object {
        const val DB_NAME = "brainest.db"
    }
}
