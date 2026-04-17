package com.scelio.brainest.presentation.home.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import brainest.feature.home.presentation.generated.resources.Res
import brainest.feature.home.presentation.generated.resources.home_completed
import brainest.feature.home.presentation.generated.resources.home_missed
import brainest.feature.home.presentation.generated.resources.home_points
import brainest.feature.home.presentation.generated.resources.ic_check
import brainest.feature.home.presentation.generated.resources.ic_close
import brainest.feature.home.presentation.generated.resources.ic_2point
import brainest.feature.home.presentation.generated.resources.ic_8point
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource

@Composable
internal fun StudyDayItem(
    day: StudyDayUi,
    modifier: Modifier = Modifier,
) {
    val shape = RoundedCornerShape(16.dp)
    val backgroundColor = when (day.status) {
        StudyDayStatus.Completed -> Color(0xFFFEF5EA)
        StudyDayStatus.Missed -> Color(0xFFFEF5EA)
        StudyDayStatus.Current -> Color.Unspecified
        StudyDayStatus.Upcoming -> Color.White
    }
    val borderColor = when (day.status) {
        StudyDayStatus.Completed -> null
        StudyDayStatus.Missed -> null
        StudyDayStatus.Current -> Color(0xFFFFA225)
        StudyDayStatus.Upcoming -> Color(0xFFF1EFEA)
    }
    val labelColor = when (day.status) {
        StudyDayStatus.Completed -> Color(0xFF6B6B6B)
        StudyDayStatus.Missed -> Color(0xFF6B6B6B)
        StudyDayStatus.Current -> Color(0xFFFF8C1A)
        StudyDayStatus.Upcoming -> Color(0xFF6B6B6B)
    }

    val borderModifier = if (borderColor != null) {
        Modifier.border(width = 1.dp, color = borderColor, shape = shape)
    } else {
        Modifier
    }

    Column(
        modifier = modifier
            .width(56.dp)
            .height(84.dp)
            .clip(shape)
            .background(backgroundColor)
            .then(borderModifier)
            .padding(vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween,
    ) {
        when (day.status) {
            StudyDayStatus.Completed -> {
                Icon(
                    imageVector = vectorResource(Res.drawable.ic_check),
                    contentDescription = stringResource(Res.string.home_completed),
                    tint = Color.Unspecified,
                    modifier = Modifier.size(35.dp),
                )
            }
            StudyDayStatus.Missed -> {
                Icon(
                    imageVector = vectorResource(Res.drawable.ic_close),
                    contentDescription = stringResource(Res.string.home_missed),
                    tint = Color.Unspecified,
                    modifier = Modifier.size(35.dp),
                )
            }
            StudyDayStatus.Current,
            StudyDayStatus.Upcoming -> {
                // Show points for upcoming/current days to motivate users
                if (day.points != null && day.points > 0) {
                    PointsBadge(day.points)
                } else {
                    StatusBadge(day.status)
                }
            }
        }

        Text(
            text = stringResource(day.label),
            style = MaterialTheme.typography.labelMedium,
            color = labelColor,
        )
    }
}

@Composable
private fun StatusBadge(
    status: StudyDayStatus,
    modifier: Modifier = Modifier,
) {
    if (status == StudyDayStatus.Upcoming) {
        Spacer(modifier = Modifier.size(32.dp))
        return
    }

    Spacer(modifier = modifier.size(32.dp))
}

@Composable
private fun PointsBadge(
    points: Int,
    modifier: Modifier = Modifier,
) {
    val icon = when {
        points >= 8 -> vectorResource(Res.drawable.ic_8point)
        points >= 2 -> vectorResource(Res.drawable.ic_2point)
        else -> return
    }
    
    Icon(
        imageVector = icon,
        contentDescription = stringResource(Res.string.home_points, points),
        tint = Color.Unspecified,
        modifier = modifier.size(32.dp),
    )
}
