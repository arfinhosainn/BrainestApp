package com.scelio.brainest.database

import androidx.room.RoomDatabase
import androidx.room.Room
import kotlinx.cinterop.ExperimentalForeignApi
import platform.Foundation.NSDocumentDirectory
import platform.Foundation.NSFileManager
import platform.Foundation.NSUserDomainMask


@OptIn(ExperimentalForeignApi::class)

actual class DatabaseFactory {
    actual fun create(): RoomDatabase.Builder<BrainestChatDatabase> {
        val dbFile = documentDirectory() + "/${BrainestChatDatabase.DB_NAME}"

        return Room.databaseBuilder(dbFile)
    }

    private fun documentDirectory(): String {
        val documentDirectory = NSFileManager.defaultManager.URLForDirectory(
            directory = NSDocumentDirectory,
            inDomain = NSUserDomainMask,
            appropriateForURL = null,
            create = false,
            error = null
        )

        return requireNotNull(documentDirectory?.path)
    }
}