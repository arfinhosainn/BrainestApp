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

data class LearningMethodsData(
    val id: String,
    val label: String,
    val icon: @Composable (() -> Unit)? = null
)


@Composable
fun LearningMethodScreen(
    title: String,
    options: List<LearningMethodsData>,
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
private fun LearningMethodScreenPreview() {
    val sampleOptions = listOf(
        LearningMethodsData(
            id = "videos",
            label = "Video Lessons",
            icon = {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(Color.LightGray, CircleShape)
                )
            }
        ),
        LearningMethodsData(
            id = "quizzes",
            label = "Quizzes & Practice Tests",
            icon = {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(Color.LightGray, CircleShape)
                )
            }
        ),
        LearningMethodsData(
            id = "flashcards",
            label = "Flashcards",
            icon = {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(Color.LightGray, CircleShape)
                )
            }
        ),
        LearningMethodsData(
            id = "reading",
            label = "Reading & Summaries",
            icon = {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(Color.LightGray, CircleShape)
                )
            }
        ),
        LearningMethodsData(
            id = "interactive",
            label = "Interactive Exercises",
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

        LearningMethodScreen(
            title = "How do you learn best?",
            subtitle = "We'll personalize your experience based on your style",
            stepLabel = "Your Profile",
            currentStep = 4,
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
private fun LearningMethodScreenWithSelectionPreview() {
    val sampleOptions = listOf(
        LearningMethodsData(
            id = "videos",
            label = "Video Lessons",
            icon = null
        ),
        LearningMethodsData(
            id = "quizzes",
            label = "Quizzes & Practice Tests",
            icon = null
        ),
        LearningMethodsData(
            id = "flashcards",
            label = "Flashcards",
            icon = null
        ),
        LearningMethodsData(
            id = "reading",
            label = "Reading & Summaries",
            icon = null
        ),
        LearningMethodsData(
            id = "interactive",
            label = "Interactive Exercises",
            icon = null
        )
    )

    BrainestTheme {
        var selectedId by remember { mutableStateOf<String?>("flashcards") }

        LearningMethodScreen(
            title = "How do you learn best?",
            subtitle = "We'll personalize your experience based on your style",
            stepLabel = "Your Profile",
            currentStep = 4,
            totalSteps = 5,
            options = sampleOptions,
            onOptionSelected = { selectedId = it },
            onContinueClicked = { /* Navigate next */ },
            selectedOptionId = selectedId
        )
    }
}

