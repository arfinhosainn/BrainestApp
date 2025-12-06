package com.scelio.brainest.domain.auth

import com.scelio.brainest.domain.util.DataError
import com.scelio.brainest.domain.util.EmptyResult
import com.scelio.brainest.domain.util.Result

interface AuthService {
    suspend fun register(
        email: String,
        password: String,
        username: String
    ): EmptyResult<DataError.Remote>

    suspend fun resendVerificationEmail(
        email: String
    ): EmptyResult<DataError.Remote>

    suspend fun login(
        email: String,
        password: String
    ): Result<AuthInfo, DataError.Remote>

    suspend fun verifyEmail(deepLinkUrl: String): EmptyResult<DataError.Remote>

    suspend fun forgotPassword(
        email: String
    ): EmptyResult<DataError.Remote>

    suspend fun resetPassword(
        deepLinkUrl: String,
        newPassword: String
    ): EmptyResult<DataError.Remote>


}