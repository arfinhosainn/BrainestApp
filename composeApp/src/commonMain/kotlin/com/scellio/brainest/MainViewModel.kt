package com.scellio.brainest

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.status.SessionStatus
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update

class MainViewModel(
    private val supabaseClient: SupabaseClient
) : ViewModel() {

    private val eventChannel = Channel<MainEvent>()
    val events = eventChannel.receiveAsFlow()

    private var hasLoadedInitialData = false

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
        val session = supabaseClient.auth.currentSessionOrNull()
        _state.update {
            it.copy(
                isCheckingAuth = false,
                isLoggedIn = session != null
            )
        }
    }

    private fun observeSessionStatus() {
        supabaseClient.auth.sessionStatus
            .onEach { status ->
                when (status) {
                    is SessionStatus.Authenticated -> {
                        _state.update {
                            it.copy(
                                isCheckingAuth = false,
                                isLoggedIn = true
                            )
                        }
                    }

                    is SessionStatus.NotAuthenticated -> {
                        val wasLoggedIn = _state.value.isLoggedIn
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
                        _state.update {
                            it.copy(isCheckingAuth = true)
                        }
                    }

                    is SessionStatus.RefreshFailure -> {
                        val wasLoggedIn = _state.value.isLoggedIn
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