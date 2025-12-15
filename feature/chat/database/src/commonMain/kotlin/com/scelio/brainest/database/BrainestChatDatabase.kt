package com.scelio.brainest.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.scelio.brainest.database.entities.ChatEntity
import com.scelio.brainest.database.entities.MessageEntity

@Database(
    entities = [ChatEntity::class, MessageEntity::class],
    version = 1,
    exportSchema = true
)
abstract class BrainestChatDatabase : RoomDatabase() {
    abstract fun chatDao(): ChatDao

    companion object {
        const val DB_NAME = "brainest.db"
    }
}