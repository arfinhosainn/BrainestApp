package com.scelio.brainest.presentation.reset_password

sealed interface ResetPasswordAction {
    data class OnPasswordChanged(val password: String) : ResetPasswordAction
    data object OnSubmitClick: ResetPasswordAction
    data object OnTogglePasswordVisibilityClick: ResetPasswordAction
}