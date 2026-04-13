package com.scelio.brainest.presentation.reset_password

import com.scelio.brainest.presentation.util.UiText

data class ResetPasswordState(
    val password: String = "",
    val isLoading: Boolean = false,
    val errorText: UiText? = null,
    val isPasswordVisible: Boolean = false,
    val canSubmit: Boolean = false,
    val isResetSuccessful: Boolean = false
)