package com.scelio.brainest.domain.models

data class MessageMetadata(
    val model: String? = null,
    val tokensUsed: Int? = null,
    val openAIResponseId: String? = null
)