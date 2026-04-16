package com.scelio.brainest.home.domain

import kotlinx.datetime.LocalDate

data class UserAchievements(
    val userId: String,
    val totalPoints: Int,
    val currentStreakDays: Int,
    val longestStreakDays: Int,
    val completedDecksCount: Int,
    val completedQuizzesCount: Int,
    val lastActivityDate: LocalDate?
)
