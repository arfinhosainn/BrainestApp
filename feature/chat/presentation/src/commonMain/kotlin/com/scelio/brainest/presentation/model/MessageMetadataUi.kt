package com.scelio.brainest.presentation.model

data class MessageMetadataUi(
    val model: String?,
    val tokensUsed: Int?,
    val responseTime: Long? = null
)