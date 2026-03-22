package com.scelio.brainest.flashcards.data

import kotlinx.serialization.Serializable

@Serializable
data class OpenAiResponseRequest(
    val model: String,
    val input: List<OpenAiMessage>,
    val temperature: Double? = null,
    val max_output_tokens: Int? = null
)

@Serializable
data class OpenAiMessage(
    val role: String,
    val content: List<OpenAiContent>
)

@Serializable
data class OpenAiContent(
    val type: String,
    val text: String
)

@Serializable
data class OpenAiResponse(
    val output: List<OpenAiOutputItem> = emptyList()
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
data class OpenAiOutputItem(
    val type: String? = null,
    val content: List<OpenAiOutputContent> = emptyList()
)

@Serializable
data class OpenAiOutputContent(
    val type: String,
    val text: String? = null
)
