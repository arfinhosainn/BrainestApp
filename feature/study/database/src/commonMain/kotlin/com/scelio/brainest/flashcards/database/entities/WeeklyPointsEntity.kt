package com.scelio.brainest.flashcards.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_weekly_points")
data class WeeklyPointsEntity(
    @PrimaryKey val userId: String,
    val weekStartDate: String,
    val mondayPoints: Int,
    val tuesdayPoints: Int,
    val wednesdayPoints: Int,
    val thursdayPoints: Int,
    val fridayPoints: Int,
    val saturdayPoints: Int,
    val sundayPoints: Int,
    val createdAt: String,
    val updatedAt: String
)
