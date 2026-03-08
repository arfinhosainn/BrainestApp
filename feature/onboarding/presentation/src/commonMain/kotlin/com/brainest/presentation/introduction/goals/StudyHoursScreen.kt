package com.brainest.presentation.introduction.goals

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.brainest.presentation.introduction.components.NumberPicker
import com.brainest.presentation.introduction.components.OnboardingStepLayout
import com.scelio.brainest.designsystem.BrainestSuccess
import com.scelio.brainest.designsystem.BrainestTheme
import org.jetbrains.compose.ui.tooling.preview.Preview


@Composable
fun StudyHoursScreen(
    title: String,
    hours: Int,
    onHoursChange: (Int) -> Unit,
    onContinueClicked: () -> Unit,
    currentStep: Int,
    totalSteps: Int,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
    stepLabel: String? = null,
    maxHours: Int = 18,
    accentColor: Color = BrainestSuccess
) {
    OnboardingStepLayout(
        title = title,
        subtitle = subtitle,
        currentStep = currentStep,
        totalSteps = totalSteps,
        stepLabel = stepLabel,
        onContinueClicked = onContinueClicked,
        continueButtonEnabled = true, // Always enabled since we have a default value
        modifier = modifier
    ) {
        // Number Picker Wheel
        NumberPicker(
            value = hours,
            onValueChange = onHoursChange,
            range = 0..maxHours,
            modifier = Modifier.fillMaxWidth(),
            itemHeight = 80,
            visibleItemsCount = 3
        )
    }
}

@Preview
@Composable
private fun StudyHoursScreenPreview() {
    BrainestTheme {
        var hours by remember { mutableIntStateOf(9) }

        StudyHoursScreen(
            title = "How many hours do you study daily?",
            subtitle = "We'll create a plan that fits your schedule",
            stepLabel = "Your Goals",
            currentStep = 2,
            totalSteps = 5,
            hours = hours,
            onHoursChange = { hours = it },
            onContinueClicked = { /* Navigate next */ }
        )
    }
}

@Preview
@Composable
private fun StudyHoursScreenWithDifferentValuePreview() {
    BrainestTheme {
        var hours by remember { mutableIntStateOf(5) }

        StudyHoursScreen(
            title = "Daily study hours",
            subtitle = "Select your average study time",
            stepLabel = "Goals",
            currentStep = 3,
            totalSteps = 4,
            hours = hours,
            onHoursChange = { hours = it },
            onContinueClicked = { /* Navigate next */ }
        )
    }
}
