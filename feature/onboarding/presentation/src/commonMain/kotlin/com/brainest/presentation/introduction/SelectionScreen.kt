package com.brainest.presentation.introduction

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

/**
 * Data class representing a single selectable option.
 *
 * @param id Unique identifier for database storage
 * @param label Display text for the option
 * @param icon Optional icon composable
 */
data class SelectionOptionData(
    val id: String,
    val label: String,
    val icon: @Composable (() -> Unit)? = null
)

/**
 * A selection screen that uses the OnboardingStepTemplate.
 * Displays a list of selectable options with single-selection behavior.
 *
 * @param title The main title text
 * @param options List of selectable options
 * @param selectedOptionId Currently selected option id (for controlled state)
 * @param onOptionSelected Callback when an option is selected, returns the selected option id
 * @param onContinueClicked Callback when continue button is clicked
 * @param currentStep Current step number (for progress indicator)
 * @param totalSteps Total number of steps (for progress indicator)
 * @param modifier Modifier for the screen
 * @param subtitle Optional subtitle text
 * @param stepLabel Optional label for step counter (e.g., "Dog's personality", "Step")
 * @param accentColor Color for selected state
 */
@Composable
fun SelectionScreen(
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

// Preview with sample data
@Preview
@Composable
private fun SelectionScreenPreview() {
    val sampleOptions = listOf(
        SelectionOptionData(
            id = "younger_than_6_months",
            label = "Younger than 6 months",
            icon = {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(Color.LightGray, CircleShape)
                )
            }
        ),
        SelectionOptionData(
            id = "6_to_12_months",
            label = "6 to 12 months",
            icon = {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(Color.LightGray, CircleShape)
                )
            }
        ),
        SelectionOptionData(
            id = "1_to_2_years",
            label = "1 to 2 years",
            icon = {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(Color.LightGray, CircleShape)
                )
            }
        ),
        SelectionOptionData(
            id = "2_to_7_years",
            label = "2 to 7 years",
            icon = {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(Color.LightGray, CircleShape)
                )
            }
        ),
        SelectionOptionData(
            id = "over_7_years",
            label = "Over 7 years",
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

        SelectionScreen(
            title = "Choose your dog's age:",
            stepLabel = "Dog's personality",
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
            id = "option_1",
            label = "Option 1",
            icon = null
        ),
        SelectionOptionData(
            id = "option_2",
            label = "Option 2",
            icon = null
        ),
        SelectionOptionData(
            id = "option_3",
            label = "Option 3",
            icon = null
        )
    )

    BrainestTheme {
        var selectedId by remember { mutableStateOf<String?>("option_2") }

        SelectionScreen(
            title = "Select an option:",
            subtitle = "Choose the best option for you",
            stepLabel = "Step",
            currentStep = 1,
            totalSteps = 3,
            options = sampleOptions,
            onOptionSelected = { selectedId = it },
            onContinueClicked = { /* Navigate next */ },
            selectedOptionId = selectedId
        )
    }
}
