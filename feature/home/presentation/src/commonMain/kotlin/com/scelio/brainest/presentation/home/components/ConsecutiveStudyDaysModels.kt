package com.scelio.brainest.presentation.home.components

enum class StudyDayStatus {
    Completed,
    Current,
    Upcoming
}

enum class StudyDayPoints {
    Two,
    Eight
}

data class StudyDayUi(
    val label: String,
    val status: StudyDayStatus,
    val points: StudyDayPoints? = null,
)
