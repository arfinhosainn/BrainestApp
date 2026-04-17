package com.brainest.presentation.introduction.personalization

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
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
import brainest.feature.onboarding.presentation.generated.resources.arabic
import brainest.feature.onboarding.presentation.generated.resources.chinese
import brainest.feature.onboarding.presentation.generated.resources.english
import brainest.feature.onboarding.presentation.generated.resources.french
import brainest.feature.onboarding.presentation.generated.resources.german
import brainest.feature.onboarding.presentation.generated.resources.preferred_language_subtitle
import brainest.feature.onboarding.presentation.generated.resources.preferred_language_title
import brainest.feature.onboarding.presentation.generated.resources.spanish
import brainest.feature.onboarding.presentation.generated.resources.ukrainian
import brainest.feature.onboarding.presentation.generated.resources.your_profile_label
import com.brainest.presentation.introduction.components.OnboardingStepLayout
import com.brainest.presentation.introduction.components.SelectionOption
import com.scelio.brainest.designsystem.BrainestSuccess
import com.scelio.brainest.designsystem.BrainestTheme
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource
import org.jetbrains.compose.ui.tooling.preview.Preview

data class LanguageData(
    val id: String,
    val label: String,
    val icon: @Composable (() -> Unit)? = null
)


@Composable
fun LanguageSelectionScreen(
    title: String,
    options: List<LanguageData>,
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
private fun LanguageSelectionScreenPreview() {
    val sampleOptions = listOf(
        LanguageData(
            id = "english",
            label = stringResource(Res.string.english),
            icon = {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(Color.Unspecified, CircleShape)
                ) {
                    Icon(
                        imageVector = vectorResource(Res.drawable.english),
                        contentDescription = "",
                        tint = Color.Unspecified
                    )

                }
            }
        ),
        LanguageData(
            id = "arabic",
            label = stringResource(Res.string.arabic),
            icon = {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(Color.LightGray, CircleShape)
                ){
                    Icon(
                        imageVector = vectorResource(Res.drawable.arabic),
                        contentDescription = "",
                        tint = Color.Unspecified
                    )
                }
            }
        ),
        LanguageData(
            id = "french",
            label = stringResource(Res.string.french),
            icon = {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(Color.LightGray, CircleShape)
                ){
                    Icon(
                        imageVector = vectorResource(Res.drawable.french),
                        contentDescription = "",
                        tint = Color.Unspecified
                    )
                }
            }
        ),
        LanguageData(
            id = "spanish",
            label = stringResource(Res.string.spanish),
            icon = {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(Color.LightGray, CircleShape)
                ){
                    Icon(
                        imageVector = vectorResource(Res.drawable.spanish),
                        contentDescription = "",
                        tint = Color.Unspecified
                    )
                }
            }
        ),
        LanguageData(
            id = "german",
            label = stringResource(Res.string.german),
            icon = {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(Color.LightGray, CircleShape)
                ){
                    Icon(
                        imageVector = vectorResource(Res.drawable.german),
                        contentDescription = "",
                        tint = Color.Unspecified
                    )
                }
            }
        ),
        LanguageData(
            id = "chinese",
            label = stringResource(Res.string.chinese),
            icon = {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(Color.LightGray, CircleShape)
                ){
                    Icon(
                        imageVector = vectorResource(Res.drawable.chinese),
                        contentDescription = "",
                        tint = Color.Unspecified
                    )
                }
            }
        ),
        LanguageData(
            id = "ukrainian",
            label = stringResource(Res.string.ukrainian),
            icon = {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(Color.LightGray, CircleShape)
                ){
                    Icon(
                        imageVector = vectorResource(Res.drawable.ukrainian),
                        contentDescription = "",
                        tint = Color.Unspecified
                    )
                }
            }
        )
    )

    BrainestTheme {
        var selectedId by remember { mutableStateOf<String?>(null) }

        LanguageSelectionScreen(
            title = stringResource(Res.string.preferred_language_title),
            subtitle = stringResource(Res.string.preferred_language_subtitle),
            stepLabel = stringResource(Res.string.your_profile_label),
            currentStep = 1,
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
private fun LanguageSelectionScreenWithSelectionPreview() {
    val sampleOptions = listOf(
        LanguageData(id = "english", label = stringResource(Res.string.english), icon = null),
        LanguageData(id = "french", label = stringResource(Res.string.french), icon = null),
        LanguageData(id = "spanish", label = stringResource(Res.string.spanish), icon = null),
        LanguageData(id = "german", label = stringResource(Res.string.german), icon = null),
        LanguageData(id = "chinese", label = stringResource(Res.string.chinese), icon = null),
        LanguageData(id = "ukrainian", label = stringResource(Res.string.ukrainian), icon = null)
    )

    BrainestTheme (darkTheme = true){
        var selectedId by remember { mutableStateOf<String?>("english") }

        LanguageSelectionScreen(
            title = stringResource(Res.string.preferred_language_title),
            subtitle = stringResource(Res.string.preferred_language_subtitle),
            stepLabel = stringResource(Res.string.your_profile_label),
            currentStep = 1,
            totalSteps = 5,
            options = sampleOptions,
            onOptionSelected = { selectedId = it },
            onContinueClicked = { /* Navigate next */ },
            selectedOptionId = selectedId
        )
    }
}
