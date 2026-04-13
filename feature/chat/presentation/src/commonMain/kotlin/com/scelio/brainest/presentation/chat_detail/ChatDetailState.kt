package com.scelio.brainest.presentation.chat_detail

import androidx.compose.runtime.Stable
import com.scelio.brainest.presentation.model.ChatItemUi
import com.scelio.brainest.presentation.model.ChatMessageUi
import com.scelio.brainest.presentation.util.UiText

@Stable
data class ChatDetailState(
    val chatUi: ChatItemUi? = null,
    val recentChats: List<ChatItemUi> = emptyList(),
    val isLoading: Boolean = false,
    val error: UiText? = null,
    val messageText: String = "",
    val canSendMessage: Boolean = false,
    val isPaginationLoading: Boolean = false,
    val paginationError: UiText? = null,
    val endReached: Boolean = false,
    val isNearBottom: Boolean = false,
)
