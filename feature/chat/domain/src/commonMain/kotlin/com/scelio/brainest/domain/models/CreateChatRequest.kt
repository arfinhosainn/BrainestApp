package com.scelio.brainest.domain.models

data class CreateChatRequest(
    val userId: String,
    val title: String,
    val systemPrompt: String? = null,
    val model: String = "gpt-4.1-nano"
)
