package com.scelio.brainest.flashcards.database

import androidx.room.RoomDatabase

expect class DatabaseFactory {
    fun create(): RoomDatabase.Builder<BrainestStudyDatabase>
}
