package com.scelio.brainest.presentation.model

import kotlin.time.Instant


data class ChatItemUi(
    val id: String,
    val title: String,
    val lastMessage: String?,
    val timestamp: Instant,
    val model: String,
    val unreadCount: Int = 0,
    val isSelected: Boolean = false
)