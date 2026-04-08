package com.scelio.brainest.domain.models

import kotlin.time.Instant

data class Chat(
    val id: String,
    val userId: String,
    val title: String,
    val model: String = "gpt-4.1-nano",
    val systemPrompt: String? = null,
    val createdAt: Instant,
    val lastActivityAt: Instant,
    val messageCount: Int = 0
)
