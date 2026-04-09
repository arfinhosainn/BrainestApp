package com.scelio.brainest.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class ChatRequest(
    val model: String,
    val input: List<MessageDto>,
    val instructions: String? = null,
    val stream: Boolean? = null
)
