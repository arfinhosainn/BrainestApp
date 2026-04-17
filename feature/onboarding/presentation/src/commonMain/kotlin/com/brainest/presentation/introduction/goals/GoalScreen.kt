package com.brainest.presentation.introduction.goals


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
import brainest.feature.onboarding.presentation.generated.resources.Res
import brainest.feature.onboarding.presentation.generated.resources.build_plan_goal
import brainest.feature.onboarding.presentation.generated.resources.get_ahead
import brainest.feature.onboarding.presentation.generated.resources.improve_grades
import brainest.feature.onboarding.presentation.generated.resources.learn_new_skills
import brainest.feature.onboarding.presentation.generated.resources.prepare_exams
import brainest.feature.onboarding.presentation.generated.resources.stay_consistent
import brainest.feature.onboarding.presentation.generated.resources.what_is_your_goal
import brainest.feature.onboarding.presentation.generated.resources.your_profile_label
import com.brainest.presentation.introduction.about_learner.SelectionOptionData
import com.brainest.presentation.introduction.components.OnboardingStepLayout
import com.brainest.presentation.introduction.components.SelectionOption
import com.scelio.brainest.designsystem.BrainestSuccess
import com.scelio.brainest.designsystem.BrainestTheme
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview


data class SelectGradeOption(
    val id: String,
    val label: String,
    val icon: @Composable (() -> Unit)? = null
)


@Composable
fun GoalScreen(
    title: String,
    options: List<SelectGradeOption>,
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
        SelectGradeOption(
            id = "improve_grades",
            label = stringResource(Res.string.improve_grades),
            icon = {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(Color.LightGray, CircleShape)
                )
            }
        ),
        SelectGradeOption(
            id = "prepare_exams",
            label = stringResource(Res.string.prepare_exams),
            icon = {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(Color.LightGray, CircleShape)
                )
            }
        ),
        SelectGradeOption(
            id = "learn_new_skills",
            label = stringResource(Res.string.learn_new_skills),
            icon = {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(Color.LightGray, CircleShape)
                )
            }
        ),
        SelectGradeOption(
            id = "stay_consistent",
            label = stringResource(Res.string.stay_consistent),
            icon = {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(Color.LightGray, CircleShape)
                )
            }
        ),
        SelectGradeOption(
            id = "get_ahead",
            label = stringResource(Res.string.get_ahead),
            icon = {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(Color.LightGray, CircleShape)
                )
            }
        )
    )

    BrainestTheme(darkTheme = true) {
        var selectedId by remember { mutableStateOf<String?>(null) }

        GoalScreen(
            title = stringResource(Res.string.what_is_your_goal),
            subtitle = stringResource(Res.string.build_plan_goal),
            stepLabel = stringResource(Res.string.your_profile_label),
            currentStep = 3,
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
        SelectGradeOption(
            id = "improve_grades",
            label = stringResource(Res.string.improve_grades),
            icon = null
        ),
        SelectGradeOption(
            id = "prepare_exams",
            label = stringResource(Res.string.prepare_exams),
            icon = null
        ),
        SelectGradeOption(
            id = "learn_new_skills",
            label = stringResource(Res.string.learn_new_skills),
            icon = null
        ),
        SelectGradeOption(
            id = "stay_consistent",
            label = stringResource(Res.string.stay_consistent),
            icon = null
        ),
        SelectGradeOption(
            id = "get_ahead",
            label = stringResource(Res.string.get_ahead),
            icon = null
        )
    )

    BrainestTheme {
        var selectedId by remember { mutableStateOf<String?>("prepare_exams") }

        GoalScreen(
            title = stringResource(Res.string.what_is_your_goal),
            subtitle = stringResource(Res.string.build_plan_goal),
            stepLabel = stringResource(Res.string.your_profile_label),
            currentStep = 3,
            totalSteps = 5,
            options = sampleOptions,
            onOptionSelected = { selectedId = it },
            onContinueClicked = { /* Navigate next */ },
            selectedOptionId = selectedId
        )
    }
}
