package com.scelio.brainest.data.chat

import com.scelio.brainest.data.dto.ChatRequest
import com.scelio.brainest.data.dto.ChatResult

interface OpenAIApiService {
    suspend fun chat(request: ChatRequest): ChatResult
}