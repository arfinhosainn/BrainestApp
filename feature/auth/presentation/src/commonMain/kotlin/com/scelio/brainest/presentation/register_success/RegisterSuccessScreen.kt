package com.scelio.brainest.presentation.register_success

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import brainest.feature.auth.presentation.generated.resources.Res
import brainest.feature.auth.presentation.generated.resources.account_successfully_created
import brainest.feature.auth.presentation.generated.resources.login
import brainest.feature.auth.presentation.generated.resources.resend_verification_email
import brainest.feature.auth.presentation.generated.resources.verification_email_sent_to_x
import com.scelio.brainest.designsystem.BrainestTheme
import com.scelio.brainest.designsystem.components.brand.BrainestSuccessIcon
import com.scelio.brainest.designsystem.components.buttons.BrainestButton
import com.scelio.brainest.designsystem.components.buttons.BrainestButtonStyle
import com.scelio.brainest.designsystem.components.layouts.BrainestAdaptiveResultLayout
import com.scelio.brainest.designsystem.components.layouts.BrainestSimpleSuccessLayout
import com.scelio.brainest.designsystem.components.layouts.BrainestSnackbarScaffold
import com.scelio.brainest.presentation.util.ObserveAsEvents
import org.jetbrains.compose.resources.getString
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun RegisterSuccessRoot(
    viewModel: RegisterSuccessViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    val snackbarHostState = remember { SnackbarHostState() }

    ObserveAsEvents(viewModel.events) { event ->
        when (event) {
            is RegisterSuccessEvent.ResendVerificationEmailSuccess -> {
                snackbarHostState.showSnackbar(
                    message = getString(
                        resource = Res.string.resent_verification_email
                    )
                )
            }
        }
    }

    RegisterSuccessScreen(
        state = state,
        onAction = viewModel::onAction,
        snackbarHostState = snackbarHostState
    )
}

@Composable
fun RegisterSuccessScreen(
    state: RegisterSuccessState,
    onAction: (RegisterSuccessAction) -> Unit,
    snackbarHostState: SnackbarHostState
) {

    BrainestSnackbarScaffold(
        snackbarHostState = snackbarHostState
    ) {
        BrainestAdaptiveResultLayout {
            BrainestSimpleSuccessLayout(
                title = stringResource(Res.string.account_successfully_created),
                description = stringResource(
                    Res.string.verification_email_sent_to_x,
                    state.registeredEmail
                ),
                icon = {
                    BrainestSuccessIcon()
                },
                primaryButton = {
                    BrainestButton(
                        text = stringResource(Res.string.login),
                        onClick = {
                            onAction(RegisterSuccessAction.OnLoginClick)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                    )
                },
                secondaryButton = {
                    BrainestButton(
                        text = stringResource(Res.string.resend_verification_email),
                        onClick = {
                            onAction(RegisterSuccessAction.OnResendVerificationEmailClick)
                        },
                        modifier = Modifier
                            .fillMaxWidth(),
                        enabled = !state.isResendingVerificationEmail,
                        isLoading = state.isResendingVerificationEmail,
                        style = BrainestButtonStyle.SECONDARY
                    )
                },
                secondaryError = state.resendVerificationError?.asString()
            )
        }
    }
}

@Preview
@Composable
private fun Preview() {
    BrainestTheme {
        RegisterSuccessScreen(
            state = RegisterSuccessState(
                registeredEmail = "test@preview.com"
            ),
            onAction = {},
            snackbarHostState = remember { SnackbarHostState() }
        )
    }
}