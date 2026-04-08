package com.scelio.brainest.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.scelio.brainest.domain.auth.AuthService
import com.scelio.brainest.domain.util.Result
import com.scelio.brainest.flashcards.domain.Deck
import com.scelio.brainest.flashcards.domain.FlashcardsRepository
import com.scelio.brainest.presentation.home.components.StudyDayPoints
import com.scelio.brainest.presentation.home.components.StudyDayStatus
import com.scelio.brainest.presentation.home.components.StudyDayUi
import com.scelio.brainest.quiz.domain.QuizRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.minus
import kotlinx.datetime.plus
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock

data class HomeState(
    val isLoading: Boolean = false,
    val userName: String = "Student",
    val decks: Int = 0,
    val quizzes: Int = 0,
    val others: Int = 0,
    val notificationCount: Int = 0,
    val streakDays: Int = 0,
    val days: List<StudyDayUi> = buildPreviewWeek(),
    val error: String? = null
)

class HomeViewModel(
    private val authService: AuthService,
    private val flashcardsRepository: FlashcardsRepository,
    private val quizRepository: QuizRepository
) : ViewModel() {

    private val _state = MutableStateFlow(HomeState())
    val state: StateFlow<HomeState> = _state

    fun loadHome() {
        if (_state.value.isLoading) return

        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }

            val userId = authService.currentUserId()
            if (userId == null) {
                _state.update {
                    it.copy(
                        isLoading = false,
                        error = "No authenticated user found."
                    )
                }
                return@launch
            }

            val decksResult = flashcardsRepository.listDecks(userId)
            val sessionsResult = flashcardsRepository.listStudySessions(userId)

            val decks = (decksResult as? Result.Success)?.data.orEmpty()
            val sessions = (sessionsResult as? Result.Success)?.data.orEmpty()

            val quizProgress = decks.map { deck ->
                async {
                    deck.id to when (val result = quizRepository.getQuizProgress(deck.id)) {
                        is Result.Success -> result.data
                        is Result.Failure -> emptyList()
                    }
                }
            }.awaitAll().toMap()
            val flashcardProgress = decks.map { deck ->
                async {
                    deck.id to when (val result = flashcardsRepository.getFlashcardProgress(deck.id)) {
                        is Result.Success -> result.data
                        is Result.Failure -> emptyList()
                    }
                }
            }.awaitAll().toMap()

            val completedDates = buildCompletedDates(
                decks = decks,
                completedFlashcardSessions = sessions,
                quizProgressByDeckId = quizProgress
            )
            val completedDecks = countCompletedFlashcardDecks(
                decks = decks,
                flashcardProgressByDeckId = flashcardProgress
            )
            _state.update {
                it.copy(
                    isLoading = false,
                    decks = completedDecks,
                    quizzes = quizProgress.values.sumOf { progressList -> progressList.size },
                    streakDays = calculateStreak(completedDates),
                    days = buildWeekDays(completedDates),
                    error = when {
                        decksResult is Result.Failure -> "Failed to load decks: ${decksResult.error}"
                        sessionsResult is Result.Failure -> "Failed to load study sessions: ${sessionsResult.error}"
                        else -> null
                    }
                )
            }
        }
    }

    private fun buildCompletedDates(
        decks: List<Deck>,
        completedFlashcardSessions: List<com.scelio.brainest.flashcards.domain.StudySession>,
        quizProgressByDeckId: Map<String, List<com.scelio.brainest.flashcards.domain.QuizProgress>>
    ): Set<LocalDate> {
        val timeZone = TimeZone.currentSystemDefault()
        val deckById = decks.associateBy { it.id }

        val flashcardDates = completedFlashcardSessions.mapNotNull { session ->
            val deck = deckById[session.deckId] ?: return@mapNotNull null
            val endedAt = session.endedAt ?: return@mapNotNull null
            if (deck.totalCards <= 0 || session.totalSwiped < deck.totalCards) {
                return@mapNotNull null
            }

            endedAt.toLocalDateTime(timeZone).date
        }.toSet()

        val quizDates = quizProgressByDeckId.values
            .flatten()
            .mapNotNull { progress ->
                if (progress.totalQuestions <= 0 || progress.answeredQuestions < progress.totalQuestions) {
                    return@mapNotNull null
                }

                progress.completedAt.toLocalDateTime(timeZone).date
            }
            .toSet()

        return flashcardDates intersect quizDates
    }

    private fun countCompletedFlashcardDecks(
        decks: List<Deck>,
        flashcardProgressByDeckId: Map<String, List<com.scelio.brainest.flashcards.domain.FlashcardProgress>>
    ): Int {
        return decks.count { deck ->
            if (deck.totalCards <= 0) {
                return@count false
            }

            val progress = flashcardProgressByDeckId[deck.id].orEmpty()
            progress
                .map { it.cardId }
                .distinct()
                .size >= deck.totalCards
        }
    }

    private fun calculateStreak(completedDates: Set<LocalDate>): Int {
        var currentDate = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
        var streak = 0

        while (currentDate in completedDates) {
            streak += 1
            currentDate = currentDate.minus(1, DateTimeUnit.DAY)
        }

        return streak
    }

    private fun buildWeekDays(completedDates: Set<LocalDate>): List<StudyDayUi> {
        val today = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
        val weekStart = today.minus(dayOffsetFromMonday(today.dayOfWeek), DateTimeUnit.DAY)

        return (0..6).map { offset ->
            val date = weekStart.plus(offset, DateTimeUnit.DAY)
            val status = when {
                date in completedDates -> StudyDayStatus.Completed
                date == today -> StudyDayStatus.Current
                else -> StudyDayStatus.Upcoming
            }

            StudyDayUi(
                label = date.dayOfWeek.shortLabel(),
                status = status,
                points = if (status == StudyDayStatus.Completed) null else defaultPointsForDay(offset)
            )
        }
    }
}

private fun DayOfWeek.shortLabel(): String = when (this) {
    DayOfWeek.MONDAY -> "Mon"
    DayOfWeek.TUESDAY -> "Tue"
    DayOfWeek.WEDNESDAY -> "Wed"
    DayOfWeek.THURSDAY -> "Thu"
    DayOfWeek.FRIDAY -> "Fri"
    DayOfWeek.SATURDAY -> "Sat"
    DayOfWeek.SUNDAY -> "Sun"
}

private fun dayOffsetFromMonday(dayOfWeek: DayOfWeek): Int = when (dayOfWeek) {
    DayOfWeek.MONDAY -> 0
    DayOfWeek.TUESDAY -> 1
    DayOfWeek.WEDNESDAY -> 2
    DayOfWeek.THURSDAY -> 3
    DayOfWeek.FRIDAY -> 4
    DayOfWeek.SATURDAY -> 5
    DayOfWeek.SUNDAY -> 6
}

private fun defaultPointsForDay(offset: Int): StudyDayPoints = when (offset) {
    3, 5 -> StudyDayPoints.Two
    else -> StudyDayPoints.Eight
}

internal fun buildPreviewWeek(): List<StudyDayUi> = listOf(
    StudyDayUi("Mon", StudyDayStatus.Upcoming, StudyDayPoints.Eight),
    StudyDayUi("Tue", StudyDayStatus.Upcoming, StudyDayPoints.Eight),
    StudyDayUi("Wed", StudyDayStatus.Upcoming, StudyDayPoints.Eight),
    StudyDayUi("Thu", StudyDayStatus.Current, StudyDayPoints.Two),
    StudyDayUi("Fri", StudyDayStatus.Upcoming, StudyDayPoints.Eight),
    StudyDayUi("Sat", StudyDayStatus.Upcoming, StudyDayPoints.Two),
    StudyDayUi("Sun", StudyDayStatus.Upcoming, StudyDayPoints.Eight),
)
