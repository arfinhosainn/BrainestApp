package com.scelio.brainest.presentation.email_verification

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.scelio.brainest.domain.auth.AuthService
import com.scelio.brainest.domain.onboarding.OnboardingSyncer
import com.scelio.brainest.domain.util.onFailure
import com.scelio.brainest.domain.util.onSuccess
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class EmailVerificationViewModel(
    private val authService: AuthService,
    private val onboardingSyncer: OnboardingSyncer,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private var hasLoadedInitialData = false
    private val deepLinkUrl = savedStateHandle.get<String>("deepLinkUrl") ?: ""

    private val _state = MutableStateFlow(EmailVerificationState())
    val state = _state
        .onStart {
            if (!hasLoadedInitialData) {
                verifyEmail()
                hasLoadedInitialData = true
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000L),
            initialValue = EmailVerificationState()
        )

    fun onAction(action: EmailVerificationAction) = Unit

    private fun verifyEmail() {
        viewModelScope.launch {
            _state.update { it.copy(isVerifying = true) }

            if (deepLinkUrl.isEmpty()) {
                _state.update { it.copy(isVerifying = false, isVerified = false) }
                return@launch
            }

            authService
                .verifyEmail(deepLinkUrl)
                .onSuccess {
                    _state.update { it.copy(isVerifying = false, isVerified = true) }
                    val userId = authService.currentUserId()
                    if (userId != null) {
                        onboardingSyncer.sync(userId)
                    }
                }
                .onFailure { _ ->
                    _state.update { it.copy(isVerifying = false, isVerified = false) }
                }
        }
    }
}
