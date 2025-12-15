package com.scelio.brainest.data.chat

import com.scelio.brainest.data.dto.ChatRequest
import com.scelio.brainest.data.dto.ChatResult
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.bearerAuth
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType

class OpenAIApiServiceImpl(
    private val httpClient: HttpClient,
    private val apiKey: String,
    private val baseUrl: String = "https://api.openai.com/v1"
) : OpenAIApiService {
    override suspend fun chat(request: ChatRequest): ChatResult {
        return httpClient.post("$baseUrl/responses") {
            contentType(ContentType.Application.Json)
            bearerAuth(apiKey)
            setBody(request)
        }.body()
    }


}