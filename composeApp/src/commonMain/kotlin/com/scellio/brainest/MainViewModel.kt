package com.scellio.brainest

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.scelio.brainest.domain.onboarding.OnboardingSyncer
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.status.SessionStatus
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
    private val supabaseClient: SupabaseClient,
    private val onboardingSyncer: OnboardingSyncer
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

    // ✅ Move to IO dispatcher
    private fun checkInitialAuthState() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val session = supabaseClient.auth.currentSessionOrNull()
                _state.update {
                    it.copy(
                        isCheckingAuth = false,
                        isLoggedIn = session != null
                    )
                }
            } catch (e: Exception) {
                println("Error checking auth state: ${e.message}")
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
                        viewModelScope.launch(Dispatchers.IO) {
                            val userId = supabaseClient.auth.currentSessionOrNull()?.user?.id
                            if (userId != null) {
                                onboardingSyncer.sync(userId)
                            }
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
