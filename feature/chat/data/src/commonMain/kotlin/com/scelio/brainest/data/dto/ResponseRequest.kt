package com.scelio.brainest.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class ResponseRequest(
    val model: String,
    val input: List<MessageDto>,
    val stream: Boolean? = null,
    val tools: List<ToolDto>? = null
)