package com.scelio.brainest.presentation.home

import androidx.compose.runtime.Stable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import brainest.feature.home.presentation.generated.resources.Res
import brainest.feature.home.presentation.generated.resources.home_day_fri
import brainest.feature.home.presentation.generated.resources.home_day_mon
import brainest.feature.home.presentation.generated.resources.home_day_sat
import brainest.feature.home.presentation.generated.resources.home_day_sun
import brainest.feature.home.presentation.generated.resources.home_day_thu
import brainest.feature.home.presentation.generated.resources.home_day_tue
import brainest.feature.home.presentation.generated.resources.home_day_wed
import brainest.feature.home.presentation.generated.resources.home_error_failed_to_load_decks
import brainest.feature.home.presentation.generated.resources.home_error_no_authenticated_user
import brainest.feature.home.presentation.generated.resources.home_student
import com.scelio.brainest.domain.auth.AuthService
import com.scelio.brainest.domain.logging.BrainestLogger
import com.scelio.brainest.domain.util.Result
import com.scelio.brainest.flashcards.domain.FlashcardProgress
import com.scelio.brainest.flashcards.domain.FlashcardsRepository
import com.scelio.brainest.home.domain.AchievementsRepository
import com.scelio.brainest.home.domain.UserAchievementEventInput
import com.scelio.brainest.home.domain.UserAchievementEventType
import com.scelio.brainest.home.domain.UserAchievements
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
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.getString
import kotlin.random.Random

@Stable
data class HomeState(
    val isLoading: Boolean = false,
    val userName: String = "",
    val completedDecks: Int = 0,
    val completedQuizzes: Int = 0,
    val streakDays: Int = 0,
    val completedDaysThisWeek: Int = 0,
    val earnedPoints: Int = 0,
    val totalPoints: Int = 0,
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
    private val achievementsRepository: AchievementsRepository,
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
                        error = getString(Res.string.home_error_no_authenticated_user),
                        studyDays = buildStudyDays(emptySet(), cachedWeeklySchedule)
                    )
                }
                return@launch
            }

            val accountCreatedDate = authService.getCurrentUser()
                ?.createdAt
                ?.toLocalDateOrNull()
            val userName = authService.getCurrentUser()
                ?.username
                ?.takeIf { it.isNotBlank() }
                ?: getString(Res.string.home_student)
            val weekStart = today().minus(today().dayOfWeek.ordinal, DateTimeUnit.DAY)

            val achievementsDeferred = viewModelScope.async {
                achievementsRepository.getUserAchievements(userId)
            }
            
            // Load schedule only once per session (or if week changed)
            // Load in parallel with decks to avoid blocking
            val scheduleDeferred = if (cachedWeeklySchedule == null || cachedWeeklySchedule?.weekStartDate != weekStart) {
                viewModelScope.async { loadOrCreateWeeklySchedule(userId, weekStart) }
            } else null
            
            val decksResult = flashcardsRepository.listDecks(userId)
            if (decksResult is Result.Failure) {
                _state.update {
                    it.copy(
                        error = getString(Res.string.home_error_failed_to_load_decks, decksResult.error.toString()),
                        studyDays = buildStudyDays(
                            completedDays = emptySet(),
                            schedule = cachedWeeklySchedule,
                            accountCreatedDate = accountCreatedDate
                        )
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
            val currentStreakDays = calculateStreak(completedDaySet)
            val longestStreakDays = calculateLongestStreak(completedDaySet)

            // Wait for schedule to be ready (fast if cached/local)
            scheduleDeferred?.await()
            val schedule = cachedWeeklySchedule

            val earnedPoints = completedDaySet.sumOf { date ->
                schedule?.getPointsForDay(date.dayOfWeek.ordinal + 1) ?: 0
            }
            val achievementsResult = achievementsDeferred.await()
            val previousAchievements = (achievementsResult as? Result.Success)?.data

            val totalStreakDays = maxOf(
                previousAchievements?.longestStreakDays ?: 0,
                longestStreakDays
            )

            val updatedAchievements = UserAchievements(
                userId = userId,
                totalPoints = earnedPoints,
                currentStreakDays = currentStreakDays,
                longestStreakDays = totalStreakDays,
                completedDecksCount = completedDecks,
                completedQuizzesCount = completedQuizzes,
                lastActivityDate = completedDaySet.maxOrNull()
            )

            if (shouldPersistAchievements(previousAchievements, updatedAchievements)) {
                val events = buildAchievementEvents(
                    previous = previousAchievements,
                    current = updatedAchievements,
                    eventDate = today()
                )
                achievementsRepository.upsertUserAchievements(updatedAchievements)
                achievementsRepository.insertAchievementEvents(events)
            }

            val totalPoints = updatedAchievements.totalPoints

            // Single atomic state update - no loading flicker
            _state.update {
                it.copy(
                    userName = userName,
                    completedDecks = completedDecks,
                    completedQuizzes = completedQuizzes,
                    streakDays = totalStreakDays,
                    earnedPoints = earnedPoints,
                    totalPoints = totalPoints,
                    completedDaysThisWeek = completedDaySet.count {
                        isInCurrentWeek(it, accountCreatedDate = accountCreatedDate)
                    },
                    studyDays = buildStudyDays(
                        completedDays = completedDaySet,
                        schedule = schedule,
                        accountCreatedDate = accountCreatedDate
                    ),
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
    accountCreatedDate: LocalDate? = null,
    now: LocalDate = today()
): List<StudyDayUi> {
    val weekStart = now.minus(now.dayOfWeek.ordinal, DateTimeUnit.DAY)
    val effectiveStart = accountCreatedDate?.let { maxOf(it, weekStart) } ?: weekStart
    return (0..6).map { offset ->
        val date = weekStart.plusDays(offset)
        if (date < effectiveStart) {
            return@map null
        }
        val status = when {
            date in completedDays -> StudyDayStatus.Completed
            date > now -> StudyDayStatus.Upcoming
            date == now -> StudyDayStatus.Current
            else -> StudyDayStatus.Missed
        }

        val points = if (
            date >= effectiveStart &&
            (status == StudyDayStatus.Current || status == StudyDayStatus.Upcoming)
        ) {
            schedule?.getPointsForDay(date.dayOfWeek.ordinal + 1)
        } else {
            null
        }

        StudyDayUi(
            label = date.dayOfWeek.shortLabel(),
            status = status,
            points = points
        )
    }.filterNotNull()
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
    accountCreatedDate: LocalDate? = null,
    now: LocalDate = today()
): Boolean {
    val weekStart = now.minus(now.dayOfWeek.ordinal, DateTimeUnit.DAY)
    val effectiveStart = accountCreatedDate?.let { maxOf(it, weekStart) } ?: weekStart
    val weekEnd = weekStart.plusDays(6)
    return date >= effectiveStart && date <= weekEnd
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

private fun String.toLocalDateOrNull(): LocalDate? {
    return try {
        kotlin.time.Instant.parse(this)
            .toLocalDateTime(TimeZone.currentSystemDefault())
            .date
    } catch (_: Exception) {
        null
    }
}

private fun DayOfWeek.shortLabel(): StringResource {
    return when (this) {
        DayOfWeek.MONDAY -> Res.string.home_day_mon
        DayOfWeek.TUESDAY -> Res.string.home_day_tue
        DayOfWeek.WEDNESDAY -> Res.string.home_day_wed
        DayOfWeek.THURSDAY -> Res.string.home_day_thu
        DayOfWeek.FRIDAY -> Res.string.home_day_fri
        DayOfWeek.SATURDAY -> Res.string.home_day_sat
        DayOfWeek.SUNDAY -> Res.string.home_day_sun
    }
}

internal fun defaultStudyDays(now: LocalDate = today()): List<StudyDayUi> {
    val weekStart = now.minus(now.dayOfWeek.ordinal, DateTimeUnit.DAY)
    return (0..6).map { offset ->
        val date = weekStart.plusDays(offset)
        StudyDayUi(
            label = date.dayOfWeek.shortLabel(),
            status = StudyDayStatus.Upcoming,
            points = null
        )
    }
}

private fun shouldPersistAchievements(
    previous: UserAchievements?,
    current: UserAchievements
): Boolean {
    if (previous == null) return true
    return previous != current
}

private fun buildAchievementEvents(
    previous: UserAchievements?,
    current: UserAchievements,
    eventDate: LocalDate
): List<UserAchievementEventInput> {
    if (previous == null) {
        val events = mutableListOf<UserAchievementEventInput>()
        if (current.totalPoints > 0) {
            events += UserAchievementEventInput(
                userId = current.userId,
                eventType = UserAchievementEventType.POINTS_EARNED,
                pointsDelta = current.totalPoints,
                occurredOn = eventDate
            )
        }
        if (current.currentStreakDays > 0) {
            events += UserAchievementEventInput(
                userId = current.userId,
                eventType = UserAchievementEventType.STREAK_UPDATED,
                occurredOn = eventDate
            )
        }
        if (current.completedDecksCount > 0) {
            events += UserAchievementEventInput(
                userId = current.userId,
                eventType = UserAchievementEventType.DECK_COMPLETED,
                occurredOn = eventDate,
                metadata = """{"delta":${current.completedDecksCount}}"""
            )
        }
        if (current.completedQuizzesCount > 0) {
            events += UserAchievementEventInput(
                userId = current.userId,
                eventType = UserAchievementEventType.QUIZ_COMPLETED,
                occurredOn = eventDate,
                metadata = """{"delta":${current.completedQuizzesCount}}"""
            )
        }
        return events
    }

    val events = mutableListOf<UserAchievementEventInput>()

    val pointsDelta = current.totalPoints - previous.totalPoints
    if (pointsDelta > 0) {
        events += UserAchievementEventInput(
            userId = current.userId,
            eventType = UserAchievementEventType.POINTS_EARNED,
            pointsDelta = pointsDelta,
            occurredOn = eventDate
        )
    }

    if (current.currentStreakDays != previous.currentStreakDays ||
        current.longestStreakDays != previous.longestStreakDays) {
        events += UserAchievementEventInput(
            userId = current.userId,
            eventType = UserAchievementEventType.STREAK_UPDATED,
            occurredOn = eventDate,
            metadata = """{"currentStreakDays":${current.currentStreakDays},"longestStreakDays":${current.longestStreakDays}}"""
        )
    }

    val deckDelta = current.completedDecksCount - previous.completedDecksCount
    if (deckDelta > 0) {
        events += UserAchievementEventInput(
            userId = current.userId,
            eventType = UserAchievementEventType.DECK_COMPLETED,
            occurredOn = eventDate,
            metadata = """{"delta":$deckDelta}"""
        )
    }

    val quizDelta = current.completedQuizzesCount - previous.completedQuizzesCount
    if (quizDelta > 0) {
        events += UserAchievementEventInput(
            userId = current.userId,
            eventType = UserAchievementEventType.QUIZ_COMPLETED,
            occurredOn = eventDate,
            metadata = """{"delta":$quizDelta}"""
        )
    }

    return events
}

private fun calculateLongestStreak(
    completedDays: Set<LocalDate>
): Int {
    if (completedDays.isEmpty()) return 0

    val orderedDates = completedDays.sorted()
    var longest = 1
    var current = 1

    for (index in 1 until orderedDates.size) {
        val previous = orderedDates[index - 1]
        val now = orderedDates[index]
        if (now == previous.plus(1, DateTimeUnit.DAY)) {
            current += 1
            if (current > longest) {
                longest = current
            }
        } else {
            current = 1
        }
    }

    return longest
}
