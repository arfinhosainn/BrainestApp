package com.scelio.brainest.presentation.register


sealed interface RegisterEvent {
    data class Success(val email: String): RegisterEvent
}