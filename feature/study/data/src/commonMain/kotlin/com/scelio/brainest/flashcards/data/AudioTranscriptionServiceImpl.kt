package com.scelio.brainest.flashcards.data

import com.scelio.brainest.domain.util.DataError
import com.scelio.brainest.domain.util.Result
import com.scelio.brainest.flashcards.domain.AudioChunkData
import com.scelio.brainest.flashcards.domain.AudioTranscriptionError
import com.scelio.brainest.flashcards.domain.AudioTranscriptionResult
import com.scelio.brainest.flashcards.domain.AudioTranscriptionService
import io.github.jan.supabase.exceptions.RestException
import io.github.jan.supabase.exceptions.UnknownRestException
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.network.sockets.ConnectTimeoutException
import io.ktor.client.network.sockets.SocketTimeoutException
import io.ktor.client.plugins.HttpRequestTimeoutException
import io.ktor.client.plugins.timeout
import io.ktor.client.request.bearerAuth
import io.ktor.client.request.forms.MultiPartFormDataContent
import io.ktor.client.request.forms.formData
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders
import io.ktor.http.isSuccess
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

class AudioTranscriptionServiceImpl(
    private val httpClient: HttpClient,
    private val apiKey: String,
    private val baseUrl: String = "https://api.deepinfra.com/v1/openai"
) : AudioTranscriptionService {

    override suspend fun transcribeChunk(
        chunk: AudioChunkData
    ): Result<AudioTranscriptionResult, AudioTranscriptionError> {
        if (apiKey.isBlank() || apiKey == "your-openai-api-key-here") {
            return Result.Failure(AudioTranscriptionError.Remote(DataError.Remote.UNAUTHORIZED))
        }

        return try {
            val response = httpClient.post("$baseUrl/audio/transcriptions") {
                bearerAuth(apiKey)
                timeout {
                    requestTimeoutMillis = 120_000L
                    socketTimeoutMillis = 120_000L
                }
                setBody(
                    MultiPartFormDataContent(
                        formData {
                            append("model", "openai/whisper-large-v3")
                            append(
                                "file",
                                chunk.bytes,
                                Headers.build {
                                    append(HttpHeaders.ContentType, chunk.mimeType)
                                    append(
                                        HttpHeaders.ContentDisposition,
                                        "form-data; name=\"file\"; filename=\"${chunk.fileName}\""
                                    )
                                }
                            )
                            chunk.language?.let { append("language", it) }
                            append("response_format", "json")
                        }
                    )
                )
            }

            if (!response.status.isSuccess()) {
                return Result.Failure(
                    AudioTranscriptionError.Remote(response.status.toDataError())
                )
            }

            val parsed = response.body<WhisperTranscriptionResponse>()

            val text = parsed.text.trim()
            if (text.isBlank()) {
                Result.Failure(AudioTranscriptionError.Empty("Empty transcription response."))
            } else {
                Result.Success(
                    AudioTranscriptionResult(
                        text = text,
                        language = parsed.language,
                        durationSeconds = parsed.duration
                    )
                )
            }
        } catch (e: Exception) {
            Result.Failure(AudioTranscriptionError.Remote(e.toDataError()))
        }
    }

    private fun io.ktor.http.HttpStatusCode.toDataError(): DataError.Remote {
        return when (value) {
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

@Serializable
private data class WhisperTranscriptionResponse(
    val text: String,
    val language: String? = null,
    val duration: Double? = null,
    @SerialName("request_id") val requestId: String? = null
)
