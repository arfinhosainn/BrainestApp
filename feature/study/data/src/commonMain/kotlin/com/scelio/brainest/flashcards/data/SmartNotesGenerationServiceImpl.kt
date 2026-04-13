package com.scelio.brainest.flashcards.data

import com.scelio.brainest.domain.util.DataError
import com.scelio.brainest.domain.util.Result
import com.scelio.brainest.data.util.toDataError
import com.scelio.brainest.flashcards.domain.SmartNotesGenerationError
import com.scelio.brainest.flashcards.domain.SmartNotesGenerationService
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.timeout
import io.ktor.client.request.bearerAuth
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import kotlinx.coroutines.TimeoutCancellationException

class SmartNotesGenerationServiceImpl(
    private val httpClient: HttpClient,
    private val apiKey: String,
    private val baseUrl: String = "https://api.openai.com/v1"
) : SmartNotesGenerationService {

    private val systemPrompt = """
        You are a study assistant.
        Create smart notes from the provided text.
        Use markdown headings with "## " for each section.
        Write short paragraphs and bullet points.
        Do not add any preamble or code fences.
    """.trimIndent()

    override suspend fun generateSmartNotes(
        text: String
    ): Result<String, SmartNotesGenerationError> {
        if (apiKey.isBlank() || apiKey == "your-openai-api-key-here") {
            return Result.Failure(
                SmartNotesGenerationError.Remote(DataError.Remote.UNAUTHORIZED)
            )
        }

        val request = OpenAiResponseRequest(
            model = "gpt-4.1-nano",
            input = listOf(
                OpenAiMessage(
                    role = "system",
                    content = listOf(OpenAiContent(type = "input_text", text = systemPrompt))
                ),
                OpenAiMessage(
                    role = "user",
                    content = listOf(OpenAiContent(type = "input_text", text = text))
                )
            ),
            temperature = 0.2
        )

        return requestSmartNotes(request)
    }

    override suspend fun generateSmartNotesFromFile(
        fileId: String
    ): Result<String, SmartNotesGenerationError> {
        if (apiKey.isBlank() || apiKey == "your-openai-api-key-here") {
            return Result.Failure(
                SmartNotesGenerationError.Remote(DataError.Remote.UNAUTHORIZED)
            )
        }

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
                        OpenAiContent(
                            type = "input_text",
                            text = "Use the attached document to create smart notes."
                        )
                    )
                )
            ),
            temperature = 0.2
        )

        return requestSmartNotes(request)
    }

    private suspend fun requestSmartNotes(
        request: OpenAiResponseRequest
    ): Result<String, SmartNotesGenerationError> {
        return try {
            val response = httpClient.post("$baseUrl/responses") {
                contentType(ContentType.Application.Json)
                bearerAuth(apiKey)
                timeout {
                    requestTimeoutMillis = 120_000L
                    socketTimeoutMillis = 120_000L
                }
                setBody(request)
            }.body<OpenAiResponse>()

            val notes = response.extractedOutputText().trim()
            if (notes.isBlank()) {
                Result.Failure(
                    SmartNotesGenerationError.Empty("Empty smart notes response.")
                )
            } else {
                Result.Success(notes)
            }
        } catch (e: Exception) {
            Result.Failure(SmartNotesGenerationError.Remote(e.toDataError()))
        }
    }
}
