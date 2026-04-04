package com.scelio.brainest.presentation.quiz.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.scelio.brainest.designsystem.BrainestTheme
import org.jetbrains.compose.ui.tooling.preview.Preview

enum class QuizOptionState {
    Default,
    Selected,
    Correct,
    Incorrect
}

@Composable
fun QuizOptionItem(
    text: String,
    state: QuizOptionState,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colorScheme = MaterialTheme.colorScheme
    val containerColor = when (state) {
        QuizOptionState.Default -> colorScheme.surface
        QuizOptionState.Selected -> colorScheme.secondaryContainer
        QuizOptionState.Correct -> colorScheme.tertiaryContainer
        QuizOptionState.Incorrect -> colorScheme.errorContainer
    }
    val contentColor = when (state) {
        QuizOptionState.Default -> colorScheme.onSurface
        QuizOptionState.Selected -> colorScheme.onSecondaryContainer
        QuizOptionState.Correct -> colorScheme.onTertiaryContainer
        QuizOptionState.Incorrect -> colorScheme.onErrorContainer
    }
    val borderColor = when (state) {
        QuizOptionState.Default -> colorScheme.outlineVariant
        QuizOptionState.Selected -> colorScheme.secondary
        QuizOptionState.Correct -> colorScheme.tertiary
        QuizOptionState.Incorrect -> colorScheme.error
    }

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        color = containerColor,
        border = BorderStroke(1.dp, borderColor)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 22.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            val indicatorColor = when (state) {
                QuizOptionState.Default -> colorScheme.onSurface
                QuizOptionState.Selected -> colorScheme.secondary
                QuizOptionState.Correct -> colorScheme.tertiary
                QuizOptionState.Incorrect -> colorScheme.error
            }
            OptionIndicator(
                isSelected = state != QuizOptionState.Default,
                color = indicatorColor
            )

            Spacer(modifier = Modifier.width(12.dp))

            Text(
                text = text,
                style = MaterialTheme.typography.bodyLarge,
                color = contentColor,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.weight(1f)
            )

            OptionBadge(state = state)
        }
    }
}

@Composable
private fun OptionIndicator(
    isSelected: Boolean,
    color: androidx.compose.ui.graphics.Color
) {
    val size = 22.dp
    val baseModifier = Modifier.size(size)
    if (isSelected) {
        Box(
            modifier = baseModifier.background(color, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .size(10.dp)
                    .background(MaterialTheme.colorScheme.surface, CircleShape)
            )
        }
    } else {
        Box(
            modifier = baseModifier.border(BorderStroke(2.dp, color), CircleShape)
        )
    }
}

@Composable
private fun OptionBadge(
    state: QuizOptionState
) {
    val label = when (state) {
        QuizOptionState.Correct -> "Correct answer"
        QuizOptionState.Incorrect -> "Your answer"
        else -> null
    }
    if (label != null) {
        val colorScheme = MaterialTheme.colorScheme
        val backgroundColor = when (state) {
            QuizOptionState.Correct -> colorScheme.tertiary
            QuizOptionState.Incorrect -> colorScheme.error
            else -> colorScheme.primary
        }
        val contentColor = when (state) {
            QuizOptionState.Correct -> colorScheme.onTertiary
            QuizOptionState.Incorrect -> colorScheme.onError
            else -> colorScheme.onPrimary
        }
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(50.dp))
                .background(backgroundColor)
                .padding(horizontal = 10.dp, vertical = 4.dp)
        ) {
            Text(
                text = label,
                color = contentColor,
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewQuizOptionItem() {
    BrainestTheme {
        Surface(color = MaterialTheme.colorScheme.surface) {
            Column(){
                QuizOptionItem(
                    text = "iPhone XR",
                    state = QuizOptionState.Correct,
                    onClick = {}
                )
                QuizOptionItem(
                    text = "iPhone XR",
                    state = QuizOptionState.Default,
                    onClick = {}
                )

            }
        }
    }
}
