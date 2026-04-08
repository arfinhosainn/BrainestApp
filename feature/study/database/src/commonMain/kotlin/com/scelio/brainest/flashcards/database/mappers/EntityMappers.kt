package com.scelio.brainest.flashcards.database.mappers

import com.scelio.brainest.flashcards.database.DeckProgressSummaryRow
import com.scelio.brainest.flashcards.database.StudySetSummaryRow
import com.scelio.brainest.flashcards.database.entities.DeckEntity
import com.scelio.brainest.flashcards.database.entities.FlashcardProgressEntity
import com.scelio.brainest.flashcards.database.entities.QuizQuestionEntity
import com.scelio.brainest.flashcards.database.entities.QuizProgressEntity
import com.scelio.brainest.flashcards.database.entities.StudySessionEntity
import com.scelio.brainest.flashcards.database.entities.StudySourceEntity
import com.scelio.brainest.flashcards.domain.DeckStudyProgressSummary
import com.scelio.brainest.flashcards.domain.Deck
import com.scelio.brainest.flashcards.domain.FlashcardProgress
import com.scelio.brainest.flashcards.domain.FlashcardResult
import com.scelio.brainest.flashcards.domain.QuizProgress
import com.scelio.brainest.flashcards.domain.StudySession
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

fun FlashcardProgress.toEntity(): FlashcardProgressEntity {
    return FlashcardProgressEntity(
        id = id,
        deckId = deckId,
        cardId = cardId,
        swipesCount = swipesCount,
        lastResult = lastResult?.dbValue,
        updatedAt = updatedAt.toEpochMilliseconds()
    )
}

fun FlashcardProgressEntity.toDomain(): FlashcardProgress {
    return FlashcardProgress(
        id = id,
        deckId = deckId,
        cardId = cardId,
        swipesCount = swipesCount,
        lastResult = lastResult?.let(FlashcardResult::fromDbValue),
        updatedAt = Instant.fromEpochMilliseconds(updatedAt)
    )
}

fun QuizProgress.toEntity(isPendingSync: Boolean = false): QuizProgressEntity {
    return QuizProgressEntity(
        id = id,
        deckId = deckId,
        totalQuestions = totalQuestions,
        answeredQuestions = answeredQuestions,
        correctAnswers = correctAnswers,
        completedAt = completedAt.toEpochMilliseconds(),
        isPendingSync = isPendingSync
    )
}

fun QuizProgressEntity.toDomain(): QuizProgress {
    return QuizProgress(
        id = id,
        deckId = deckId,
        totalQuestions = totalQuestions,
        answeredQuestions = answeredQuestions,
        correctAnswers = correctAnswers,
        completedAt = Instant.fromEpochMilliseconds(completedAt)
    )
}

fun StudySession.toEntity(isPendingSync: Boolean = false): StudySessionEntity {
    return StudySessionEntity(
        id = id,
        userId = userId,
        deckId = deckId,
        cardsKnown = cardsKnown,
        cardsUnknown = cardsUnknown,
        totalSwiped = totalSwiped,
        startedAt = startedAt.toEpochMilliseconds(),
        endedAt = endedAt?.toEpochMilliseconds(),
        isPendingSync = isPendingSync
    )
}

fun StudySessionEntity.toDomain(): StudySession {
    return StudySession(
        id = id,
        userId = userId,
        deckId = deckId,
        cardsKnown = cardsKnown,
        cardsUnknown = cardsUnknown,
        totalSwiped = totalSwiped,
        startedAt = Instant.fromEpochMilliseconds(startedAt),
        endedAt = endedAt?.let(Instant::fromEpochMilliseconds)
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

fun DeckProgressSummaryRow.toDomain(): DeckStudyProgressSummary {
    return DeckStudyProgressSummary(
        deckId = deckId,
        flashcardsSwiped = flashcardsSwiped,
        quizzesCompleted = quizzesCompleted
    )
}
