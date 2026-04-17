package com.scelio.brainest.presentation.home.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import brainest.feature.home.presentation.generated.resources.Res
import brainest.feature.home.presentation.generated.resources.home_day_fri
import brainest.feature.home.presentation.generated.resources.home_day_mon
import brainest.feature.home.presentation.generated.resources.home_day_sat
import brainest.feature.home.presentation.generated.resources.home_day_sun
import brainest.feature.home.presentation.generated.resources.home_day_thu
import brainest.feature.home.presentation.generated.resources.home_day_tue
import brainest.feature.home.presentation.generated.resources.home_day_wed
import brainest.feature.home.presentation.generated.resources.home_consecutive_study_days
import com.scelio.brainest.designsystem.BrainestTheme
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun ConsecutiveStudyDaysCard(
    days: List<StudyDayUi>,
    modifier: Modifier = Modifier,
    title: String = stringResource(Res.string.home_consecutive_study_days),
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        color = Color.White,
        shadowElevation = 2.dp,
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 18.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            ConsecutiveStudyDaysHeader(title = title)
            Spacer(modifier = Modifier.height(16.dp))
            StudyDaysRow(days = days)
        }
    }
}

@Preview
@Composable
private fun PreviewConsecutiveStudyDaysCard() {
    BrainestTheme {
        ConsecutiveStudyDaysCard(
            days = listOf(
                StudyDayUi(Res.string.home_day_mon, StudyDayStatus.Completed),
                StudyDayUi(Res.string.home_day_tue, StudyDayStatus.Completed),
                StudyDayUi(Res.string.home_day_wed, StudyDayStatus.Missed),
                StudyDayUi(Res.string.home_day_thu, StudyDayStatus.Current, points = 8),
                StudyDayUi(Res.string.home_day_fri, StudyDayStatus.Upcoming, points = 2),
                StudyDayUi(Res.string.home_day_sat, StudyDayStatus.Upcoming, points = 8),
                StudyDayUi(Res.string.home_day_sun, StudyDayStatus.Upcoming, points = 2),
            ),
        )
    }
}
