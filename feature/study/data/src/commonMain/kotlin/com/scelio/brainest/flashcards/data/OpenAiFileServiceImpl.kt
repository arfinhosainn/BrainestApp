package com.scelio.brainest.flashcards.data

import com.scelio.brainest.domain.util.DataError
import com.scelio.brainest.domain.util.Result
import com.scelio.brainest.flashcards.domain.OpenAiFileService
import io.github.jan.supabase.exceptions.RestException
import io.github.jan.supabase.exceptions.UnknownRestException
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.network.sockets.ConnectTimeoutException
import io.ktor.client.network.sockets.SocketTimeoutException
import io.ktor.client.plugins.HttpRequestTimeoutException
import io.ktor.client.plugins.timeout
import io.ktor.client.request.bearerAuth
import io.ktor.client.request.delete
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.request.forms.MultiPartFormDataContent
import io.ktor.client.request.forms.formData
import io.ktor.http.ContentDisposition
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.Headers
import io.ktor.http.contentType
import io.ktor.http.isSuccess
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.serialization.Serializable

class OpenAiFileServiceImpl(
    private val httpClient: HttpClient,
    private val apiKey: String,
    private val baseUrl: String = "https://api.openai.com/v1"
) : OpenAiFileService {

    override suspend fun uploadDocument(
        fileData: ByteArray,
        fileName: String,
        mimeType: String
    ): Result<String, DataError.Remote> {
        if (apiKey.isBlank() || apiKey == "your-openai-api-key-here") {
            return Result.Failure(DataError.Remote.UNAUTHORIZED)
        }

        return try {
            val response = httpClient.post("$baseUrl/files") {
                bearerAuth(apiKey)
                timeout {
                    requestTimeoutMillis = 120_000L
                    socketTimeoutMillis = 120_000L
                }
                setBody(
                    MultiPartFormDataContent(
                        formData {
                            append("purpose", "assistants")
                            append(
                                key = "file",
                                value = fileData,
                                headers = Headers.build {
                                    append(
                                        HttpHeaders.ContentDisposition,
                                        ContentDisposition.File.withParameter(
                                            ContentDisposition.Parameters.Name,
                                            "file"
                                        ).withParameter(
                                            ContentDisposition.Parameters.FileName,
                                            fileName
                                        ).toString()
                                    )
                                    append(HttpHeaders.ContentType, mimeType)
                                }
                            )
                        }
                    )
                )
            }

            if (!response.status.isSuccess()) {
                return Result.Failure(response.status.toDataError())
            }

            val parsed = response.body<OpenAiFileUploadResponse>()

            Result.Success(parsed.id)
        } catch (e: Exception) {
            Result.Failure(e.toDataError())
        }
    }

    override suspend fun deleteFile(fileId: String): Result<Unit, DataError.Remote> {
        if (apiKey.isBlank() || apiKey == "your-openai-api-key-here") {
            return Result.Failure(DataError.Remote.UNAUTHORIZED)
        }

        return try {
            val response = httpClient.delete("$baseUrl/files/$fileId") {
                bearerAuth(apiKey)
                timeout {
                    requestTimeoutMillis = 120_000L
                    socketTimeoutMillis = 120_000L
                }
            }
            if (!response.status.isSuccess()) {
                return Result.Failure(response.status.toDataError())
            }
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Failure(e.toDataError())
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
}

@Serializable
private data class OpenAiFileUploadResponse(
    val id: String
)
