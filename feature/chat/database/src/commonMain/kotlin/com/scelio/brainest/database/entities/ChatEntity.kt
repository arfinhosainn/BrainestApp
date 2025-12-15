package com.scelio.brainest.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(
    tableName = "chats"
)
data class ChatEntity(
    @PrimaryKey
    val id: String,
    val userId: String,
    val title: String?,
    val model: String,
    val systemPrompt: String?,
    val createdAt: Long, // Timestamp in milliseconds
    val lastActivityAt: Long, // Timestamp in milliseconds
    val messageCount: Int
)