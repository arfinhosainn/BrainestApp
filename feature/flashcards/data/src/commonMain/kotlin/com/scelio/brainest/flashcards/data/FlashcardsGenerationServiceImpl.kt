package com.scelio.brainest.flashcards.data

import com.scelio.brainest.domain.util.DataError
import com.scelio.brainest.domain.util.Result
import com.scelio.brainest.flashcards.domain.FlashcardInput
import com.scelio.brainest.flashcards.domain.FlashcardsGenerationError
import com.scelio.brainest.flashcards.domain.FlashcardsGenerationService
import io.github.jan.supabase.exceptions.RestException
import io.github.jan.supabase.exceptions.UnknownRestException
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.bearerAuth
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.client.network.sockets.ConnectTimeoutException
import io.ktor.client.network.sockets.SocketTimeoutException
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

class FlashcardsGenerationServiceImpl(
    private val httpClient: HttpClient,
    private val apiKey: String,
    private val baseUrl: String = "https://api.openai.com/v1"
) : FlashcardsGenerationService {

    private val json = Json { ignoreUnknownKeys = true }

    override suspend fun generateFlashcards(
        prompt: String,
        count: Int
    ): Result<List<FlashcardInput>, FlashcardsGenerationError> {
        if (apiKey.isBlank() || apiKey == "your-openai-api-key-here") {
            return Result.Failure(
                FlashcardsGenerationError.Remote(DataError.Remote.UNAUTHORIZED)
            )
        }

        val systemPrompt = buildSystemPrompt(count)
        val userPrompt = buildUserPrompt(prompt)

        val request = OpenAiResponseRequest(
            model = "gpt-4o-mini",
            input = listOf(
                OpenAiMessage(
                    role = "system",
                    content = listOf(OpenAiContent(type = "input_text", text = systemPrompt))
                ),
                OpenAiMessage(
                    role = "user",
                    content = listOf(OpenAiContent(type = "input_text", text = userPrompt))
                )
            ),
            temperature = 0.3
        )

        return try {
            val response = httpClient.post("$baseUrl/responses") {
                contentType(ContentType.Application.Json)
                bearerAuth(apiKey)
                setBody(request)
            }.body<OpenAiResponse>()

            val rawText = response.extractedOutputText()
            when (val parseResult = parseFlashcards(rawText, count)) {
                is Result.Success -> {
                    if (parseResult.data.isEmpty()) {
                        Result.Failure(FlashcardsGenerationError.Empty("No flashcards were generated."))
                    } else {
                        parseResult
                    }
                }
                is Result.Failure -> Result.Failure(parseResult.error)
            }
        } catch (e: Exception) {
            Result.Failure(FlashcardsGenerationError.Remote(e.toDataError()))
        }
    }

    private fun buildSystemPrompt(count: Int): String {
        return """
            You are a flashcard generator.
            Return ONLY valid JSON.
            Output a JSON array with exactly $count items.
            Each item must have:
              - "front": question or prompt
              - "back": answer or explanation
              - "order_index": integer index starting at 0
            Do not include any extra text.
        """.trimIndent()
    }

    private fun buildUserPrompt(prompt: String): String {
        return """
            Topic: $prompt
            Generate flashcards that are clear, concise, and correct.
        """.trimIndent()
    }

    private fun parseFlashcards(
        rawText: String,
        count: Int
    ): Result<List<FlashcardInput>, FlashcardsGenerationError> {
        val cleaned = rawText
            .removePrefix("```json")
            .removePrefix("```")
            .removeSuffix("```")
            .trim()

        val jsonArray = extractJsonArray(cleaned)
            ?: return Result.Failure(FlashcardsGenerationError.Parse("No JSON array found in output."))

        return try {
            val decoded = json.decodeFromString<List<FlashcardJson>>(jsonArray)
            val cards = decoded.take(count).mapIndexed { index, item ->
                FlashcardInput(
                    front = item.front.trim(),
                    back = item.back.trim(),
                    orderIndex = item.orderIndex ?: index
                )
            }.filter { it.front.isNotBlank() && it.back.isNotBlank() }
            Result.Success(cards)
        } catch (e: Exception) {
            Result.Failure(FlashcardsGenerationError.Parse("Failed to parse JSON output."))
        }
    }

    private fun extractJsonArray(text: String): String? {
        val start = text.indexOf('[')
        val end = text.lastIndexOf(']')
        if (start == -1 || end == -1 || end <= start) {
            return null
        }
        return text.substring(start, end + 1)
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

@Serializable
private data class FlashcardJson(
    val front: String,
    val back: String,
    @SerialName("order_index") val orderIndex: Int? = null
)
