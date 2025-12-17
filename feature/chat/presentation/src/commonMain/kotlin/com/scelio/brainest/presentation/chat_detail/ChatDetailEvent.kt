package com.scelio.brainest.presentation.chat_detail

import com.scelio.brainest.presentation.util.UiText

sealed interface ChatDetailEvent {
    data class OnError(val error: UiText): ChatDetailEvent
    data object OnNewMessage: ChatDetailEvent
}