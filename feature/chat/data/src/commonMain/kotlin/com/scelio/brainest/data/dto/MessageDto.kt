package com.scelio.brainest.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class MessageDto(
    val role: String, // "user", "assistant", "system"
    val content: List<ContentDto>
)