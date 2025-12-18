package com.scelio.brainest.presentation.chat_list

import com.scelio.brainest.presentation.model.ChatMessageUi


sealed interface ChatListAction {
    data object OnFabClick : ChatListAction
    data object OnRefresh : ChatListAction
    data class OnChatClick(val chatId: String) : ChatListAction
}