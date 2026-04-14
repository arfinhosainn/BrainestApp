package com.scelio.brainest.presentation.home.components

import androidx.compose.runtime.Stable

@Stable
enum class StudyDayStatus {
    Completed,
    Missed,
    Current,
    Upcoming
}

@Stable
data class StudyDayUi(
    val label: String,
    val status: StudyDayStatus,
    val points: Int? = null
)
