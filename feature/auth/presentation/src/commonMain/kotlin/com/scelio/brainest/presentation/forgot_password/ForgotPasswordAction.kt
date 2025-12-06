package com.scelio.brainest.presentation.forgot_password

sealed interface ForgotPasswordAction {
    data object OnSubmitClick: ForgotPasswordAction
}