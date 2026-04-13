package com.scelio.brainest.presentation.register


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import brainest.feature.auth.presentation.generated.resources.Res
import brainest.feature.auth.presentation.generated.resources.error_account_exists
import brainest.feature.auth.presentation.generated.resources.error_invalid_email
import brainest.feature.auth.presentation.generated.resources.error_invalid_password
import brainest.feature.auth.presentation.generated.resources.error_invalid_username
import com.scelio.brainest.domain.auth.AuthService
import kotlinx.coroutines.flow.distinctUntilChanged
import com.scelio.brainest.domain.util.DataError
import com.scelio.brainest.domain.util.onFailure
import com.scelio.brainest.domain.util.onSuccess
import com.scelio.brainest.domain.validation.EmailValidator
import com.scelio.brainest.domain.validation.PasswordValidator
import com.scelio.brainest.presentation.util.UiText
import com.scelio.brainest.presentation.util.toUiText
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class RegisterViewModel(
    private val authService: AuthService
) : ViewModel() {


    private val eventChannel = Channel<RegisterEvent>()
    val events = eventChannel.receiveAsFlow()
    private var hasLoadedInitialData = false

    private val _state = MutableStateFlow(RegisterState())
    val state = _state
        .onStart {
            if (!hasLoadedInitialData) {
                observeValidationStates()
                hasLoadedInitialData = true
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000L),
            initialValue = RegisterState()
        )


    private val isEmailValidFlow = _state
        .map { EmailValidator.validate(it.email) }
        .distinctUntilChanged()

    private val isUsernameValidFlow = _state
        .map { it.username.length in 3..20 }
        .distinctUntilChanged()

    private val isPasswordValidFlow = _state
        .map { PasswordValidator.validate(it.password).isValidPassword }
        .distinctUntilChanged()

    private val isRegisteringFlow = state
        .map { it.isRegistering }
        .distinctUntilChanged()

    private fun observeValidationStates() {
        combine(
            isEmailValidFlow,
            isUsernameValidFlow,
            isPasswordValidFlow,
            isRegisteringFlow
        ) { isEmailValid, isUsernameValid, isPasswordValid, isRegistering ->
            val allValid = isEmailValid && isUsernameValid && isPasswordValid
            _state.update {
                it.copy(
                    canRegister = !isRegistering && allValid
                )
            }
        }.launchIn(viewModelScope)
    }


    fun onAction(action: RegisterAction) {
        when (action) {
            is RegisterAction.OnEmailChanged -> _state.update { it.copy(email = action.email, emailError = null) }
            is RegisterAction.OnPasswordChanged -> _state.update { it.copy(password = action.password, passwordError = null) }
            is RegisterAction.OnUsernameChanged -> _state.update { it.copy(username = action.username, usernameError = null) }
            RegisterAction.OnLoginClick -> Unit
            RegisterAction.OnRegisterClick -> register()
            RegisterAction.OnTogglePasswordVisibilityClick -> {
                _state.update {
                    it.copy(
                        isPasswordVisible = !it.isPasswordVisible
                    )
                }
            }

            else -> Unit
        }
    }


    private fun register() {
        clearAllTextFieldErrors()

        if (!validateFormInputs()) {
            return
        }

        viewModelScope.launch {
            _state.update { it.copy(isRegistering = true) }


            val email = state.value.email
            val username = state.value.username
            val password = state.value.password

            authService
                .register(
                    email = email,
                    username = username,
                    password = password
                )
                .onSuccess {
                    _state.update { it.copy(isRegistering = false) }
                    eventChannel.send(RegisterEvent.Success(email))
                }
                .onFailure { error ->
                    val registrationError = when (error) {
                        DataError.Remote.CONFLICT -> UiText.Resource(Res.string.error_account_exists)
                        else -> error.toUiText()
                    }
                    _state.update {
                        it.copy(
                            isRegistering = false,
                            registrationError = registrationError
                        )
                    }
                }
        }
    }


    private fun clearAllTextFieldErrors() {
        _state.update {
            it.copy(
                emailError = null,
                usernameError = null,
                passwordError = null,
                registrationError = null
            )
        }
    }

    private fun validateFormInputs(): Boolean {
        clearAllTextFieldErrors()

        val currentState = state.value
        val email = currentState.email
        val username = currentState.username
        val password = currentState.password

        val isEmailValid = EmailValidator.validate(email)
        val passwordValidationState = PasswordValidator.validate(password)
        val isUsernameValid = username.length in 3..20

        val emailError = if (!isEmailValid) {
            UiText.Resource(Res.string.error_invalid_email)
        } else null
        val usernameError = if (!isUsernameValid) {
            UiText.Resource(Res.string.error_invalid_username)
        } else null
        val passwordError = if (!passwordValidationState.isValidPassword) {
            UiText.Resource(Res.string.error_invalid_password)
        } else null

        _state.update {
            it.copy(
                emailError = emailError,
                usernameError = usernameError,
                passwordError = passwordError
            )
        }

        return isUsernameValid && isEmailValid && passwordValidationState.isValidPassword
    }


}