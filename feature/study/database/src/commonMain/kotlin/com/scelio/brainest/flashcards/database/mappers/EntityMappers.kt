package com.scelio.brainest.flashcards.database.mappers

import com.scelio.brainest.flashcards.database.StudySetSummaryRow
import com.scelio.brainest.flashcards.database.entities.DeckEntity
import com.scelio.brainest.flashcards.database.entities.QuizQuestionEntity
import com.scelio.brainest.flashcards.database.entities.StudySourceEntity
import com.scelio.brainest.flashcards.domain.Deck
import com.scelio.brainest.flashcards.domain.StudySetSummary
import com.scelio.brainest.flashcards.domain.StudySource
import com.scelio.brainest.flashcards.domain.StudySourceType
import com.scelio.brainest.quiz.domain.QuizQuestion
import kotlin.time.Instant
import kotlinx.serialization.json.Json

private val json = Json {
    ignoreUnknownKeys = true
}

fun Deck.toEntity(isPendingSync: Boolean = false): DeckEntity {
    return DeckEntity(
        id = id,
        userId = userId,
        title = title,
        sourceFilename = sourceFilename,
        totalCards = totalCards,
        createdAt = createdAt.toEpochMilliseconds(),
        isPendingSync = isPendingSync
    )
}

fun DeckEntity.toDomain(): Deck {
    return Deck(
        id = id,
        userId = userId,
        title = title,
        sourceFilename = sourceFilename,
        totalCards = totalCards,
        createdAt = Instant.fromEpochMilliseconds(createdAt)
    )
}

fun StudySource.toEntity(isPendingSync: Boolean = false): StudySourceEntity {
    return StudySourceEntity(
        id = id,
        deckId = deckId,
        sourceType = sourceType.dbValue,
        sourceText = sourceText,
        sourceFileId = sourceFileId,
        sourceFilename = sourceFilename,
        createdAt = createdAt.toEpochMilliseconds(),
        smartNotes = smartNotes,
        isPendingSync = isPendingSync
    )
}

fun StudySourceEntity.toDomain(): StudySource {
    return StudySource(
        id = id,
        deckId = deckId,
        sourceType = StudySourceType.fromDbValue(sourceType),
        sourceText = sourceText,
        sourceFileId = sourceFileId,
        sourceFilename = sourceFilename,
        createdAt = Instant.fromEpochMilliseconds(createdAt),
        smartNotes = smartNotes
    )
}

fun QuizQuestion.toEntity(isPendingSync: Boolean = false): QuizQuestionEntity {
    return QuizQuestionEntity(
        id = id,
        deckId = deckId,
        question = question,
        optionsJson = json.encodeToString(options),
        correctIndex = correctIndex,
        orderIndex = orderIndex,
        isPendingSync = isPendingSync
    )
}

fun QuizQuestionEntity.toDomain(): QuizQuestion {
    return QuizQuestion(
        id = id,
        deckId = deckId,
        question = question,
        options = json.decodeFromString(optionsJson),
        correctIndex = correctIndex,
        orderIndex = orderIndex
    )
}

fun StudySetSummaryRow.toDomain(): StudySetSummary {
    return StudySetSummary(
        id = deck.id,
        title = deck.title,
        createdAt = Instant.fromEpochMilliseconds(deck.createdAt),
        flashcardsCount = deck.totalCards,
        quizCount = quizCount
    )
}
