package com.scelio.brainest.presentation.register

import com.scelio.brainest.presentation.util.UiText

data class RegisterState(
    val email: String = "",
    val isEmailValid: Boolean = false,
    val emailError: UiText? = null,
    val password: String = "",
    val isPasswordValid: Boolean = false,
    val passwordError: UiText? = null,
    val username: String = "",
    val isUsernameValid: Boolean = false,
    val usernameError: UiText? = null,
    val registrationError: UiText? = null,
    val isRegistering: Boolean = false,
    val canRegister: Boolean = false,
    val isPasswordVisible: Boolean = false
)