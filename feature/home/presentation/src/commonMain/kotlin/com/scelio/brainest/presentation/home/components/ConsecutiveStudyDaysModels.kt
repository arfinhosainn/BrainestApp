package com.scelio.brainest.presentation.home.components

enum class StudyDayStatus {
    Completed,
    Missed,
    Current,
    Upcoming
}

data class StudyDayUi(
    val label: String,
    val status: StudyDayStatus,
)
