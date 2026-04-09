package com.scelio.brainest.data.chat

import com.scelio.brainest.data.dto.ChatRequest
import com.scelio.brainest.data.dto.ChatResult
import kotlinx.coroutines.flow.Flow

interface OpenAIApiService {
    suspend fun chat(request: ChatRequest): ChatResult
    fun streamChat(request: ChatRequest): Flow<OpenAiStreamEvent>
}
