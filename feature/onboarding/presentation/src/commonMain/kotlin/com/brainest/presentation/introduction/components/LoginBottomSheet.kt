package com.brainest.presentation.introduction.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import brainest.feature.onboarding.presentation.generated.resources.Res
import brainest.feature.onboarding.presentation.generated.resources.apple
import brainest.feature.onboarding.presentation.generated.resources.google
import brainest.feature.onboarding.presentation.generated.resources.mail
import com.scelio.brainest.designsystem.BrainestTheme
import com.scelio.brainest.designsystem.Typography
import com.scelio.brainest.designsystem.components.buttons.BrainestButton
import com.scelio.brainest.designsystem.components.buttons.BrainestButtonStyle
import org.jetbrains.compose.resources.vectorResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginBottomSheet(
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    onContinueWithApple: () -> Unit = {},
    onContinueWithGoogle: () -> Unit = {},
    onContinueWithEmail: () -> Unit = {},
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = Color.White,
        tonalElevation = 0.dp,
        scrimColor = Color.Transparent,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .padding(bottom = 48.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Spacer(modifier = Modifier.height(4.dp))

            // Continue with Apple

            BrainestButton(
                text = "Continue with Apple",
                onClick = onContinueWithApple,
                modifier = modifier.fillMaxWidth(),
                leadingIcon = {
                    Icon(
                        imageVector = vectorResource(Res.drawable.apple),
                        contentDescription = "",
                        modifier = modifier.size(20.dp),
                    )
                },
                textStyles = TextStyle(
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Medium,
                    fontFamily = Typography.bodyMedium.fontFamily,
                ),
                style = BrainestButtonStyle.PRIMARY
            )

            // Continue with Google
            BrainestButton(
                text = "Continue with Google",
                onClick = onContinueWithGoogle,
                modifier = modifier.fillMaxWidth(),
                leadingIcon = {
                    Icon(
                        imageVector = vectorResource(Res.drawable.google),
                        contentDescription = "",
                        modifier = modifier.size(24.dp),
                        tint = Color.Unspecified
                    )
                },
                textStyles = TextStyle(
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Medium,
                    fontFamily = Typography.bodyMedium.fontFamily,
                ),
                style = BrainestButtonStyle.SECONDARY
            )

            // Continue with Email
            BrainestButton(
                text = "Continue with Email",
                onClick = onContinueWithEmail,
                modifier = modifier.fillMaxWidth(),
                leadingIcon = {
                    Icon(
                        imageVector = vectorResource(Res.drawable.mail),
                        contentDescription = "",
                        modifier = modifier.size(22.dp),
                        tint = Color.Unspecified
                    )
                },
                textStyles = TextStyle(
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Medium,
                    fontFamily = Typography.bodyMedium.fontFamily,
                ),
                style = BrainestButtonStyle.SECONDARY
            )

        }
    }
}


@Preview(showBackground = true)
@Composable
fun PreviewLoginSheet() {
    BrainestTheme {
        LoginBottomSheet(onDismiss = {})

    }
}

@Preview(showBackground = true)
@Composable
fun PreviewDarkLoginSheet() {
    BrainestTheme(darkTheme = true) {
        LoginBottomSheet(onDismiss = {})

    }
}