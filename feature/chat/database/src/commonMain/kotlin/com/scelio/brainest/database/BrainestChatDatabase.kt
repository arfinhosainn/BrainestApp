package com.scelio.brainest.database

import androidx.room.ConstructedBy // 1. Add this import
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.RoomDatabaseConstructor // 2. Add this import
import com.scelio.brainest.database.entities.ChatEntity
import com.scelio.brainest.database.entities.MessageEntity

@Database(
    entities = [ChatEntity::class, MessageEntity::class],
    version = 1,
    exportSchema = true
)
@ConstructedBy(BrainestChatDatabaseConstructor::class) // 3. Add this annotation
abstract class BrainestChatDatabase : RoomDatabase() {
    abstract fun chatDao(): ChatDao

    companion object {
        const val DB_NAME = "brainest.db"
    }
}
