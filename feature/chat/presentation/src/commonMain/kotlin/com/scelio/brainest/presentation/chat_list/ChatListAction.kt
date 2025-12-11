package com.scelio.brainest.presentation.chat_list

import com.scelio.brainest.presentation.model.ChatMessageUi


sealed interface ChatListAction {
    data object OnDismissLogoutDialog: ChatListAction
    data object OnCreateChatClick: ChatListAction
    data object OnProfileSettingsClick: ChatListAction
    data class OnChatClick(val chat: ChatMessageUi): ChatListAction
}