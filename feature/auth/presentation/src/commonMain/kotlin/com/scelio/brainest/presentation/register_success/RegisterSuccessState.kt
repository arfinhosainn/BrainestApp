package com.scelio.brainest.presentation.register_success

import com.scelio.brainest.presentation.util.UiText

data class RegisterSuccessState(
    val registeredEmail: String = "",
    val isResendingVerificationEmail: Boolean = false,
    val resendVerificationError: UiText? = null
)