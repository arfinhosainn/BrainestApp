package com.scelio.brainest.presentation.quiz

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.scelio.brainest.domain.util.Result
import com.scelio.brainest.flashcards.domain.FlashcardsRepository
import com.scelio.brainest.quiz.domain.QuizQuestion
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class QuizSessionState(
    val isLoading: Boolean = false,
    val questions: List<QuizQuestion> = emptyList(),
    val currentIndex: Int = 0,
    val selectedAnswers: Map<String, Int> = emptyMap(),
    val error: String? = null
)

class QuizSessionViewModel(
    private val repository: FlashcardsRepository
) : ViewModel() {

    private val _state = MutableStateFlow(QuizSessionState())
    val state: StateFlow<QuizSessionState> = _state

    fun load(deckId: String) {
        if (_state.value.isLoading) return
        _state.update { it.copy(isLoading = true, error = null) }

        viewModelScope.launch {
            when (val result = repository.getQuizQuestions(deckId)) {
                is Result.Success -> {
                    _state.update {
                        it.copy(
                            isLoading = false,
                            questions = result.data,
                            currentIndex = 0
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
        _state.update { state ->
            state.copy(
                selectedAnswers = state.selectedAnswers + (question.id to optionIndex)
            )
        }
    }

    fun goNext() {
        _state.update { state ->
            val nextIndex = (state.currentIndex + 1).coerceAtMost(state.questions.lastIndex)
            state.copy(currentIndex = nextIndex)
        }
    }

    fun goPrevious() {
        _state.update { state ->
            val prevIndex = (state.currentIndex - 1).coerceAtLeast(0)
            state.copy(currentIndex = prevIndex)
        }
    }
}
