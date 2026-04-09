package com.scelio.brainest.data.chat

import com.scelio.brainest.data.dto.ChatResult

sealed interface OpenAiStreamEvent {
    data class OutputTextDelta(
        val delta: String
    ) : OpenAiStreamEvent

    data class Completed(
        val response: ChatResult?
    ) : OpenAiStreamEvent
}
