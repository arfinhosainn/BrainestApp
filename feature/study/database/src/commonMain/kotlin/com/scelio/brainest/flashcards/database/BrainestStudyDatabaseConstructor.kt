package com.scelio.brainest.flashcards.database

import androidx.room.RoomDatabaseConstructor

@Suppress("NO_ACTUAL_FOR_EXPECT")
expect object BrainestStudyDatabaseConstructor : RoomDatabaseConstructor<BrainestStudyDatabase> {
    override fun initialize(): BrainestStudyDatabase
}
