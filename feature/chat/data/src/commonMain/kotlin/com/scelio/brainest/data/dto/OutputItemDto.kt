package com.scelio.brainest.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class OutputItemDto(
    val type: String? = null,
    val content: List<OutputContentDto> = emptyList()
)
