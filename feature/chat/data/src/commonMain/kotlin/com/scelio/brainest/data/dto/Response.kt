package com.scelio.brainest.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Response(
    val id: String,
    val `object`: String,
    val created: Long,
    val model: String,
    @SerialName("output_text")
    val outputText: String? = null,
    val output: List<OutputItemDto>? = null,
    val usage: UsageDto? = null
)
