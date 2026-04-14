package com.scelio.brainest.presentation.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.scelio.brainest.domain.auth.AuthService
import com.scelio.brainest.domain.logging.BrainestLogger
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlinx.datetime.format

data class SettingsState(
    val username: String = "",
    val joinedText: String = "",
    val isLoading: Boolean = true,
    val error: String? = null
)

sealed class SettingsEvent {
    data object NavigateToLogin : SettingsEvent()
    data class ShowError(val message: String) : SettingsEvent()
}

class SettingsViewModel(
    private val authService: AuthService,
    private val logger: BrainestLogger
) : ViewModel() {

    private val _state = MutableStateFlow(SettingsState())
    val state = _state.asStateFlow()

    init {
        loadUserProfile()
    }

    private fun loadUserProfile() {
        viewModelScope.launch {
            try {
                _state.update { it.copy(isLoading = true, error = null) }
                
                val user = authService.getCurrentUser()
                logger.debug("[SettingsViewModel] loadUserProfile - user: ${user != null}, username: ${user?.username}, createdAt: ${user?.createdAt}")
                
                if (user != null) {
                    val joinedText = user.createdAt?.let { dateStr ->
                        try {
                            // Parse ISO date string from Supabase (e.g., "2024-01-15T10:30:00Z")
                            val instant = Instant.parse(dateStr)
                            val localDate = instant.toLocalDateTime(TimeZone.currentSystemDefault())
                            val monthNames = listOf(
                                "Jan", "Feb", "Mar", "Apr", "May", "Jun",
                                "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"
                            )
                            val monthName = monthNames[localDate.monthNumber - 1]
                            "$monthName ${localDate.year}"
                        } catch (e: Exception) {
                            logger.error("[SettingsViewModel] Failed to parse date: $dateStr")
                            "Unknown"
                        }
                    } ?: "Unknown"
                    
                    _state.update { 
                        it.copy(
                            username = user.username,
                            joinedText = "Joined $joinedText",
                            isLoading = false
                        ) 
                    }
                } else {
                    _state.update { 
                        it.copy(
                            isLoading = false,
                            error = "Failed to load user profile"
                        ) 
                    }
                }
            } catch (e: Exception) {
                _state.update { 
                    it.copy(
                        isLoading = false,
                        error = e.message ?: "Unknown error"
                    ) 
                }
            }
        }
    }

    fun onNameChange(newName: String) {
        viewModelScope.launch {
            _state.update { it.copy(username = newName) }
            
            // Update username in Supabase
            val result = authService.updateUsername(newName)
            when (result) {
                is com.scelio.brainest.domain.util.Result.Success -> {
                    logger.debug("[SettingsViewModel] Username updated successfully")
                }
                is com.scelio.brainest.domain.util.Result.Failure -> {
                    logger.error("[SettingsViewModel] Failed to update username: ${result.error}")
                    // Optionally show error to user
                }
            }
        }
    }

    fun onLogoutClick() {
        viewModelScope.launch {
            try {
                authService.signOut()
            } catch (e: Exception) {
                // Handle error if needed
            }
        }
    }
}
