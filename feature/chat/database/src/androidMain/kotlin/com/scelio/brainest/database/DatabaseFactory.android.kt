package com.scelio.brainest.database

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase

actual class DatabaseFactory(
    private val context: Context
) {
    actual fun create(): RoomDatabase.Builder<BrainestChatDatabase> {
        val dbFile = context.applicationContext.getDatabasePath(BrainestChatDatabase.DB_NAME)

        return Room.databaseBuilder(
            context.applicationContext,
            dbFile.absolutePath
        )
    }
}