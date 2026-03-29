package com.brainest.presentation.introduction.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.scelio.brainest.designsystem.BrainestTheme
import com.scelio.brainest.designsystem.BricolageGrotesq
import com.scelio.brainest.designsystem.Typography
import com.scelio.brainest.designsystem.components.buttons.BrainestButton
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun OnboardingStepLayout(
    title: String,
    currentStep: Int,
    totalSteps: Int,
    onContinueClicked: () -> Unit,
    continueButtonEnabled: Boolean,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
    stepLabel: String? = null,
    backgroundColor: Color = Color(0xFFF5E9E1),
    progressColor: Color = Color(0xFF6B5BCD),
    continueButtonText: String = "Continue",
    showProgressIndicator: Boolean = true,
    content: @Composable () -> Unit
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(backgroundColor)
            .statusBarsPadding()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp)
        ) {
            Spacer(modifier = Modifier.height(24.dp))

            // Progress Indicator
            if (showProgressIndicator) {
                StepProgressIndicator(
                    currentStep = currentStep,
                    totalSteps = totalSteps,
                    stepLabel = stepLabel,
                    progressColor = progressColor
                )

                Spacer(modifier = Modifier.height(40.dp))
            }

            // Title Section
            Text(
                text = title,
                style = TextStyle(
                    fontFamily = BricolageGrotesq,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.ExtraBold,
                    lineHeight = 36.sp,
                    color = Color(0xFF2C201F)
                ),
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )

            if (subtitle != null) {
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = subtitle,
                    style = TextStyle(
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        fontFamily = BricolageGrotesq,
                        color = Color(0xFF2C201F)
                    ),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Custom Content Area
            Box(
                modifier = Modifier.weight(1f),
                contentAlignment = Alignment.TopCenter
            ) {
                content()
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Continue Button
            BrainestButton(
                text = continueButtonText,
                onClick = onContinueClicked,
                enabled = continueButtonEnabled,
                textStyles = TextStyle(
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    fontFamily = Typography.bodyMedium.fontFamily,
                    color = Color.White
                ),
                modifier = Modifier.fillMaxWidth(),
                trailingIcon = {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                        contentDescription = null,
                        tint = Color.White
                    )
                }
            )

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Preview
@Composable
private fun OnboardingStepTemplatePreview() {
    BrainestTheme {
        OnboardingStepLayout(
            title = "Choose your child age:",
            currentStep = 2,
            totalSteps = 5,
            stepLabel = "Children Personality",
            onContinueClicked = { },
            continueButtonEnabled = true
        ) {
            // Sample content - could be selection options, input fields, etc.
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                repeat(5) { _ ->
                    SelectionOption(
                        label = "6 to 12 months",
                        isSelected = true,
                        onClick = {},
                        icon = {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .background(Color.LightGray, CircleShape)
                            )
                        }
                    )
                }
            }
        }
    }
}

@Preview
@Composable
private fun OnboardingStepTemplateWithSubtitlePreview() {
    BrainestTheme {
        OnboardingStepLayout(
            title = "What's your name?",
            subtitle = "This helps us personalize your experience",
            currentStep = 1,
            totalSteps = 3,
            stepLabel = "Step",
            onContinueClicked = { },
            continueButtonEnabled = false
        ) {
            // Sample input placeholder
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .background(
                        Color.White,
                        RoundedCornerShape(12.dp)
                    ),
                contentAlignment = Alignment.CenterStart
            ) {
                Text(
                    "Enter your name...",
                    modifier = Modifier.padding(horizontal = 16.dp),
                    color = Color.Gray
                )
            }
        }
    }
}
