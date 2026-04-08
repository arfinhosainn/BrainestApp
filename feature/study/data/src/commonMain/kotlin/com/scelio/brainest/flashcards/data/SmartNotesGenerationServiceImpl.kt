package com.scelio.brainest.flashcards.data

import com.scelio.brainest.domain.util.DataError
import com.scelio.brainest.domain.util.Result
import com.scelio.brainest.flashcards.domain.SmartNotesGenerationError
import com.scelio.brainest.flashcards.domain.SmartNotesGenerationService
import io.github.jan.supabase.exceptions.RestException
import io.github.jan.supabase.exceptions.UnknownRestException
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.network.sockets.ConnectTimeoutException
import io.ktor.client.network.sockets.SocketTimeoutException
import io.ktor.client.plugins.HttpRequestTimeoutException
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

    private fun Exception.toDataError(): DataError.Remote {
        return when (this) {
            is RestException -> {
                when (statusCode) {
                    400 -> DataError.Remote.BAD_REQUEST
                    401 -> DataError.Remote.UNAUTHORIZED
                    403 -> DataError.Remote.FORBIDDEN
                    404 -> DataError.Remote.NOT_FOUND
                    408 -> DataError.Remote.REQUEST_TIMEOUT
                    409 -> DataError.Remote.CONFLICT
                    413 -> DataError.Remote.PAYLOAD_TOO_LARGE
                    429 -> DataError.Remote.TOO_MANY_REQUESTS
                    in 500..502 -> DataError.Remote.SERVER_ERROR
                    503 -> DataError.Remote.SERVICE_UNAVAILABLE
                    else -> DataError.Remote.UNKNOWN
                }
            }

            is UnknownRestException -> DataError.Remote.UNKNOWN
            is ConnectTimeoutException -> DataError.Remote.SERVER_ERROR
            is SocketTimeoutException -> DataError.Remote.REQUEST_TIMEOUT
            is HttpRequestTimeoutException -> DataError.Remote.REQUEST_TIMEOUT
            is TimeoutCancellationException -> DataError.Remote.REQUEST_TIMEOUT
            else -> {
                if (message?.contains("Unable to resolve host") == true ||
                    message?.contains("Network is unreachable") == true
                ) {
                    DataError.Remote.NO_INTERNET
                } else {
                    DataError.Remote.UNKNOWN
                }
            }
        }
    }
}
