package com.scelio.brainest.presentation.model

import com.scelio.brainest.presentation.util.UiText

data class ChatItemUi(
    val id: String,
    val title: String,
    val lastMessage: String?,
    val timestamp: UiText,
    val model: String,
    val unreadCount: Int = 0,
    val isSelected: Boolean = false
)