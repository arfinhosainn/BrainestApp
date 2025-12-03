package com.scelio.brainest.presentation.register_success

data class RegisterSuccessState(
    val registeredEmail: String = "",
    val isResendingVerificationEmail: Boolean = false
)