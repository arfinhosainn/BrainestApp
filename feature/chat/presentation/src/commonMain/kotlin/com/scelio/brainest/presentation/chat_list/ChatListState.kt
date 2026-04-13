package com.scelio.brainest.presentation.chat_list

import androidx.compose.runtime.Stable
import com.scelio.brainest.presentation.model.ChatItemUi
import com.scelio.brainest.presentation.model.ChatMessageUi
import com.scelio.brainest.presentation.util.UiText

@Stable
data class ChatListState(
    val isLoading: Boolean = false,
    val searchText: String = "",
    val isCreatingChat: Boolean = false,
    val chats: List<ChatItemUi> = emptyList(),
    val error: UiText? = null
)