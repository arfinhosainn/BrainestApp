package com.scelio.brainest.presentation.chat_list

import com.scelio.brainest.presentation.model.ChatItemUi
import com.scelio.brainest.presentation.model.ChatMessageUi
import com.scelio.brainest.presentation.util.UiText

data class ChatListState(
    val chats: List<ChatItemUi> = emptyList(),
    val error: UiText? = null,
    val selectedChatId: String? = null,
    val isLoading: Boolean = false,
)