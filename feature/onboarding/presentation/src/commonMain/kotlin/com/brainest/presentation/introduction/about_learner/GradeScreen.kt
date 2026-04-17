package com.brainest.presentation.introduction.about_learner

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
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
import brainest.feature.onboarding.presentation.generated.resources.elementary
import brainest.feature.onboarding.presentation.generated.resources.high_school
import brainest.feature.onboarding.presentation.generated.resources.middle_school
import brainest.feature.onboarding.presentation.generated.resources.postgraduate
import brainest.feature.onboarding.presentation.generated.resources.tailor_learning_path
import brainest.feature.onboarding.presentation.generated.resources.undergraduate
import brainest.feature.onboarding.presentation.generated.resources.what_is_your_grade
import brainest.feature.onboarding.presentation.generated.resources.your_profile_label
import com.brainest.presentation.introduction.components.OnboardingStepLayout
import com.brainest.presentation.introduction.components.SelectionOption
import com.scelio.brainest.designsystem.BrainestSuccess
import com.scelio.brainest.designsystem.BrainestTheme
import org.jetbrains.compose.resources.stringResource
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
private fun SelectionScreenWithSelectionPreview() {
    val sampleOptions = listOf(
        SelectionOptionData(
            id = "elementary",
            label = stringResource(Res.string.elementary),
            icon = null
        ),
        SelectionOptionData(
            id = "middle_school",
            label = stringResource(Res.string.middle_school),
            icon = null
        ),
        SelectionOptionData(
            id = "high_school",
            label = stringResource(Res.string.high_school),
            icon = null
        ),
        SelectionOptionData(
            id = "undergraduate",
            label = stringResource(Res.string.undergraduate),
            icon = null
        ),
        SelectionOptionData(
            id = "postgraduate",
            label = stringResource(Res.string.postgraduate),
            icon = null
        )
    )

    BrainestTheme {
        var selectedId by remember { mutableStateOf<String?>("high_school") }

        GradeScreen(
            title = stringResource(Res.string.what_is_your_grade),
            subtitle = stringResource(Res.string.tailor_learning_path),
            stepLabel = stringResource(Res.string.your_profile_label),
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
private fun SelectionScreenWithSelectionPreviewDark() {
    val sampleOptions = listOf(
        SelectionOptionData(
            id = "elementary",
            label = stringResource(Res.string.elementary),
            icon = null
        ),
        SelectionOptionData(
            id = "middle_school",
            label = stringResource(Res.string.middle_school),
            icon = null
        ),
        SelectionOptionData(
            id = "high_school",
            label = stringResource(Res.string.high_school),
            icon = null
        ),
        SelectionOptionData(
            id = "undergraduate",
            label = stringResource(Res.string.undergraduate),
            icon = null
        ),
        SelectionOptionData(
            id = "postgraduate",
            label = stringResource(Res.string.postgraduate),
            icon = null
        )
    )

    BrainestTheme(darkTheme = true) {
        var selectedId by remember { mutableStateOf<String?>("high_school") }

        GradeScreen(
            title = stringResource(Res.string.what_is_your_grade),
            subtitle = stringResource(Res.string.tailor_learning_path),
            stepLabel = stringResource(Res.string.your_profile_label),
            currentStep = 2,
            totalSteps = 5,
            options = sampleOptions,
            onOptionSelected = { selectedId = it },
            onContinueClicked = { /* Navigate next */ },
            selectedOptionId = selectedId
        )
    }
}
