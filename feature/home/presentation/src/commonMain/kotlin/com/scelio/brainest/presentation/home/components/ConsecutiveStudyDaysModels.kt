package com.scelio.brainest.presentation.home.components

import androidx.compose.runtime.Stable
import org.jetbrains.compose.resources.StringResource

@Stable
enum class StudyDayStatus {
    Completed,
    Missed,
    Current,
    Upcoming
}

@Stable
data class StudyDayUi(
    val label: StringResource,
    val status: StudyDayStatus,
    val points: Int? = null
)
