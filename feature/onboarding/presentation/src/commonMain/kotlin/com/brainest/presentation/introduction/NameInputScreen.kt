package com.brainest.presentation.introduction

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.brainest.presentation.introduction.components.OnboardingStepLayout
import com.scelio.brainest.designsystem.BrainestSuccess
import com.scelio.brainest.designsystem.BrainestTheme
import com.scelio.brainest.designsystem.Nunito
import org.jetbrains.compose.ui.tooling.preview.Preview
@Composable
fun NameInputScreen(
    title: String,
    inputValue: String,
    onInputValueChange: (String) -> Unit,
    onContinueClicked: () -> Unit,
    currentStep: Int,
    totalSteps: Int,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
    stepLabel: String? = null,
    placeholder: String = "Your Name",
    accentColor: Color = BrainestSuccess,
    keyboardType: KeyboardType = KeyboardType.Text,
    maxLines: Int = 1
) {
    val canContinue = inputValue.isNotBlank()

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
            modifier = Modifier.fillMaxWidth()
        ) {
            // Input Field with underline only style
            BasicTextField(
                value = inputValue,
                onValueChange = onInputValueChange,
                modifier = Modifier.fillMaxWidth(),
                singleLine = maxLines == 1,
                maxLines = maxLines,
                keyboardOptions = KeyboardOptions(
                    keyboardType = keyboardType,
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(
                    onDone = { if (canContinue) onContinueClicked() }
                ),
                textStyle = TextStyle(
                    fontFamily = Nunito,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 28.sp,
                    color = Color(0xFF2C201F)
                ),
                decorationBox = { innerTextField ->
                    Column {
                        // Placeholder or Input
                        Box(
                            modifier = Modifier.fillMaxWidth(),
                            contentAlignment = Alignment.CenterStart
                        ) {
                            if (inputValue.isEmpty() && placeholder.isNotEmpty()) {
                                Text(
                                    text = placeholder,
                                    style = TextStyle(
                                        fontFamily = Nunito,
                                        fontWeight = FontWeight.ExtraBold,
                                        fontSize = 28.sp,
                                        color = Color(0xFFBDBDBD) // Light gray for placeholder
                                    )
                                )
                            }
                            innerTextField()
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Bottom underline only
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(2.dp)
                                .background(
                                    if (inputValue.isNotEmpty()) accentColor
                                    else Color(0xFFE0E0E0)
                                )
                        )
                    }
                }
            )
        }
    }
}

@Preview
@Composable
private fun NameInputEmptyScreenPreview() {
    BrainestTheme {
        var name by remember { mutableStateOf("") }

        NameInputScreen(
            title = "What's your name?",
            subtitle = "This helps us personalize your experience",
            stepLabel = "Dog's personality",
            currentStep = 1,
            totalSteps = 5,
            inputValue = name,
            onInputValueChange = { name = it },
            onContinueClicked = { /* Navigate next */ },
            placeholder = "Your Name"
        )
    }
}

@Preview
@Composable
private fun NameInputFilledScreenPreview() {
    BrainestTheme {
        var name by remember { mutableStateOf("John") }

        NameInputScreen(
            title = "What's your name?",
            subtitle = "This helps us personalize your experience",
            stepLabel = "Step",
            currentStep = 1,
            totalSteps = 3,
            inputValue = name,
            onInputValueChange = { name = it },
            onContinueClicked = { /* Navigate next */ },
            placeholder = "Your Name"
        )
    }
}

