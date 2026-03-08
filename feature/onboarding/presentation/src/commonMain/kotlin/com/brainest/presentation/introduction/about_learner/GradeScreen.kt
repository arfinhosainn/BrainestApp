package com.brainest.presentation.introduction.about_learner

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


data class SelectionOptionData(
    val id: String,
    val label: String,
    val icon: @Composable (() -> Unit)? = null
)


@Composable
fun GradeScreen(
    title: String,
    options: List<SelectionOptionData>,
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
        // Selection Options List
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
private fun SelectionScreenPreview() {
    val sampleOptions = listOf(
        SelectionOptionData(
            id = "elementary",
            label = "Elementary (Grade 1-5)",
            icon = {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(Color.LightGray, CircleShape)
                )
            }
        ),
        SelectionOptionData(
            id = "middle_school",
            label = "Middle School (Grade 6-8)",
            icon = {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(Color.LightGray, CircleShape)
                )
            }
        ),
        SelectionOptionData(
            id = "high_school",
            label = "High School (Grade 9-12)",
            icon = {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(Color.LightGray, CircleShape)
                )
            }
        ),
        SelectionOptionData(
            id = "undergraduate",
            label = "Undergraduate",
            icon = {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(Color.LightGray, CircleShape)
                )
            }
        ),
        SelectionOptionData(
            id = "postgraduate",
            label = "Postgraduate",
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

        GradeScreen(
            title = "What's your grade level?",
            subtitle = "We'll tailor your learning path accordingly",
            stepLabel = "Your Profile",
            currentStep = 2,
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
private fun SelectionScreenWithSelectionPreview() {
    val sampleOptions = listOf(
        SelectionOptionData(
            id = "elementary",
            label = "Elementary (Grade 1-5)",
            icon = null
        ),
        SelectionOptionData(
            id = "middle_school",
            label = "Middle School (Grade 6-8)",
            icon = null
        ),
        SelectionOptionData(
            id = "high_school",
            label = "High School (Grade 9-12)",
            icon = null
        ),
        SelectionOptionData(
            id = "undergraduate",
            label = "Undergraduate",
            icon = null
        ),
        SelectionOptionData(
            id = "postgraduate",
            label = "Postgraduate",
            icon = null
        )
    )

    BrainestTheme {
        var selectedId by remember { mutableStateOf<String?>("high_school") }

        GradeScreen(
            title = "What's your grade level?",
            subtitle = "We'll tailor your learning path accordingly",
            stepLabel = "Your Profile",
            currentStep = 2,
            totalSteps = 5,
            options = sampleOptions,
            onOptionSelected = { selectedId = it },
            onContinueClicked = { /* Navigate next */ },
            selectedOptionId = selectedId
        )
    }
}
