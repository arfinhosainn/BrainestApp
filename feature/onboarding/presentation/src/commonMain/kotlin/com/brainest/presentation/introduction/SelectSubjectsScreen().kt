package com.brainest.presentation.introduction

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.brainest.presentation.introduction.components.MultiSelectChip
import com.brainest.presentation.introduction.components.OnboardingStepLayout
import com.scelio.brainest.designsystem.BrainestSuccess
import com.scelio.brainest.designsystem.BrainestTheme
import com.scelio.brainest.designsystem.Typography
import org.jetbrains.compose.ui.tooling.preview.Preview

data class MultiSelectOptionData(
    val id: String,
    val label: String
)

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SelectSubjectScreen(
    title: String,
    options: List<MultiSelectOptionData>,
    selectedOptionIds: Set<String>,
    onOptionToggle: (String) -> Unit,
    onContinueClicked: () -> Unit,
    currentStep: Int,
    totalSteps: Int,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
    stepLabel: String? = null,
    accentColor: Color = BrainestSuccess,
    onNoneOfTheAboveClicked: (() -> Unit)? = null,
    noneOfTheAboveText: String = "None of the above",
    allowEmptySelection: Boolean = false
) {
    val hasSelection = selectedOptionIds.isNotEmpty()
    val canContinue = hasSelection || allowEmptySelection

    OnboardingStepLayout(
        title = title,
        subtitle = subtitle,
        currentStep = currentStep,
        totalSteps = totalSteps,
        stepLabel = stepLabel,
        onContinueClicked = onContinueClicked,
        continueButtonEnabled = canContinue,
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Multi-Select Chip Options
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp, Alignment.Start),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                options.forEach { option ->
                    MultiSelectChip(
                        label = option.label,
                        isSelected = selectedOptionIds.contains(option.id),
                        onClick = { onOptionToggle(option.id) },
                        selectedColor = accentColor,
                        selectionId = option.id
                    )
                }
            }

            // None of the above option
            if (onNoneOfTheAboveClicked != null) {
                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = noneOfTheAboveText,
                    style = Typography.bodyMedium.copy(
                        fontSize = 16.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                        textDecoration = TextDecoration.Underline
                    ),
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .clickable(onClick = onNoneOfTheAboveClicked)
                        .padding(vertical = 8.dp)
                )
            }
        }
    }
}

@Preview
@Composable
private fun PreviewSelectSubjectScreen() {
    val subjects = listOf(
        MultiSelectOptionData("math", "Mathematics"),
        MultiSelectOptionData("science", "Science"),
        MultiSelectOptionData("physics", "Physics"),
        MultiSelectOptionData("chemistry", "Chemistry"),
        MultiSelectOptionData("biology", "Biology"),
        MultiSelectOptionData("history", "History"),
        MultiSelectOptionData("geography", "Geography"),
        MultiSelectOptionData("english", "English"),
        MultiSelectOptionData("literature", "Literature"),
        MultiSelectOptionData("computer_science", "Computer Science"),
        MultiSelectOptionData("economics", "Economics"),
        MultiSelectOptionData("psychology", "Psychology")
    )

    BrainestTheme {
        var selectedIds by remember { mutableStateOf(setOf("come")) }

        SelectSubjectScreen(
            title = "What subjects are you studying?",
            subtitle = "Select all that apply, we'll personalize your learning experience",
            stepLabel = "Command skills",
            currentStep = 1,
            totalSteps = 2,
            options = subjects,
            selectedOptionIds = selectedIds,
            onOptionToggle = { id ->
                selectedIds = if (selectedIds.contains(id)) {
                    selectedIds - id
                } else {
                    selectedIds + id
                }
            },
            onContinueClicked = { /* Navigate next */ },
            onNoneOfTheAboveClicked = { /* Handle none */ }
        )
    }
}

@Preview
@Composable
private fun MultiSelectionScreenEmptyPreview() {
    val subjects = listOf(
        MultiSelectOptionData("math", "Mathematics"),
        MultiSelectOptionData("science", "Science"),
        MultiSelectOptionData("physics", "Physics"),
        MultiSelectOptionData("chemistry", "Chemistry"),
        MultiSelectOptionData("biology", "Biology"),
        MultiSelectOptionData("history", "History"),
        MultiSelectOptionData("geography", "Geography"),
        MultiSelectOptionData("english", "English"),
        MultiSelectOptionData("literature", "Literature"),
        MultiSelectOptionData("computer_science", "Computer Science"),
        MultiSelectOptionData("economics", "Economics"),
        MultiSelectOptionData("psychology", "Psychology")
    )

    BrainestTheme {
        var selectedIds by remember { mutableStateOf(setOf<String>()) }

        SelectSubjectScreen(
            title = "What subjects are you studying?",
            subtitle = "Mark all that your dog knows for sure!",
            stepLabel = "Command skills",
            currentStep = 1,
            totalSteps = 2,
            options = subjects,
            selectedOptionIds = selectedIds,
            onOptionToggle = { id ->
                selectedIds = if (selectedIds.contains(id)) {
                    selectedIds - id
                } else {
                    selectedIds + id
                }
            },
            onContinueClicked = { /* Navigate next */ },
            onNoneOfTheAboveClicked = { /* Handle none */ }
        )
    }
}
