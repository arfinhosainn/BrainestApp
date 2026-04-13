package com.scelio.brainest.presentation.home

import androidx.compose.runtime.Stable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.scelio.brainest.domain.auth.AuthService
import com.scelio.brainest.domain.util.Result
import com.scelio.brainest.flashcards.domain.FlashcardProgress
import com.scelio.brainest.flashcards.domain.FlashcardsRepository
import com.scelio.brainest.presentation.home.components.StudyDayUi
import com.scelio.brainest.presentation.home.components.StudyDayStatus
import com.scelio.brainest.quiz.domain.QuizRepository
import kotlin.time.Clock
import kotlin.time.Instant
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

@Stable
data class HomeState(
    val isLoading: Boolean = false,
    val userName: String = "Student",
    val completedDecks: Int = 0,
    val completedQuizzes: Int = 0,
    val streakDays: Int = 0,
    val completedDaysThisWeek: Int = 0,
    val studyDays: List<StudyDayUi> = defaultStudyDays(),
    val error: String? = null
)

private data class DeckStudySnapshot(
    val deckId: String,
    val totalCards: Int,
    val flashcardsSwiped: Int,
    val flashCompletionDates: Set<LocalDate>,
    val quizCompletionDates: List<LocalDate>,
    val completedQuizCount: Int
)

class HomeViewModel(
    private val authService: AuthService,
    private val flashcardsRepository: FlashcardsRepository,
    private val quizRepository: QuizRepository
) : ViewModel() {

    private val _state = MutableStateFlow(HomeState(isLoading = true))
    val state: StateFlow<HomeState> = _state

    fun loadHome() {
        if (_state.value.isLoading) return
        refreshHome()
    }

    init {
        refreshHome()
    }

    private fun refreshHome() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }

            val userId = authService.currentUserId()
            if (userId == null) {
                _state.update {
                    it.copy(
                        isLoading = false,
                        error = "No authenticated user found.",
                        studyDays = buildStudyDays(emptySet())
                    )
                }
                return@launch
            }

            val decksResult = flashcardsRepository.listDecks(userId)
            if (decksResult is Result.Failure) {
                _state.update {
                    it.copy(
                        isLoading = false,
                        error = "Failed to load decks: ${decksResult.error}",
                        studyDays = buildStudyDays(emptySet())
                    )
                }
                return@launch
            }

            val decks = (decksResult as Result.Success).data

            // Load flashcard and quiz progress in parallel for each deck
            val deckSnapshots = decks.map { deck ->
                viewModelScope.async {
                    val flashcardProgressResult = flashcardsRepository.getFlashcardProgress(deck.id)
                    val quizProgressResult = quizRepository.getQuizProgress(deck.id)

                    val flashcardsSwiped = when (flashcardProgressResult) {
                        is Result.Success -> flashcardProgressResult.data.sumOf { it.swipesCount }
                        is Result.Failure -> 0
                    }

                    val flashCompletionDates = when (flashcardProgressResult) {
                        is Result.Success -> completedFlashDatesForDeck(
                            totalCards = deck.totalCards,
                            progress = flashcardProgressResult.data
                        )
                        is Result.Failure -> emptySet()
                    }

                    val (quizCompletionDates, completedQuizCount) = when (quizProgressResult) {
                        is Result.Success -> Pair(
                            quizProgressResult.data.map { it.completedAt.toLocalDate() },
                            quizProgressResult.data.size
                        )
                        is Result.Failure -> Pair(emptyList(), 0)
                    }

                    DeckStudySnapshot(
                        deckId = deck.id,
                        totalCards = deck.totalCards,
                        flashcardsSwiped = flashcardsSwiped,
                        flashCompletionDates = flashCompletionDates,
                        quizCompletionDates = quizCompletionDates,
                        completedQuizCount = completedQuizCount
                    )
                }
            }.awaitAll()

            val completedDaySet = deckSnapshots
                .flatMap { snapshot ->
                    snapshot.flashCompletionDates.intersect(snapshot.quizCompletionDates.toSet()).toList()
                }
                .toSet()
            val completedDecks = deckSnapshots.count { snapshot ->
                snapshot.totalCards > 0 && snapshot.flashcardsSwiped >= snapshot.totalCards
            }
            val completedQuizzes = deckSnapshots.sumOf { it.completedQuizCount }
            val streakDays = calculateStreak(completedDaySet)

            _state.update {
                it.copy(
                    isLoading = false,
                    completedDecks = completedDecks,
                    completedQuizzes = completedQuizzes,
                    streakDays = streakDays,
                    completedDaysThisWeek = completedDaySet.count { isInCurrentWeek(it) },
                    studyDays = buildStudyDays(completedDaySet),
                    error = null
                )
            }
        }
    }

    private fun completedFlashDatesForDeck(
        totalCards: Int,
        progress: List<FlashcardProgress>
    ): Set<LocalDate> {
        if (totalCards <= 0) return emptySet()
        return progress
            .groupBy { it.updatedAt.toLocalDate() }
            .asSequence()
            .filter { (_, items) -> items.sumOf { it.swipesCount } >= totalCards }
            .map { (date, _) -> date }
            .toSet()
    }
}

private fun buildStudyDays(
    completedDays: Set<LocalDate>,
    now: LocalDate = today()
): List<StudyDayUi> {
    val weekStart = now.minus(now.dayOfWeek.ordinal, DateTimeUnit.DAY)
    return (0..6).map { offset ->
        val date = weekStart.plusDays(offset)
        val status = when {
            date in completedDays -> StudyDayStatus.Completed
            date > now -> StudyDayStatus.Upcoming
            date == now -> StudyDayStatus.Current
            else -> StudyDayStatus.Missed
        }

        StudyDayUi(
            label = date.dayOfWeek.shortLabel(),
            status = status
        )
    }
}

private fun calculateStreak(
    completedDays: Set<LocalDate>,
    now: LocalDate = today()
): Int {
    var streak = 0
    var date = now

    while (date in completedDays) {
        streak += 1
        date = date.minus(1, DateTimeUnit.DAY)
    }

    return streak
}

private fun isInCurrentWeek(
    date: LocalDate,
    now: LocalDate = today()
): Boolean {
    val weekStart = now.minus(now.dayOfWeek.ordinal, DateTimeUnit.DAY)
    val weekEnd = weekStart.plusDays(6)
    return date >= weekStart && date <= weekEnd
}

private fun Instant.toLocalDate(): LocalDate {
    return kotlinx.datetime.Instant.fromEpochMilliseconds(toEpochMilliseconds())
        .toLocalDateTime(TimeZone.currentSystemDefault())
        .date
}

private fun today(): LocalDate {
    return Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
}

private fun LocalDate.plusDays(days: Int): LocalDate = plus(days, DateTimeUnit.DAY)

private fun DayOfWeek.shortLabel(): String {
    return when (this) {
        DayOfWeek.MONDAY -> "Mon"
        DayOfWeek.TUESDAY -> "Tue"
        DayOfWeek.WEDNESDAY -> "Wed"
        DayOfWeek.THURSDAY -> "Thu"
        DayOfWeek.FRIDAY -> "Fri"
        DayOfWeek.SATURDAY -> "Sat"
        DayOfWeek.SUNDAY -> "Sun"
    }
}

internal fun defaultStudyDays(): List<StudyDayUi> = buildStudyDays(emptySet())
