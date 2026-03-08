package com.brainest.presentation.introduction.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.scelio.brainest.designsystem.BrainestTheme
import com.scelio.brainest.designsystem.Typography
import org.jetbrains.compose.ui.tooling.preview.Preview


@Composable
fun StepProgressIndicator(
    currentStep: Int,
    totalSteps: Int,
    modifier: Modifier = Modifier,
    stepLabel: String? = null,
    progressColor: Color = Color(0xFF6B5BCD), // Default purple
    trackColor: Color = Color.LightGray.copy(alpha = 0.3f),
    textStyle: TextStyle = Typography.bodyMedium.copy(
        color = progressColor,
        fontWeight = FontWeight.Medium,
        fontSize = 16.sp
    )
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Step text label
        val stepText = if (stepLabel != null) {
            "$stepLabel $currentStep/$totalSteps"
        } else {
            "$currentStep/$totalSteps"
        }

        Text(
            text = stepText,
            style = textStyle,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Progress Bar
        LinearProgressIndicator(
            progress = { currentStep / totalSteps.toFloat() },
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .clip(RoundedCornerShape(3.dp)),
            color = progressColor,
            trackColor = trackColor
        )
    }
}

@Preview
@Composable
private fun StepProgressIndicatorWithLabelPreview() {
    BrainestTheme {
        StepProgressIndicator(
            currentStep = 2,
            totalSteps = 5,
            stepLabel = "Dog's personality"
        )
    }
}

@Preview
@Composable
private fun StepProgressIndicatorWithoutLabelPreview() {
    BrainestTheme {
        StepProgressIndicator(
            currentStep = 3,
            totalSteps = 8,
            progressColor = MaterialTheme.colorScheme.primary
        )
    }
}

@Preview
@Composable
private fun StepProgressIndicatorFirstStepPreview() {
    BrainestTheme {
        StepProgressIndicator(
            currentStep = 1,
            totalSteps = 4,
            stepLabel = "Question"
        )
    }
}

@Preview
@Composable
private fun StepProgressIndicatorLastStepPreview() {
    BrainestTheme {
        StepProgressIndicator(
            currentStep = 4,
            totalSteps = 4,
            stepLabel = "Step",
            progressColor = Color(0xFF34C759) // Green
        )
    }
}
