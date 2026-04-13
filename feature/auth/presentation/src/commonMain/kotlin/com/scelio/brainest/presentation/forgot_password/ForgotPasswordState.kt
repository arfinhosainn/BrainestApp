package com.scelio.brainest.presentation.forgot_password

import com.scelio.brainest.presentation.util.UiText

data class ForgotPasswordState(
    val email: String = "",
    val canSubmit: Boolean = false,
    val isLoading: Boolean = false,
    val errorText: UiText? = null,
    val isEmailSentSuccessfully: Boolean = false
)