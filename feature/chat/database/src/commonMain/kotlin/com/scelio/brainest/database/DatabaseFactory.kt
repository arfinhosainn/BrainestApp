package com.scelio.brainest.database

import androidx.room.Room
import androidx.room.RoomDatabase

expect class DatabaseFactory {
    fun create(): RoomDatabase.Builder<BrainestChatDatabase>
}