package com.scelio.brainest.domain.models

import kotlin.time.Instant

data class Chat(
    val id: String,
    val userId: String,
    val title: String? = null,
    val model: String = "gpt-4o-mini",
    val systemPrompt: String? = null,
    val createdAt: Instant,
    val lastActivityAt: Instant,
    val messageCount: Int = 0
)