package com.scelio.brainest.presentation.home

import androidx.compose.runtime.Stable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.scelio.brainest.domain.auth.AuthService
import com.scelio.brainest.domain.logging.BrainestLogger
import com.scelio.brainest.domain.util.Result
import com.scelio.brainest.flashcards.domain.FlashcardProgress
import com.scelio.brainest.flashcards.domain.FlashcardsRepository
import com.scelio.brainest.home.domain.WeeklyPointsRepository
import com.scelio.brainest.home.domain.WeeklyPointsSchedule
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
import kotlin.random.Random

@Stable
data class HomeState(
    val isLoading: Boolean = false,
    val userName: String = "Student",
    val completedDecks: Int = 0,
    val completedQuizzes: Int = 0,
    val streakDays: Int = 0,
    val completedDaysThisWeek: Int = 0,
    val earnedPoints: Int = 0,
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
    private val quizRepository: QuizRepository,
    private val weeklyPointsRepository: WeeklyPointsRepository,
    private val logger: BrainestLogger
) : ViewModel() {

    private var cachedWeeklySchedule: WeeklyPointsSchedule? = null

    // Start with isLoading = false so UI shows immediately without flash
    private val _state = MutableStateFlow(HomeState(isLoading = false))
    val state: StateFlow<HomeState> = _state

    fun loadHome() {
        refreshHome()
    }

    init {
        refreshHome()
    }

    private fun refreshHome() {
        viewModelScope.launch {
            val userId = authService.currentUserId()
            if (userId == null) {
                _state.update {
                    it.copy(
                        error = "No authenticated user found.",
                        studyDays = buildStudyDays(emptySet(), cachedWeeklySchedule)
                    )
                }
                return@launch
            }

            val weekStart = today().minus(today().dayOfWeek.ordinal, DateTimeUnit.DAY)
            
            // Load schedule only once per session (or if week changed)
            // Load in parallel with decks to avoid blocking
            val scheduleDeferred = if (cachedWeeklySchedule == null || cachedWeeklySchedule?.weekStartDate != weekStart) {
                viewModelScope.async { loadOrCreateWeeklySchedule(userId, weekStart) }
            } else null
            
            val decksResult = flashcardsRepository.listDecks(userId)
            if (decksResult is Result.Failure) {
                _state.update {
                    it.copy(
                        error = "Failed to load decks: ${decksResult.error}",
                        studyDays = buildStudyDays(emptySet(), cachedWeeklySchedule)
                    )
                }
                return@launch
            }

            val decks = (decksResult as Result.Success).data
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

            // Wait for schedule to be ready (fast if cached/local)
            scheduleDeferred?.await()
            val schedule = cachedWeeklySchedule

            val earnedPoints = completedDaySet.sumOf { date ->
                schedule?.getPointsForDay(date.dayOfWeek.ordinal + 1) ?: 0
            }

            // Single atomic state update - no loading flicker
            _state.update {
                it.copy(
                    completedDecks = completedDecks,
                    completedQuizzes = completedQuizzes,
                    streakDays = streakDays,
                    earnedPoints = earnedPoints,
                    completedDaysThisWeek = completedDaySet.count { isInCurrentWeek(it) },
                    studyDays = buildStudyDays(completedDaySet, schedule),
                    error = null
                )
            }
        }
    }

    private suspend fun loadOrCreateWeeklySchedule(
        userId: String,
        weekStart: LocalDate
    ): WeeklyPointsSchedule? {
        if (cachedWeeklySchedule != null &&
            cachedWeeklySchedule?.userId == userId &&
            cachedWeeklySchedule?.weekStartDate == weekStart) {
            return cachedWeeklySchedule
        }

        val result = weeklyPointsRepository.getWeeklySchedule(userId, weekStart)

        return when (result) {
            is Result.Success -> {
                result.data?.let { schedule ->
                    cachedWeeklySchedule = schedule
                    schedule
                } ?: run {
                    val newSchedule = generateWeeklySchedule(userId, weekStart)
                    weeklyPointsRepository.saveWeeklySchedule(newSchedule)
                    cachedWeeklySchedule = newSchedule
                    newSchedule
                }
            }
            else -> {
                logger.error("[HomeViewModel] Failed to load weekly schedule")
                generateWeeklySchedule(userId, weekStart)
            }
        }
    }

    private fun generateWeeklySchedule(
        userId: String,
        weekStart: LocalDate
    ): WeeklyPointsSchedule {
        fun randomPoints() = if (Random.nextBoolean()) 8 else 2

        return WeeklyPointsSchedule(
            userId = userId,
            weekStartDate = weekStart,
            mondayPoints = randomPoints(),
            tuesdayPoints = randomPoints(),
            wednesdayPoints = randomPoints(),
            thursdayPoints = randomPoints(),
            fridayPoints = randomPoints(),
            saturdayPoints = randomPoints(),
            sundayPoints = randomPoints()
        )
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
    schedule: WeeklyPointsSchedule?,
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

        val points = if (status == StudyDayStatus.Current || status == StudyDayStatus.Upcoming) {
            schedule?.getPointsForDay(date.dayOfWeek.ordinal + 1)
        } else {
            null
        }

        StudyDayUi(
            label = date.dayOfWeek.shortLabel(),
            status = status,
            points = points
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

internal fun defaultStudyDays(): List<StudyDayUi> = buildStudyDays(emptySet(), null)
