package com.scelio.brainest.presentation.quiz.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBackIos
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import brainest.feature.study.presentation.generated.resources.Res
import brainest.feature.study.presentation.generated.resources.quiz_back
import brainest.feature.study.presentation.generated.resources.quiz_playing_title
import com.scelio.brainest.designsystem.BrainestTheme
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun QuizTopAppBar(
    title: String,
    timeLeftText: String,
    totalTimeText: String,
    progress: Float,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colorScheme = MaterialTheme.colorScheme

    Column(modifier = modifier) {
        Row(
            modifier = Modifier,
            verticalAlignment = Alignment.CenterVertically
        ) {
            QuizBackButton(onBackClick = onBackClick)
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall,
                color = colorScheme.onSurface,
                fontWeight = FontWeight.SemiBold
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = timeLeftText,
                style = MaterialTheme.typography.labelMedium,
                color = colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.width(12.dp))
            LinearProgressIndicator(
                progress = { progress.coerceIn(0f, 1f) },
                modifier = Modifier
                    .weight(1f)
                    .height(8.dp)
                    .clip(CircleShape),
                color = colorScheme.primary,
                trackColor = colorScheme.surfaceVariant
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = totalTimeText,
                style = MaterialTheme.typography.labelMedium,
                color = colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun QuizBackButton(
    onBackClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val layoutDirection = LocalLayoutDirection.current
    val iconOffset = if (layoutDirection == LayoutDirection.Ltr) 1.dp else (-1).dp
    Box(
        modifier = Modifier
            .size(36.dp)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.surface)
            .clickable(
                interactionSource = interactionSource,
                indication = ripple(bounded = true, radius = 20.dp),
                onClick = onBackClick
            ),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowBackIos,
            contentDescription = stringResource(Res.string.quiz_back),
            modifier = Modifier.size(18.dp).offset(x = iconOffset),
            tint = MaterialTheme.colorScheme.onSurface
        )
    }
}

@Preview
@Composable
private fun PreviewQuizTopAppBar() {
    BrainestTheme {
        QuizTopAppBar(
            title = stringResource(Res.string.quiz_playing_title),
            timeLeftText = "05:42",
            totalTimeText = "10:00",
            progress = 0.58f,
            onBackClick = {}
        )
    }
}
