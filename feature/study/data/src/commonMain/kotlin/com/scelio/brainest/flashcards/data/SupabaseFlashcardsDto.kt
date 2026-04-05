package com.scelio.brainest.flashcards.data

import com.scelio.brainest.flashcards.domain.Deck
import com.scelio.brainest.flashcards.domain.Flashcard
import com.scelio.brainest.flashcards.domain.FlashcardResult
import com.scelio.brainest.flashcards.domain.SessionRecord
import com.scelio.brainest.flashcards.domain.StudySource
import com.scelio.brainest.flashcards.domain.StudySourceType
import com.scelio.brainest.flashcards.domain.StudySession
import com.scelio.brainest.quiz.domain.QuizQuestion
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlin.time.Instant
import kotlinx.datetime.Instant as KxInstant

@Serializable
data class SupabaseDeckDto(
    val id: String,
    @SerialName("user_id") val userId: String,
    val title: String,
    @SerialName("source_filename") val sourceFilename: String? = null,
    @SerialName("total_cards") val totalCards: Int,
    @SerialName("created_at") val createdAt: String
)

@Serializable
data class SupabaseFlashcardDto(
    val id: String,
    @SerialName("deck_id") val deckId: String,
    val front: String,
    val back: String,
    @SerialName("order_index") val orderIndex: Int
)

@Serializable
data class SupabaseStudySessionDto(
    val id: String,
    @SerialName("user_id") val userId: String,
    @SerialName("deck_id") val deckId: String,
    @SerialName("cards_known") val cardsKnown: Int,
    @SerialName("cards_unknown") val cardsUnknown: Int,
    @SerialName("total_swiped") val totalSwiped: Int,
    @SerialName("started_at") val startedAt: String,
    @SerialName("ended_at") val endedAt: String? = null
)

@Serializable
data class SupabaseSessionRecordDto(
    val id: String,
    @SerialName("session_id") val sessionId: String,
    @SerialName("flashcard_id") val flashcardId: String,
    val result: String,
    @SerialName("responded_at") val respondedAt: String
)

@Serializable
data class SupabaseStudySourceDto(
    val id: String,
    @SerialName("deck_id") val deckId: String,
    @SerialName("source_type") val sourceType: String,
    @SerialName("source_text") val sourceText: String? = null,
    @SerialName("source_file_id") val sourceFileId: String? = null,
    @SerialName("source_filename") val sourceFilename: String? = null,
    @SerialName("created_at") val createdAt: String
)

@Serializable
data class SupabaseQuizQuestionDto(
    val id: String,
    @SerialName("deck_id") val deckId: String,
    val question: String,
    val options: List<String>,
    @SerialName("correct_index") val correctIndex: Int,
    @SerialName("order_index") val orderIndex: Int
)

fun Deck.toSupabaseDto() = SupabaseDeckDto(
    id = id,
    userId = userId,
    title = title,
    sourceFilename = sourceFilename,
    totalCards = totalCards,
    createdAt = createdAt.toIsoTimestamp()
)

fun SupabaseDeckDto.toDomain() = Deck(
    id = id,
    userId = userId,
    title = title,
    sourceFilename = sourceFilename,
    totalCards = totalCards,
    createdAt = createdAt.toKotlinInstant()
)

fun Flashcard.toSupabaseDto() = SupabaseFlashcardDto(
    id = id,
    deckId = deckId,
    front = front,
    back = back,
    orderIndex = orderIndex
)

fun SupabaseFlashcardDto.toDomain() = Flashcard(
    id = id,
    deckId = deckId,
    front = front,
    back = back,
    orderIndex = orderIndex
)

fun StudySession.toSupabaseDto() = SupabaseStudySessionDto(
    id = id,
    userId = userId,
    deckId = deckId,
    cardsKnown = cardsKnown,
    cardsUnknown = cardsUnknown,
    totalSwiped = totalSwiped,
    startedAt = startedAt.toIsoTimestamp(),
    endedAt = endedAt?.toIsoTimestamp()
)

fun SupabaseStudySessionDto.toDomain() = StudySession(
    id = id,
    userId = userId,
    deckId = deckId,
    cardsKnown = cardsKnown,
    cardsUnknown = cardsUnknown,
    totalSwiped = totalSwiped,
    startedAt = startedAt.toKotlinInstant(),
    endedAt = endedAt?.let { it.toKotlinInstant() }
)

fun SessionRecord.toSupabaseDto() = SupabaseSessionRecordDto(
    id = id,
    sessionId = sessionId,
    flashcardId = flashcardId,
    result = result.dbValue,
    respondedAt = respondedAt.toIsoTimestamp()
)

fun SupabaseSessionRecordDto.toDomain() = SessionRecord(
    id = id,
    sessionId = sessionId,
    flashcardId = flashcardId,
    result = FlashcardResult.fromDbValue(result),
    respondedAt = respondedAt.toKotlinInstant()
)

fun StudySource.toSupabaseDto() = SupabaseStudySourceDto(
    id = id,
    deckId = deckId,
    sourceType = sourceType.dbValue,
    sourceText = sourceText,
    sourceFileId = sourceFileId,
    sourceFilename = sourceFilename,
    createdAt = createdAt.toIsoTimestamp()
)

fun SupabaseStudySourceDto.toDomain() = StudySource(
    id = id,
    deckId = deckId,
    sourceType = StudySourceType.fromDbValue(sourceType),
    sourceText = sourceText,
    sourceFileId = sourceFileId,
    sourceFilename = sourceFilename,
    createdAt = createdAt.toKotlinInstant()
)

fun QuizQuestion.toSupabaseDto() = SupabaseQuizQuestionDto(
    id = id,
    deckId = deckId,
    question = question,
    options = options,
    correctIndex = correctIndex,
    orderIndex = orderIndex
)

fun SupabaseQuizQuestionDto.toDomain() = QuizQuestion(
    id = id,
    deckId = deckId,
    question = question,
    options = options,
    correctIndex = correctIndex,
    orderIndex = orderIndex
)

private fun Instant.toIsoTimestamp(): String {
    return KxInstant.fromEpochMilliseconds(this.toEpochMilliseconds()).toString()
}

private fun String.toKotlinInstant(): Instant {
    return Instant.fromEpochMilliseconds(KxInstant.parse(this).toEpochMilliseconds())
}
