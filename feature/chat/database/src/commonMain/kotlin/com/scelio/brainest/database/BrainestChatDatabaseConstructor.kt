package com.scelio.brainest.database

import androidx.room.RoomDatabaseConstructor

@Suppress("NO_ACTUAL_FOR_EXPECT")
expect object BrainestChatDatabaseConstructor: RoomDatabaseConstructor<BrainestChatDatabase> {
    override fun initialize(): BrainestChatDatabase
}