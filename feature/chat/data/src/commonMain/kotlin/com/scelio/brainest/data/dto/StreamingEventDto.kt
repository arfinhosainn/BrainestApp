package com.scelio.brainest.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class StreamingEventDto(
    val type: String,
    val delta: String? = null
)