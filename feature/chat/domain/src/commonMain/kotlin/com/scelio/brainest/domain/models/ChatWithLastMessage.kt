package com.scelio.brainest.domain.models

data class ChatWithLastMessage(
    val chat: Chat,
    val lastMessage: ChatMessage?
)