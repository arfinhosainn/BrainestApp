package com.scelio.brainest.data.dto.requests

import kotlinx.serialization.Serializable

data class RegisterRequest(
    val email: String,
    val username: String,
    val password: String
)