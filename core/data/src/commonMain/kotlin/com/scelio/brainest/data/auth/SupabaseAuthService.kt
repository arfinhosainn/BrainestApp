package com.scelio.brainest.data.auth

import com.scelio.brainest.data.util.toDataError
import com.scelio.brainest.domain.auth.AuthInfo
import com.scelio.brainest.domain.auth.AuthService
import com.scelio.brainest.domain.auth.User
import com.scelio.brainest.domain.logging.BrainestLogger
import com.scelio.brainest.domain.util.DataError
import com.scelio.brainest.domain.util.EmptyResult
import com.scelio.brainest.domain.util.Result
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.OtpType
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.parseSessionFromUrl
import io.github.jan.supabase.auth.providers.builtin.Email
import io.github.jan.supabase.auth.user.UserSession
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put

class SupabaseAuthService(
    private val supabaseClient: SupabaseClient,
    private val logger: BrainestLogger
) : AuthService {

    override suspend fun register(
        email: String,
        password: String,
        username: String
    ): EmptyResult<DataError.Remote> {
        return try {
            supabaseClient.auth.signUpWith(
                Email,
                redirectUrl = "brainest://brainest.app/auth/verify"
            ) {
                this.email = email
                this.password = password
                data = buildJsonObject {
                    put("username", username)
                }
            }
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Failure(e.toDataError())
        }
    }

    override suspend fun resendVerificationEmail(
        email: String
    ): EmptyResult<DataError.Remote> {
        return try {
            supabaseClient.auth.resendEmail(
                type = OtpType.Email.SIGNUP,
                email = email
            )
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Failure(e.toDataError())
        }
    }

    override suspend fun verifyEmail(deepLinkUrl: String): EmptyResult<DataError.Remote> {
        return try {
            val userSession = supabaseClient.auth.parseSessionFromUrl(deepLinkUrl)
            supabaseClient.auth.importSession(
                userSession
            )
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Failure(e.toDataError())
        }
    }


    override suspend fun login(
        email: String,
        password: String
    ): Result<AuthInfo, DataError.Remote> {
        return try {
            supabaseClient.auth.signInWith(Email) {
                this.email = email
                this.password = password
            }

            val session = supabaseClient.auth.currentSessionOrNull()
                ?: return Result.Failure(DataError.Remote.UNAUTHORIZED)

            val user = session.user
                ?: return Result.Failure(DataError.Remote.UNKNOWN)

            val username = user.userMetadata?.get("username") as? String
                ?: return Result.Failure(DataError.Remote.UNKNOWN)

            val authInfo = AuthInfo(
                accessToken = session.accessToken,
                refreshToken = session.refreshToken,
                user = User(
                    id = user.id,
                    email = user.email ?: email,
                    username = username,
                    hasVerifiedEmail = user.emailConfirmedAt != null,
                    profilePictureUrl = user.userMetadata?.get("avatar_url") as? String
                )
            )

            Result.Success(authInfo)
        } catch (e: Exception) {
            Result.Failure(e.toDataError())
        }
    }


    override suspend fun forgotPassword(
        email: String
    ): EmptyResult<DataError.Remote> {
        return try {
            supabaseClient.auth.resetPasswordForEmail(
                email = email,
                redirectUrl = "brainest://brainest.app/auth/reset-password"
            )
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Failure(e.toDataError())
        }
    }

    override suspend fun resetPassword(
        deepLinkUrl: String,
        newPassword: String
    ): EmptyResult<DataError.Remote> {
        return try {
            val code = deepLinkUrl
                .substringAfter("code=", "")
                .substringBefore("&")
                .takeIf { it.isNotEmpty() }
                ?: return Result.Failure(DataError.Remote.UNAUTHORIZED)

            supabaseClient.auth.exchangeCodeForSession(code)

            supabaseClient.auth.updateUser {
                password = newPassword
            }

            Result.Success(Unit)
        } catch (e: Exception) {
            logger.error("Error resetting password", e)
            Result.Failure(e.toDataError())
        }
    }

    override suspend fun currentUserId(): String? {
        val session = supabaseClient.auth.currentSessionOrNull() ?: return null
        return session.user?.id
    }


}