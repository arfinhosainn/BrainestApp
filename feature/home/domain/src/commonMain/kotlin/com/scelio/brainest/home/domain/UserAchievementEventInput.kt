package com.scelio.brainest.home.domain

import kotlinx.datetime.LocalDate

enum class UserAchievementEventType {
    POINTS_EARNED,
    STREAK_UPDATED,
    DECK_COMPLETED,
    QUIZ_COMPLETED
}

data class UserAchievementEventInput(
    val userId: String,
    val eventType: UserAchievementEventType,
    val pointsDelta: Int? = null,
    val relatedDeckId: String? = null,
    val occurredOn: LocalDate? = null,
    val metadata: String? = null
)
