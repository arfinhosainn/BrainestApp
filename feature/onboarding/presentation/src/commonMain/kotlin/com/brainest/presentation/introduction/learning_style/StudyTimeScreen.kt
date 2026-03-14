package com.brainest.presentation.introduction.learning_style

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.brainest.presentation.introduction.components.OnboardingStepLayout
import com.brainest.presentation.introduction.components.SelectionOption
import com.scelio.brainest.designsystem.BrainestSuccess
import com.scelio.brainest.designsystem.BrainestTheme
import org.jetbrains.compose.ui.tooling.preview.Preview

data class StudyTimeData(
    val id: String,
    val label: String,
    val icon: @Composable (() -> Unit)? = null
)


@Composable
fun StudyTimeScreen(
    title: String,
    options: List<StudyTimeData>,
    selectedOptionId: String?,
    onOptionSelected: (String) -> Unit,
    onContinueClicked: () -> Unit,
    currentStep: Int,
    totalSteps: Int,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
    stepLabel: String? = null,
    accentColor: Color = BrainestSuccess
) {
    OnboardingStepLayout(
        title = title,
        subtitle = subtitle,
        currentStep = currentStep,
        totalSteps = totalSteps,
        stepLabel = stepLabel,
        onContinueClicked = onContinueClicked,
        continueButtonEnabled = selectedOptionId != null,
        modifier = modifier
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            itemsIndexed(
                items = options,
                key = { _, option -> option.id }
            ) { _, option ->
                SelectionOption(
                    label = option.label,
                    isSelected = selectedOptionId == option.id,
                    onClick = { onOptionSelected(option.id) },
                    icon = option.icon,
                    selectedColor = accentColor,
                    selectionId = option.id,
                    modifier = Modifier.animateContentSize()
                )
            }
        }
    }
}


@Preview
@Composable
private fun StudyTimeScreenPreview() {
    val sampleOptions = listOf(
        StudyTimeData(
            id = "early_morning",
            label = "Early Morning (5AM - 8AM)",
            icon = {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(Color.LightGray, CircleShape)
                )
            }
        ),
        StudyTimeData(
            id = "morning",
            label = "Morning (8AM - 12PM)",
            icon = {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(Color.LightGray, CircleShape)
                )
            }
        ),
        StudyTimeData(
            id = "afternoon",
            label = "Afternoon (12PM - 5PM)",
            icon = {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(Color.LightGray, CircleShape)
                )
            }
        ),
        StudyTimeData(
            id = "evening",
            label = "Evening (5PM - 9PM)",
            icon = {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(Color.LightGray, CircleShape)
                )
            }
        ),
        StudyTimeData(
            id = "night",
            label = "Night (9PM - 12AM)",
            icon = {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(Color.LightGray, CircleShape)
                )
            }
        )
    )

    BrainestTheme {
        var selectedId by remember { mutableStateOf<String?>(null) }

        StudyTimeScreen(
            title = "When do you usually study?",
            subtitle = "We'll schedule your sessions at the right time",
            stepLabel = "Your Profile",
            currentStep = 5,
            totalSteps = 5,
            options = sampleOptions,
            onOptionSelected = { selectedId = it },
            onContinueClicked = { /* Navigate next */ },
            selectedOptionId = selectedId
        )
    }
}

@Preview
@Composable
private fun StudyTimeScreenWithSelectionPreview() {
    val sampleOptions = listOf(
        StudyTimeData(id = "early_morning", label = "Early Morning (5AM - 8AM)", icon = null),
        StudyTimeData(id = "morning", label = "Morning (8AM - 12PM)", icon = null),
        StudyTimeData(id = "afternoon", label = "Afternoon (12PM - 5PM)", icon = null),
        StudyTimeData(id = "evening", label = "Evening (5PM - 9PM)", icon = null),
        StudyTimeData(id = "night", label = "Night (9PM - 12AM)", icon = null)
    )

    BrainestTheme(darkTheme = true) {
        var selectedId by remember { mutableStateOf<String?>("evening") }

        StudyTimeScreen(
            title = "When do you usually study?",
            subtitle = "We'll schedule your sessions at the right time",
            stepLabel = "Your Profile",
            currentStep = 5,
            totalSteps = 5,
            options = sampleOptions,
            onOptionSelected = { selectedId = it },
            onContinueClicked = { /* Navigate next */ },
            selectedOptionId = selectedId
        )
    }
}