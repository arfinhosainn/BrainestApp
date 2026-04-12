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
import com.scelio.brainest.designsystem.BrainestTheme
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun ConsecutiveStudyDaysCard(
    days: List<StudyDayUi>,
    modifier: Modifier = Modifier,
    title: String = "Consecutive study days",
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
                StudyDayUi("Mon", StudyDayStatus.Completed),
                StudyDayUi("Tue", StudyDayStatus.Completed),
                StudyDayUi("Wed", StudyDayStatus.Missed),
                StudyDayUi("Thu", StudyDayStatus.Current),
                StudyDayUi("Fri", StudyDayStatus.Upcoming),
                StudyDayUi("Sat", StudyDayStatus.Upcoming),
                StudyDayUi("Sun", StudyDayStatus.Upcoming),
            ),
        )
    }
}
