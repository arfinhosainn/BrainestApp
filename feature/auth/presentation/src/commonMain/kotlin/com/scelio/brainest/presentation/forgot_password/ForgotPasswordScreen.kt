package com.scelio.brainest.presentation.forgot_password

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import brainest.feature.auth.presentation.generated.resources.Res
import brainest.feature.auth.presentation.generated.resources.email
import brainest.feature.auth.presentation.generated.resources.email_placeholder
import brainest.feature.auth.presentation.generated.resources.forgot_password
import brainest.feature.auth.presentation.generated.resources.forgot_password_email_sent_successfully
import brainest.feature.auth.presentation.generated.resources.submit
import com.scelio.brainest.designsystem.BrainestTheme
import com.scelio.brainest.designsystem.components.brand.BrainestBrandLogo
import com.scelio.brainest.designsystem.components.buttons.BrainestButton
import com.scelio.brainest.designsystem.components.layouts.BrainestAdaptiveFormLayout
import com.scelio.brainest.designsystem.components.textfields.BrainestTextField
import com.scelio.brainest.designsystem.extended
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun ForgotPasswordRoot(
    viewModel: ForgotPasswordViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    ForgotPasswordScreen(
        state = state,
        onAction = viewModel::onAction
    )
}

@Composable
fun ForgotPasswordScreen(
    state: ForgotPasswordState,
    onAction: (ForgotPasswordAction) -> Unit,
) {
    BrainestAdaptiveFormLayout(
        headerText = stringResource(Res.string.forgot_password),
        errorText = state.errorText?.asString(),
        logo = {
            BrainestBrandLogo()
        }
    ) {
        BrainestTextField(
            state = state.emailTextFieldState,
            modifier = Modifier
                .fillMaxWidth(),
            placeholder = stringResource(Res.string.email_placeholder),
            title = stringResource(Res.string.email),
            isError = state.errorText != null,
            supportingText = state.errorText?.asString(),
            keyboardType = KeyboardType.Email,
            singleLine = true
        )
        Spacer(modifier = Modifier.height(16.dp))
        BrainestButton(
            text = stringResource(Res.string.submit),
            onClick = {
                onAction(ForgotPasswordAction.OnSubmitClick)
            },
            modifier = Modifier
                .fillMaxWidth(),
            enabled = !state.isLoading && state.canSubmit,
            isLoading = state.isLoading
        )
        Spacer(modifier = Modifier.height(8.dp))
        if(state.isEmailSentSuccessfully) {
            Text(
                text = stringResource(Res.string.forgot_password_email_sent_successfully),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.extended.success,
                modifier = Modifier
                    .fillMaxWidth(),
                textAlign = TextAlign.Center
            )
        }
    }
}

@Preview
@Composable
private fun Preview() {
    BrainestTheme {
        ForgotPasswordScreen(
            state = ForgotPasswordState(),
            onAction = {}
        )
    }
}