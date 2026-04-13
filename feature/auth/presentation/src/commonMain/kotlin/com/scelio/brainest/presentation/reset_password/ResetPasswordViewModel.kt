package com.scelio.brainest.presentation.reset_password

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import brainest.feature.auth.presentation.generated.resources.Res
import brainest.feature.auth.presentation.generated.resources.error_reset_password_token_invalid
import brainest.feature.auth.presentation.generated.resources.error_same_password
import com.scelio.brainest.domain.auth.AuthService
import com.scelio.brainest.domain.logging.BrainestLogger
import com.scelio.brainest.domain.util.DataError
import com.scelio.brainest.domain.util.onFailure
import com.scelio.brainest.domain.util.onSuccess
import com.scelio.brainest.domain.validation.PasswordValidator
import com.scelio.brainest.presentation.util.UiText
import com.scelio.brainest.presentation.util.toUiText
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ResetPasswordViewModel(
    private val authService: AuthService,
    private val logger: BrainestLogger,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private var hasLoadedInitialData = false

    private val deepLinkUrl = savedStateHandle.get<String>("deepLinkUrl")
        ?: ""

    private val _state = MutableStateFlow(ResetPasswordState())
    
    private val isPasswordValidFlow = _state
        .map { PasswordValidator.validate(it.password).isValidPassword }
        .distinctUntilChanged()

    val state = _state
        .onStart {
            if (!hasLoadedInitialData) {
                observeValidationState()
                hasLoadedInitialData = true
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000L),
            initialValue = ResetPasswordState()
        )

    private fun observeValidationState() {
        isPasswordValidFlow.onEach { isPasswordValid ->
            _state.update {
                it.copy(
                    canSubmit = isPasswordValid
                )
            }
        }.launchIn(viewModelScope)
    }


    fun onAction(action: ResetPasswordAction) {
        when (action) {
            is ResetPasswordAction.OnPasswordChanged -> {
                _state.update { it.copy(password = action.password) }
            }
            ResetPasswordAction.OnSubmitClick -> resetPassword()
            ResetPasswordAction.OnTogglePasswordVisibilityClick -> {
                _state.update {
                    it.copy(
                        isPasswordVisible = !it.isPasswordVisible
                    )
                }
            }
        }
    }

    private fun resetPassword() {
        if (state.value.isLoading || !state.value.canSubmit) {
            return
        }

        viewModelScope.launch {
            _state.update {
                it.copy(
                    isLoading = true,
                    isResetSuccessful = false
                )
            }

            val newPassword = state.value.password
            authService
                .resetPassword(
                    newPassword = newPassword,
                    deepLinkUrl = deepLinkUrl
                )
                .onSuccess {
                    _state.update {
                        it.copy(
                            isLoading = false,
                            isResetSuccessful = true,
                            errorText = null
                        )
                    }
                }
                .onFailure { error ->
                    logger.error("Reset password failed: $error")
                    val errorText = when (error) {
                        DataError.Remote.UNAUTHORIZED -> UiText.Resource(Res.string.error_reset_password_token_invalid)
                        DataError.Remote.CONFLICT -> UiText.Resource(Res.string.error_same_password)
                        else -> error.toUiText()
                    }
                    _state.update {
                        it.copy(
                            errorText = errorText,
                            isLoading = false,
                        )
                    }
                }


        }
    }
}