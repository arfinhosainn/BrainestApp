package com.scelio.brainest.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class ChatResult(
    val id: String? = null,
    val `object`: String? = null,
    val created: Long? = null,
    val model: String? = null,
    val output: List<OutputItemDto> = emptyList(),
    val usage: UsageDto? = null
) {
    fun extractedOutputText(): String {
        return output
            .flatMap { it.content }
            .filter { it.type == "output_text" }
            .joinToString(separator = "") { it.text.orEmpty() }
            .trim()
    }
}




@Serializable
data class OutputContentDto(
    val type: String,
    val text: String? = null
)