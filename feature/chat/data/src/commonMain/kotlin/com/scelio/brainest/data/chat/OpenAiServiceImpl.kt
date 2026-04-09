package com.scelio.brainest.data.chat

import com.scelio.brainest.data.dto.ChatRequest
import com.scelio.brainest.data.dto.ChatResult
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.preparePost
import io.ktor.client.request.bearerAuth
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsChannel
import io.ktor.http.HttpHeaders
import io.ktor.http.isSuccess
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.utils.io.readUTF8Line
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.decodeFromJsonElement
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

class OpenAIApiServiceImpl(
    private val httpClient: HttpClient,
    private val apiKey: String,
    private val baseUrl: String = "https://api.openai.com/v1"
) : OpenAIApiService {
    private val json = Json {
        ignoreUnknownKeys = true
    }

    override suspend fun chat(request: ChatRequest): ChatResult {
        return httpClient.post("$baseUrl/responses") {
            contentType(ContentType.Application.Json)
            bearerAuth(apiKey)
            setBody(request)
        }.body()
    }

    override fun streamChat(request: ChatRequest): Flow<OpenAiStreamEvent> = flow {
        httpClient.preparePost("$baseUrl/responses") {
            contentType(ContentType.Application.Json)
            bearerAuth(apiKey)
            header(HttpHeaders.Accept, "text/event-stream")
            setBody(request.copy(stream = true))
        }.execute { response ->
            if (!response.status.isSuccess()) {
                throw IllegalStateException("Streaming request failed with status ${response.status.value}")
            }

            val channel = response.bodyAsChannel()
            val dataLines = mutableListOf<String>()

            suspend fun flushEvent() {
                if (dataLines.isEmpty()) return

                val payload = dataLines.joinToString(separator = "\n")
                dataLines.clear()

                if (payload == "[DONE]") return

                val event = json.parseToJsonElement(payload).jsonObject
                when (event["type"]?.jsonPrimitive?.contentOrNull) {
                    "response.output_text.delta" -> {
                        val delta = event["delta"]?.jsonPrimitive?.contentOrNull.orEmpty()
                        if (delta.isNotEmpty()) {
                            emit(OpenAiStreamEvent.OutputTextDelta(delta))
                        }
                    }

                    "response.completed" -> {
                        emit(OpenAiStreamEvent.Completed(event.extractResponse()))
                    }

                    "error",
                    "response.failed" -> {
                        val message = event.errorMessage()
                        throw IllegalStateException(message)
                    }
                }
            }

            while (!channel.isClosedForRead) {
                val line = channel.readUTF8Line() ?: break
                if (line.isBlank()) {
                    flushEvent()
                    continue
                }
                if (line.startsWith("data:")) {
                    dataLines += line.removePrefix("data:").trimStart()
                }
            }

            flushEvent()
        }
    }
}

private fun JsonObject.extractResponse(): ChatResult? {
    val responseElement = this["response"] ?: return null
    return runCatching {
        Json {
            ignoreUnknownKeys = true
        }.decodeFromJsonElement<ChatResult>(responseElement)
    }.getOrNull()
}

private fun JsonObject.errorMessage(): String {
    return this["error"]
        ?.jsonObject
        ?.get("message")
        ?.jsonPrimitive
        ?.contentOrNull
        ?: "Streaming response failed"
}
