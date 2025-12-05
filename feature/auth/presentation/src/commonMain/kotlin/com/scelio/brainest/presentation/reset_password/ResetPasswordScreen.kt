package com.scelio.brainest.presentation.reset_password

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import brainest.feature.auth.presentation.generated.resources.Res
import brainest.feature.auth.presentation.generated.resources.password
import brainest.feature.auth.presentation.generated.resources.password_hint
import brainest.feature.auth.presentation.generated.resources.reset_password_successfully
import brainest.feature.auth.presentation.generated.resources.set_new_password
import brainest.feature.auth.presentation.generated.resources.submit
import com.scelio.brainest.designsystem.BrainestTheme
import com.scelio.brainest.designsystem.components.brand.BrainestBrandLogo
import com.scelio.brainest.designsystem.components.buttons.BrainestButton
import com.scelio.brainest.designsystem.components.layouts.BrainestAdaptiveFormLayout
import com.scelio.brainest.designsystem.components.textfields.BrainestPasswordTextField
import com.scelio.brainest.designsystem.extended
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun ResetPasswordRoot(
    viewModel: ResetPasswordViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    ResetPasswordScreen(
        state = state,
        onAction = viewModel::onAction
    )
}

@Composable
fun ResetPasswordScreen(
    state: ResetPasswordState,
    onAction: (ResetPasswordAction) -> Unit,
) {
    BrainestAdaptiveFormLayout(
        headerText = stringResource(Res.string.set_new_password),
        errorText = state.errorText?.asString(),
        logo = {
            BrainestBrandLogo()
        }
    ) {
        BrainestPasswordTextField(
            state = state.passwordTextState,
            modifier = Modifier
                .fillMaxWidth(),
            placeholder = stringResource(Res.string.password),
            title = stringResource(Res.string.password),
            supportingText = stringResource(Res.string.password_hint),
            isPasswordVisible = state.isPasswordVisible,
            onToggleVisibilityClick = {
                onAction(ResetPasswordAction.OnTogglePasswordVisibilityClick)
            }
        )
        Spacer(modifier = Modifier.height(16.dp))
        BrainestButton(
            text = stringResource(Res.string.submit),
            onClick = {
                onAction(ResetPasswordAction.OnSubmitClick)
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = !state.isLoading && state.canSubmit,
            isLoading = state.isLoading
        )
        if(state.isResetSuccessful) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = stringResource(Res.string.reset_password_successfully),
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
        ResetPasswordScreen(
            state = ResetPasswordState(),
            onAction = {}
        )
    }
}