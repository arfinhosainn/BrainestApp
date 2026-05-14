package com.scelio.brainest.presentation.quiz

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.scelio.brainest.domain.auth.AuthService
import com.scelio.brainest.domain.util.Result
import com.scelio.brainest.flashcards.domain.FlashcardsRepository
import com.scelio.brainest.quiz.domain.QuizQuestion
import com.scelio.brainest.quiz.domain.QuizRewardSeed
import com.scelio.brainest.quiz.domain.QuizRepository
import com.scelio.brainest.quiz.domain.QuizCompletionReward
import com.scelio.brainest.quiz.domain.computeEffectiveQuizRewards
import kotlin.time.Clock
import kotlin.time.Instant
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class QuizSessionState(
    val isLoading: Boolean = false,
    val questions: List<QuizQuestion> = emptyList(),
    val currentIndex: Int = 0,
    val selectedAnswers: Map<String, Int> = emptyMap(),
    val isCompleted: Boolean = false,
    val answeredQuestions: Int = 0,
    val correctAnswers: Int = 0,
    val earnedExp: Int = 0,
    val earnedDiamonds: Int = 0,
    val error: String? = null
)

class QuizSessionViewModel(
    private val repository: QuizRepository,
    private val authService: AuthService,
    private val flashcardsRepository: FlashcardsRepository
) : ViewModel() {

    private var activeDeckId: String? = null
    private var hasRecordedCompletion = false
    private var isFinalizingCompletion = false

    private val _state = MutableStateFlow(QuizSessionState())
    val state: StateFlow<QuizSessionState> = _state

    fun load(deckId: String) {
        if (_state.value.isLoading) return
        activeDeckId = deckId
        hasRecordedCompletion = false
        isFinalizingCompletion = false
        _state.update { it.copy(isLoading = true, error = null) }

        viewModelScope.launch {
            when (val result = repository.getQuizQuestions(deckId)) {
                is Result.Success -> {
                    _state.update {
                        it.copy(
                            isLoading = false,
                            questions = result.data,
                            currentIndex = 0,
                            selectedAnswers = emptyMap(),
                            isCompleted = false,
                            answeredQuestions = 0,
                            correctAnswers = 0,
                            earnedExp = 0,
                            earnedDiamonds = 0
                        )
                    }
                }
                is Result.Failure -> {
                    _state.update {
                        it.copy(
                            isLoading = false,
                            error = "Failed to load quiz: ${result.error}"
                        )
                    }
                }
            }
        }
    }

    fun selectOption(optionIndex: Int) {
        val question = _state.value.questions.getOrNull(_state.value.currentIndex) ?: return
        if (_state.value.selectedAnswers.containsKey(question.id)) return
        _state.update { state ->
            state.copy(
                selectedAnswers = state.selectedAnswers + (question.id to optionIndex)
            )
        }
    }

    fun goNext() {
        val state = _state.value
        if (state.questions.isEmpty()) return

        val isLastQuestion = state.currentIndex >= state.questions.lastIndex
        if (isLastQuestion) {
            completeQuiz()
            return
        }

        _state.update { current ->
            current.copy(currentIndex = current.currentIndex + 1)
        }
    }

    fun goPrevious() {
        _state.update { state ->
            val prevIndex = (state.currentIndex - 1).coerceAtLeast(0)
            state.copy(currentIndex = prevIndex)
        }
    }

    private fun recordQuizCompletionIfNeeded(
        totalQuestions: Int,
        answeredQuestions: Int,
        correctAnswers: Int,
        completedAt: Instant
    ) {
        if (hasRecordedCompletion) return

        val deckId = activeDeckId ?: return
        if (totalQuestions <= 0) return

        hasRecordedCompletion = true

        viewModelScope.launch {
            when (val result = repository.recordQuizCompletion(
                deckId = deckId,
                totalQuestions = totalQuestions,
                answeredQuestions = answeredQuestions,
                correctAnswers = correctAnswers,
                completedAt = completedAt
            )) {
                is Result.Success -> Unit
                is Result.Failure -> {
                    hasRecordedCompletion = false
                    _state.update {
                        it.copy(error = "Failed to save quiz progress: ${result.error}")
                    }
                }
            }
        }
    }

    fun restartQuiz() {
        hasRecordedCompletion = false
        isFinalizingCompletion = false
        _state.update {
            it.copy(
                currentIndex = 0,
                selectedAnswers = emptyMap(),
                isCompleted = false,
                answeredQuestions = 0,
                correctAnswers = 0,
                earnedExp = 0,
                earnedDiamonds = 0,
                error = null
            )
        }
    }

    private fun completeQuiz() {
        if (isFinalizingCompletion || _state.value.isCompleted) return

        val state = _state.value
        val deckId = activeDeckId ?: return
        val totalQuestions = state.questions.size
        val answeredQuestions = state.selectedAnswers.size
        val correctAnswers = state.questions.count { question ->
            state.selectedAnswers[question.id] == question.correctIndex
        }
        val completedAt = Clock.System.now()
        isFinalizingCompletion = true

        viewModelScope.launch {
            try {
                val rewards = calculateCurrentCompletionReward(
                    deckId = deckId,
                    totalQuestions = totalQuestions,
                    answeredQuestions = answeredQuestions,
                    correctAnswers = correctAnswers,
                    completedAt = completedAt
                )

                _state.update {
                    it.copy(
                        isCompleted = true,
                        answeredQuestions = answeredQuestions,
                        correctAnswers = correctAnswers,
                        earnedExp = rewards.earnedExp,
                        earnedDiamonds = rewards.earnedDiamonds
                    )
                }

                recordQuizCompletionIfNeeded(
                    totalQuestions = totalQuestions,
                    answeredQuestions = answeredQuestions,
                    correctAnswers = correctAnswers,
                    completedAt = completedAt
                )
            } finally {
                isFinalizingCompletion = false
            }
        }
    }

    private suspend fun calculateCurrentCompletionReward(
        deckId: String,
        totalQuestions: Int,
        answeredQuestions: Int,
        correctAnswers: Int,
        completedAt: Instant
    ): QuizCompletionReward {
        val userId = authService.currentUserId() ?: return QuizCompletionReward(0, 0)
        val decksResult = flashcardsRepository.listDecks(userId)
        val decks = (decksResult as? Result.Success)?.data ?: emptyList()

        val existingCompletions = buildList {
            decks.forEach { deck ->
                val progressResult = repository.getQuizProgress(deck.id)
                val progress = (progressResult as? Result.Success)?.data.orEmpty()
                progress.forEach { completion ->
                    add(
                        QuizRewardSeed(
                            deckId = completion.deckId,
                            completedAtEpochMillis = completion.completedAt.toEpochMilliseconds(),
                            totalQuestions = completion.totalQuestions,
                            answeredQuestions = completion.answeredQuestions,
                            correctAnswers = completion.correctAnswers
                        )
                    )
                }
            }
        }

        val currentCompletion = QuizRewardSeed(
            deckId = deckId,
            completedAtEpochMillis = completedAt.toEpochMilliseconds(),
            totalQuestions = totalQuestions,
            answeredQuestions = answeredQuestions,
            correctAnswers = correctAnswers
        )

        return computeEffectiveQuizRewards(existingCompletions + currentCompletion)
            .lastOrNull()
            ?: QuizCompletionReward(0, 0)
    }
}
