package com.scelio.brainest.domain.auth

import kotlinx.coroutines.flow.Flow

/**
 * Manages user session state and provides reactive session status updates.
 * 
 * This interface abstracts the underlying auth provider (e.g., Supabase) 
 * from the presentation layer, following Clean Architecture principles.
 */
interface SessionManager {
    
    /**
     * Returns the current user ID if authenticated, null otherwise.
     */
    suspend fun getCurrentUserId(): String?
    
    /**
     * Returns whether the user is currently authenticated.
     */
    suspend fun isAuthenticated(): Boolean
    
    /**
     * Reactive flow of session status changes.
     * Emits [SessionStatus] values when authentication state changes.
     */
    fun sessionStatusFlow(): Flow<SessionStatus>
}

/**
 * Represents the various states of a user session.
 */
sealed interface SessionStatus {
    /**
     * Session is being initialized or restored from storage.
     */
    data object Initializing : SessionStatus
    
    /**
     * User is authenticated with a valid session.
     */
    data class Authenticated(val userId: String) : SessionStatus
    
    /**
     * User is not authenticated.
     * @param isSignOut Whether this was an intentional sign-out.
     */
    data class NotAuthenticated(val isSignOut: Boolean = false) : SessionStatus
    
    /**
     * Session refresh failed and session is no longer valid.
     */
    data object RefreshFailure : SessionStatus
}
