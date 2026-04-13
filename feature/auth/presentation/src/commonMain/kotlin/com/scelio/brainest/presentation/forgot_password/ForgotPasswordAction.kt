package com.scelio.brainest.presentation.forgot_password

sealed interface ForgotPasswordAction {
    data class OnEmailChanged(val email: String) : ForgotPasswordAction
    data object OnSubmitClick: ForgotPasswordAction
}