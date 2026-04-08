package com.scelio.brainest.quiz.data

import com.scelio.brainest.domain.util.DataError
import com.scelio.brainest.domain.util.Result
import com.scelio.brainest.flashcards.data.OpenAiContent
import com.scelio.brainest.flashcards.data.OpenAiMessage
import com.scelio.brainest.flashcards.data.OpenAiResponse
import com.scelio.brainest.flashcards.data.OpenAiResponseRequest
import com.scelio.brainest.quiz.domain.QuizGenerationError
import com.scelio.brainest.quiz.domain.QuizGenerationService
import com.scelio.brainest.quiz.domain.QuizQuestionInput
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
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

class QuizGenerationServiceImpl(
    private val httpClient: HttpClient,
    private val apiKey: String,
    private val baseUrl: String = "https://api.openai.com/v1"
) : QuizGenerationService {

    private val json = Json { ignoreUnknownKeys = true }

    override suspend fun generateQuizFromText(
        prompt: String,
        count: Int,
        multipleChoice: Boolean
    ): Result<List<QuizQuestionInput>, QuizGenerationError> {
        if (apiKey.isBlank() || apiKey == "your-openai-api-key-here") {
            return Result.Failure(
                QuizGenerationError.Remote(DataError.Remote.UNAUTHORIZED)
            )
        }

        val systemPrompt = buildSystemPrompt(count, multipleChoice)
        val userPrompt = buildUserPrompt(prompt)

        val request = OpenAiResponseRequest(
            model = "gpt-4.1-nano",
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

        return requestQuiz(request, count)
    }

    override suspend fun generateQuizFromFile(
        fileId: String,
        count: Int,
        multipleChoice: Boolean
    ): Result<List<QuizQuestionInput>, QuizGenerationError> {
        if (apiKey.isBlank() || apiKey == "your-openai-api-key-here") {
            return Result.Failure(
                QuizGenerationError.Remote(DataError.Remote.UNAUTHORIZED)
            )
        }

        val systemPrompt = buildSystemPrompt(count, multipleChoice)
        val userPrompt = """
            Use the attached document to generate quiz questions.
            Focus on the most important concepts and definitions.
        """.trimIndent()

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
            temperature = 0.3
        )

        return requestQuiz(request, count)
    }

    private suspend fun requestQuiz(
        request: OpenAiResponseRequest,
        count: Int
    ): Result<List<QuizQuestionInput>, QuizGenerationError> {
        return try {
            val response = httpClient.post("$baseUrl/responses") {
                contentType(ContentType.Application.Json)
                bearerAuth(apiKey)
                setBody(request)
            }.body<OpenAiResponse>()

            val rawText = response.extractedOutputText()
            when (val parseResult = parseQuestions(rawText, count)) {
                is Result.Success -> {
                    if (parseResult.data.isEmpty()) {
                        Result.Failure(QuizGenerationError.Empty("No quiz questions were generated."))
                    } else {
                        parseResult
                    }
                }
                is Result.Failure -> Result.Failure(parseResult.error)
            }
        } catch (e: Exception) {
            Result.Failure(QuizGenerationError.Remote(e.toDataError()))
        }
    }

    private fun buildSystemPrompt(count: Int, multipleChoice: Boolean): String {
        val format = if (multipleChoice) {
            """
            Each item must have:
              - "question": the question text
              - "options": array of 4 answer options
              - "correct_index": integer index of the correct option (0-3)
              - "order_index": integer index starting at 0
            """
        } else {
            """
            Each item must have:
              - "question": the question text
              - "options": array of 4 answer options
              - "correct_index": integer index of the correct option (0-3)
              - "order_index": integer index starting at 0
            """
        }

        return """
            You are a quiz generator.
            Return ONLY valid JSON.
            Output a JSON array with exactly $count items.
            $format
            Do not include any extra text.
        """.trimIndent()
    }

    private fun buildUserPrompt(prompt: String): String {
        return """
            Topic: $prompt
            Generate quiz questions that are clear, concise, and correct.
        """.trimIndent()
    }

    private fun parseQuestions(
        rawText: String,
        count: Int
    ): Result<List<QuizQuestionInput>, QuizGenerationError> {
        val cleaned = rawText
            .removePrefix("```json")
            .removePrefix("```")
            .removeSuffix("```")
            .trim()

        val jsonArray = extractJsonArray(cleaned)
            ?: return Result.Failure(QuizGenerationError.Parse("No JSON array found in output."))

        return try {
            val decoded = json.decodeFromString<List<QuizQuestionJson>>(jsonArray)
            val questions = decoded.take(count).mapIndexed { index, item ->
                QuizQuestionInput(
                    question = item.question.trim(),
                    options = item.options.map { it.trim() }.take(4),
                    correctIndex = item.correctIndex.coerceIn(0, 3),
                    orderIndex = item.orderIndex ?: index
                )
            }.filter { it.question.isNotBlank() && it.options.size == 4 }
            Result.Success(questions)
        } catch (e: Exception) {
            Result.Failure(QuizGenerationError.Parse("Failed to parse JSON output."))
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
private data class QuizQuestionJson(
    val question: String,
    val options: List<String>,
    @SerialName("correct_index") val correctIndex: Int,
    @SerialName("order_index") val orderIndex: Int? = null
)
