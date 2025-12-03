package com.scelio.brainest.domain.auth

import com.scelio.brainest.domain.util.DataError
import com.scelio.brainest.domain.util.EmptyResult

interface AuthService {
    suspend fun register(
        email: String,
        password: String,
        username: String
    ): EmptyResult<DataError.Remote>

    suspend fun resendVerificationEmail(
        email: String
    ): EmptyResult<DataError.Remote>


}