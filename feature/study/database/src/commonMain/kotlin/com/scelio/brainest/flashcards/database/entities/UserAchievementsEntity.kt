package com.scelio.brainest.flashcards.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_achievements")
data class UserAchievementsEntity(
    @PrimaryKey val userId: String,
    val totalPoints: Int,
    val currentStreakDays: Int,
    val longestStreakDays: Int,
    val completedDecksCount: Int,
    val completedQuizzesCount: Int,
    val lastActivityDate: String?,
    val createdAt: String,
    val updatedAt: String
)
