package com.scelio.brainest.flashcards.data

import com.scelio.brainest.domain.util.DataError
import com.scelio.brainest.domain.util.Result
import com.scelio.brainest.data.util.toDataError
import com.scelio.brainest.flashcards.domain.DocumentTranscriptionError
import com.scelio.brainest.flashcards.domain.DocumentTranscriptionResult
import com.scelio.brainest.flashcards.domain.DocumentTranscriptionService
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.timeout
import io.ktor.client.request.bearerAuth
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.http.isSuccess
import kotlinx.coroutines.TimeoutCancellationException

class DocumentTranscriptionServiceImpl(
    private val httpClient: HttpClient,
    private val apiKey: String,
    private val baseUrl: String = "https://api.openai.com/v1"
) : DocumentTranscriptionService {

    override suspend fun transcribeFile(
        fileId: String
    ): Result<DocumentTranscriptionResult, DocumentTranscriptionError> {
        if (apiKey.isBlank() || apiKey == "your-openai-api-key-here") {
            return Result.Failure(
                DocumentTranscriptionError.Remote(DataError.Remote.UNAUTHORIZED)
            )
        }

        val systemPrompt = """
            You are a transcription engine.
            Extract the document text verbatim.
            Preserve paragraph breaks where possible.
        """.trimIndent()
        val userPrompt = "Return only the extracted text. Do not add commentary."

        val request = OpenAiResponseRequest(
            model = "gpt-4.1-nano",
            input = listOf(
                OpenAiMessage(
                    role = "system",
                    content = listOf(OpenAiContent(type = "input_text", text = systemPrompt))
                ),
                OpenAiMessage(
                    role = "user",
                    content = listOf(
                        OpenAiContent(type = "input_file", fileId = fileId),
                        OpenAiContent(type = "input_text", text = userPrompt)
                    )
                )
            ),
            temperature = 0.0
        )

        return try {
            val response = httpClient.post("$baseUrl/responses") {
                contentType(ContentType.Application.Json)
                bearerAuth(apiKey)
                timeout {
                    requestTimeoutMillis = 120_000L
                    socketTimeoutMillis = 120_000L
                }
                setBody(request)
            }

            if (!response.status.isSuccess()) {
                return Result.Failure(
                    DocumentTranscriptionError.Remote(response.status.toDataError())
                )
            }

            val parsed = response.body<OpenAiResponse>()

            val text = parsed.extractedOutputText().trim()
            if (text.isBlank()) {
                Result.Failure(
                    DocumentTranscriptionError.Empty("Empty document transcription response.")
                )
            } else {
                Result.Success(DocumentTranscriptionResult(text = text))
            }
        } catch (e: Exception) {
            Result.Failure(DocumentTranscriptionError.Remote(e.toDataError()))
        }
    }
}
