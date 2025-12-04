package com.scelio.brainest.presentation.login

sealed interface LoginEvent {
    data object Success: LoginEvent
}