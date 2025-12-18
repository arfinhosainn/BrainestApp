package com.scelio.brainest.presentation.chat_list

import com.scelio.brainest.presentation.util.UiText

sealed interface ChatListEvent {
    data class NavigateToChat(val chatId: String) : ChatListEvent
    data class ShowError(val error: UiText) : ChatListEvent
}