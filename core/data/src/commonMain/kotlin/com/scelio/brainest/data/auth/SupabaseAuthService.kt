package com.scelio.brainest.data.auth

import com.scelio.brainest.domain.auth.AuthService
import com.scelio.brainest.domain.util.DataError
import com.scelio.brainest.domain.util.EmptyResult
import com.scelio.brainest.domain.util.Result
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.OtpType
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.parseSessionFromUrl
import io.github.jan.supabase.auth.providers.builtin.Email
import io.github.jan.supabase.auth.user.UserSession
import io.github.jan.supabase.exceptions.RestException
import io.github.jan.supabase.exceptions.UnknownRestException
import io.ktor.client.network.sockets.ConnectTimeoutException
import io.ktor.client.network.sockets.SocketTimeoutException
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put

class SupabaseAuthService(
    private val supabaseClient: SupabaseClient
) : AuthService {

    override suspend fun register(
        email: String,
        password: String,
        username: String
    ): EmptyResult<DataError.Remote> {
        return try {
            supabaseClient.auth.signUpWith(Email) {
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


    private fun Exception.toDataError(): DataError.Remote {
        return when (this) {
            is RestException -> {
                when (statusCode) {
                    400 -> DataError.Remote.BAD_REQUEST
                    401 -> DataError.Remote.UNAUTHORIZED
                    403 -> DataError.Remote.FORBIDDEN
                    404 -> DataError.Remote.NOT_FOUND
                    408 -> DataError.Remote.REQUEST_TIMEOUT
                    409 -> DataError.Remote.CONFLICT
                    413 -> DataError.Remote.PAYLOAD_TOO_LARGE
                    429 -> DataError.Remote.TOO_MANY_REQUESTS
                    in 500..502 -> DataError.Remote.SERVER_ERROR
                    503 -> DataError.Remote.SERVICE_UNAVAILABLE
                    else -> DataError.Remote.UNKNOWN
                }
            }

            is UnknownRestException -> DataError.Remote.UNKNOWN
            is ConnectTimeoutException -> DataError.Remote.SERVER_ERROR
            is SocketTimeoutException -> DataError.Remote.REQUEST_TIMEOUT

            else -> {
                if (this.message?.contains("Unable to resolve host") == true ||
                    this.message?.contains("Network is unreachable") == true
                ) {
                    DataError.Remote.NO_INTERNET
                } else {
                    DataError.Remote.UNKNOWN
                }
            }
        }
    }
}