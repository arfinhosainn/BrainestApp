package com.scelio.brainest.presentation.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import brainest.feature.auth.presentation.generated.resources.Res
import brainest.feature.auth.presentation.generated.resources.error_email_not_verified
import brainest.feature.auth.presentation.generated.resources.error_invalid_credentials
import com.scelio.brainest.domain.auth.AuthService
import com.scelio.brainest.domain.onboarding.OnboardingSyncer
import com.scelio.brainest.domain.util.DataError
import com.scelio.brainest.domain.util.onFailure
import com.scelio.brainest.domain.util.onSuccess
import com.scelio.brainest.domain.validation.EmailValidator
import com.scelio.brainest.presentation.util.UiText
import com.scelio.brainest.presentation.util.toUiText
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch


class LoginViewModel(
    private val authService: AuthService,
    private val onboardingSyncer: OnboardingSyncer
) : ViewModel() {

    private var hasLoadedInitialData = false

    private val eventChannel = Channel<LoginEvent>()
    val events = eventChannel.receiveAsFlow()

    private val _state = MutableStateFlow(LoginState())
    
    private val isEmailValidFlow = _state
        .map { EmailValidator.validate(it.email) }
        .distinctUntilChanged()

    private val isPasswordNotBlankFlow =
        _state
            .map { it.password.isNotBlank() }
            .distinctUntilChanged()

    val state = _state
        .onStart {
            if (!hasLoadedInitialData) {
                observeTextStates()
                hasLoadedInitialData = true
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000L),
            initialValue = LoginState()
        )


    private val isRegisteringFlow = state
        .map { it.isLoggingIn }
        .distinctUntilChanged()


    fun onAction(action: LoginAction) {
        when (action) {
            is LoginAction.OnEmailChanged -> {
                _state.update { it.copy(email = action.email) }
            }
            is LoginAction.OnPasswordChanged -> {
                _state.update { it.copy(password = action.password) }
            }
            LoginAction.OnLoginClick -> login()
            LoginAction.OnTogglePasswordVisibility -> {
                _state.update {
                    it.copy(
                        isPasswordVisible = !it.isPasswordVisible
                    )
                }

            }

            else -> Unit
        }
    }

    private fun observeTextStates() {
        combine(
            isEmailValidFlow,
            isPasswordNotBlankFlow,
            isRegisteringFlow
        ) { isEmailValid, isPasswordNotBlank, isRegistering ->
            _state.update {
                it.copy(
                    canLogin = !isRegistering && isEmailValid && isPasswordNotBlank
                )
            }
        }.launchIn(viewModelScope)

    }

    private fun login() {
        if (!state.value.canLogin) {
            return
        }

        viewModelScope.launch {
            _state.update {
                it.copy(
                    isLoggingIn = true
                )
            }

            val email = state.value.email
            val password = state.value.password

            authService
                .login(
                    email = email,
                    password = password
                )
                .onSuccess { authInfo ->
                    _state.update {
                        it.copy(
                            isLoggingIn = false
                        )
                    }
                    onboardingSyncer.sync(authInfo.user.id)
                    eventChannel.send(LoginEvent.Success)
                }
                .onFailure { error ->
                    val errorMessage = when (error) {
                        DataError.Remote.UNAUTHORIZED -> UiText.Resource(Res.string.error_invalid_credentials)
                        DataError.Remote.FORBIDDEN -> UiText.Resource(Res.string.error_email_not_verified)
                        else -> error.toUiText()
                    }

                    _state.update {
                        it.copy(
                            error = errorMessage,
                            isLoggingIn = false
                        )
                    }
                }
        }
    }

}
