package com.scellio.brainest

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.scelio.brainest.domain.auth.SessionManager
import com.scelio.brainest.domain.auth.SessionStatus
import com.scelio.brainest.domain.logging.BrainestLogger
import com.scelio.brainest.domain.onboarding.OnboardingSyncer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class MainViewModel(
    private val sessionManager: SessionManager,
    private val onboardingSyncer: OnboardingSyncer,
    private val logger: BrainestLogger
) : ViewModel() {

    private val eventChannel = Channel<MainEvent>()
    val events = eventChannel.receiveAsFlow()

    private var hasLoadedInitialData = false
    private var hasResolvedStartup = false

    private val _state = MutableStateFlow(MainState())
    val state = _state
        .onStart {
            if (!hasLoadedInitialData) {
                checkInitialAuthState()
                observeSessionStatus()
                hasLoadedInitialData = true
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000L),
            initialValue = MainState()
        )

    private fun checkInitialAuthState() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val isLoggedIn = sessionManager.isAuthenticated()
                hasResolvedStartup = true
                _state.update {
                    it.copy(
                        isCheckingAuth = false,
                        isLoggedIn = isLoggedIn
                    )
                }
            } catch (e: Exception) {
                logger.error("Error checking auth state", e)
                hasResolvedStartup = true
                _state.update {
                    it.copy(
                        isCheckingAuth = false,
                        isLoggedIn = false
                    )
                }
            }
        }
    }

    private fun observeSessionStatus() {
        sessionManager.sessionStatusFlow()
            .onEach { status ->
                when (status) {
                    is SessionStatus.Authenticated -> {
                        hasResolvedStartup = true
                        _state.update {
                            it.copy(
                                isCheckingAuth = false,
                                isLoggedIn = true
                            )
                        }
                        viewModelScope.launch(Dispatchers.IO) {
                            onboardingSyncer.sync(status.userId)
                        }
                    }

                    is SessionStatus.NotAuthenticated -> {
                        val wasLoggedIn = _state.value.isLoggedIn
                        hasResolvedStartup = true
                        _state.update {
                            it.copy(
                                isCheckingAuth = false,
                                isLoggedIn = false
                            )
                        }

                        if (wasLoggedIn && !status.isSignOut) {
                            eventChannel.send(MainEvent.OnSessionExpired)
                        }
                    }

                    is SessionStatus.Initializing -> {
                        if (!hasResolvedStartup) {
                            _state.update {
                                it.copy(isCheckingAuth = true)
                            }
                        }
                    }

                    is SessionStatus.RefreshFailure -> {
                        val wasLoggedIn = _state.value.isLoggedIn
                        hasResolvedStartup = true
                        _state.update {
                            it.copy(
                                isCheckingAuth = false,
                                isLoggedIn = false
                            )
                        }

                        if (wasLoggedIn) {
                            eventChannel.send(MainEvent.OnSessionExpired)
                        }
                    }
                }
            }
            .launchIn(viewModelScope)
    }
}
