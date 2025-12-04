package com.scelio.brainest.domain.auth

// domain/auth/User.kt
data class User(
    val id: String,
    val email: String,
    val username: String,
    val hasVerifiedEmail: Boolean,
    val profilePictureUrl: String? = null
)