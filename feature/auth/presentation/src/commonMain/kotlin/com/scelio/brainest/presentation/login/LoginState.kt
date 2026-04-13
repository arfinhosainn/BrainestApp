package com.scelio.brainest.presentation.login

import com.scelio.brainest.presentation.util.UiText

data class LoginState(
    val email: String = "",
    val password: String = "",
    val isPasswordVisible: Boolean = false,
    val canLogin: Boolean = false,
    val isLoggingIn: Boolean = false,
    val error: UiText? = null
)