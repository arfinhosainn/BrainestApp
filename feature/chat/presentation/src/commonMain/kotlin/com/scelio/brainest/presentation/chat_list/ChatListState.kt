package com.scelio.brainest.presentation.chat_list

import com.scelio.brainest.presentation.model.ChatItemUi
import com.scelio.brainest.presentation.model.ChatMessageUi
import com.scelio.brainest.presentation.util.UiText

data class ChatListState(
    val isLoading: Boolean = false,
    val isCreatingChat: Boolean = false,
    val chats: List<ChatItemUi> = emptyList(),
    val error: UiText? = null
)