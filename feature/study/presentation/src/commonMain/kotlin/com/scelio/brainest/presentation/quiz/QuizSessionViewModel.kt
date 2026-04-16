package com.scelio.brainest.presentation.quiz

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.scelio.brainest.domain.util.Result
import com.scelio.brainest.quiz.domain.QuizQuestion
import com.scelio.brainest.quiz.domain.QuizRepository
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
    val error: String? = null
)

class QuizSessionViewModel(
    private val repository: QuizRepository
) : ViewModel() {

    private var activeDeckId: String? = null
    private var hasRecordedCompletion = false

    private val _state = MutableStateFlow(QuizSessionState())
    val state: StateFlow<QuizSessionState> = _state

    fun load(deckId: String) {
        if (_state.value.isLoading) return
        activeDeckId = deckId
        hasRecordedCompletion = false
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
                            correctAnswers = 0
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

    private fun recordQuizCompletionIfNeeded() {
        if (hasRecordedCompletion) return

        val deckId = activeDeckId ?: return
        val state = _state.value
        val totalQuestions = state.questions.size
        if (totalQuestions == 0) return

        hasRecordedCompletion = true
        val answeredQuestions = state.selectedAnswers.size
        val correctAnswers = state.questions.count { question ->
            state.selectedAnswers[question.id] == question.correctIndex
        }

        viewModelScope.launch {
            when (val result = repository.recordQuizCompletion(
                deckId = deckId,
                totalQuestions = totalQuestions,
                answeredQuestions = answeredQuestions,
                correctAnswers = correctAnswers
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
        _state.update {
            it.copy(
                currentIndex = 0,
                selectedAnswers = emptyMap(),
                isCompleted = false,
                answeredQuestions = 0,
                correctAnswers = 0,
                error = null
            )
        }
    }

    private fun completeQuiz() {
        val state = _state.value
        val answeredQuestions = state.selectedAnswers.size
        val correctAnswers = state.questions.count { question ->
            state.selectedAnswers[question.id] == question.correctIndex
        }

        _state.update {
            it.copy(
                isCompleted = true,
                answeredQuestions = answeredQuestions,
                correctAnswers = correctAnswers
            )
        }
        recordQuizCompletionIfNeeded()
    }
}
