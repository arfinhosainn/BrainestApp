package com.scelio.brainest.presentation.model

data class ChatItemUi(
    val id: String,
    val title: String,
    val lastMessage: String,
    val lastMessageTime: String, // "2 min ago", "Yesterday"
    val unreadCount: Int = 0,
    val isActive: Boolean = false
)
