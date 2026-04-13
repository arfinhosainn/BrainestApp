package com.scelio.brainest.data.auth

import com.scelio.brainest.domain.auth.SessionManager
import com.scelio.brainest.domain.auth.SessionStatus
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.status.SessionStatus as SupabaseSessionStatus
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * Supabase-backed implementation of [SessionManager].
 * 
 * Maps Supabase session states to domain [SessionStatus] values.
 */
class SupabaseSessionManager(
    private val supabaseClient: SupabaseClient
) : SessionManager {
    
    override suspend fun getCurrentUserId(): String? {
        return try {
            val session = supabaseClient.auth.currentSessionOrNull()
            session?.user?.id
        } catch (e: Exception) {
            null
        }
    }
    
    override suspend fun isAuthenticated(): Boolean {
        return try {
            val session = supabaseClient.auth.currentSessionOrNull()
            session != null
        } catch (e: Exception) {
            false
        }
    }
    
    override fun sessionStatusFlow(): Flow<SessionStatus> {
        return supabaseClient.auth.sessionStatus.map { supabaseStatus ->
            when (supabaseStatus) {
                is SupabaseSessionStatus.Initializing -> SessionStatus.Initializing
                
                is SupabaseSessionStatus.Authenticated -> {
                    val userId = supabaseClient.auth.currentSessionOrNull()?.user?.id ?: ""
                    SessionStatus.Authenticated(userId)
                }
                
                is SupabaseSessionStatus.NotAuthenticated -> {
                    SessionStatus.NotAuthenticated(isSignOut = supabaseStatus.isSignOut)
                }
                
                is SupabaseSessionStatus.RefreshFailure -> SessionStatus.RefreshFailure
            }
        }
    }
}
