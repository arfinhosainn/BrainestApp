package com.scelio.brainest.flashcards.data

import com.scelio.brainest.domain.util.DataError
import com.scelio.brainest.domain.util.Result
import com.scelio.brainest.flashcards.domain.DocumentTranscriptionError
import com.scelio.brainest.flashcards.domain.DocumentTranscriptionResult
import com.scelio.brainest.flashcards.domain.DocumentTranscriptionService
import io.github.jan.supabase.exceptions.RestException
import io.github.jan.supabase.exceptions.UnknownRestException
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.network.sockets.ConnectTimeoutException
import io.ktor.client.network.sockets.SocketTimeoutException
import io.ktor.client.request.bearerAuth
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType

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
            model = "gpt-4o-mini",
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
                setBody(request)
            }.body<OpenAiResponse>()

            val text = response.extractedOutputText().trim()
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
