package com.scelio.brainest.presentation.login


import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import brainest.feature.auth.presentation.generated.resources.Res
import brainest.feature.auth.presentation.generated.resources.create_account
import brainest.feature.auth.presentation.generated.resources.email
import brainest.feature.auth.presentation.generated.resources.email_placeholder
import brainest.feature.auth.presentation.generated.resources.forgot_password
import brainest.feature.auth.presentation.generated.resources.login
import brainest.feature.auth.presentation.generated.resources.password
import brainest.feature.auth.presentation.generated.resources.welcome_back
import com.scelio.brainest.designsystem.BrainestTheme
import com.scelio.brainest.designsystem.components.brand.BrainestBrandLogo
import com.scelio.brainest.designsystem.components.buttons.BrainestButton
import com.scelio.brainest.designsystem.components.layouts.BrainestAdaptiveFormLayout
import com.scelio.brainest.designsystem.components.layouts.BrainestSnackbarScaffold
import com.scelio.brainest.designsystem.components.textfields.BrainestPasswordTextField
import com.scelio.brainest.designsystem.components.textfields.BrainestTextField
import com.scelio.brainest.designsystem.extended
import com.scelio.brainest.presentation.util.ObserveAsEvents
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun LoginRoot(
    viewModel: LoginViewModel = koinViewModel(),
    onLoginSuccess: () -> Unit,
    onForgotPasswordClick: () -> Unit,
    onCreateAccountClick: () -> Unit
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    ObserveAsEvents(viewModel.events) { event ->
        when(event) {
            LoginEvent.Success -> onLoginSuccess()
        }
    }

    LoginScreen(
        state = state,
        onAction = { action ->
            when(action) {
                LoginAction.OnForgotPasswordClick -> onForgotPasswordClick()
                LoginAction.OnSignUpClick -> onCreateAccountClick()
                else -> Unit
            }
            viewModel.onAction(action)
        }
    )
}

@Composable
fun LoginScreen(
    state: LoginState,
    onAction: (LoginAction) -> Unit,
) {
    BrainestSnackbarScaffold {
        BrainestAdaptiveFormLayout(
            headerText = stringResource(Res.string.welcome_back),
            errorText = state.error?.asString(),
            logo = {
                BrainestBrandLogo()
            },
            modifier = Modifier
                .fillMaxSize()
        ) {
            BrainestTextField(
                state = state.emailTextFieldState,
                placeholder = stringResource(Res.string.email_placeholder),
                keyboardType = KeyboardType.Email,
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth(),
                title = stringResource(Res.string.email)
            )
            Spacer(modifier = Modifier.height(16.dp))
            BrainestPasswordTextField(
                state = state.passwordTextFieldState,
                placeholder = stringResource(Res.string.password),
                isPasswordVisible = state.isPasswordVisible,
                onToggleVisibilityClick = {
                    onAction(LoginAction.OnTogglePasswordVisibility)
                },
                title = stringResource(Res.string.password),
                modifier = Modifier
                    .fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = stringResource(Res.string.forgot_password),
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.tertiary,
                modifier = Modifier
                    .align(Alignment.End)
                    .clickable {
                        onAction(LoginAction.OnForgotPasswordClick)
                    }
            )
            Spacer(modifier = Modifier.height(24.dp))

            BrainestButton(
                text = stringResource(Res.string.login),
                onClick = {
                    onAction(LoginAction.OnLoginClick)
                },
                enabled = state.canLogin,
                isLoading = state.isLoggingIn,
                modifier = Modifier
                    .fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            BrainestButton(
                text = stringResource(Res.string.create_account),
                onClick = {
                    onAction(LoginAction.OnSignUpClick)
                },
                backgroundColor = Color.Transparent,
                contentColor = MaterialTheme.colorScheme.extended.textSecondary,
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.extended.disabledOutline),
                modifier = Modifier
                    .fillMaxWidth()
            )
        }
    }
}

@Preview
@Composable
private fun LightThemePreview() {
    BrainestTheme {
        LoginScreen(
            state = LoginState(),
            onAction = {}
        )
    }
}

@Preview
@Composable
private fun DarkThemePreview() {
    BrainestTheme(darkTheme = true) {
        LoginScreen(
            state = LoginState(),
            onAction = {}
        )
    }
}
