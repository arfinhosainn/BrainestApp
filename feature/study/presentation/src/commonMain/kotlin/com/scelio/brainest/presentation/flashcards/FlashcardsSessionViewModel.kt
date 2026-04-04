package com.scelio.brainest.presentation.flashcards

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.scelio.brainest.domain.auth.AuthService
import com.scelio.brainest.domain.util.Result
import com.scelio.brainest.flashcards.domain.Flashcard
import com.scelio.brainest.flashcards.domain.FlashcardsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class FlashcardsSessionState(
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val cards: List<Flashcard> = emptyList(),
    val error: String? = null,
    val sessionId: String? = null
)

class FlashcardsSessionViewModel(
    private val repository: FlashcardsRepository,
    private val authService: AuthService
) : ViewModel() {

    private var tracker = FlashcardsSessionTracker()
    private var activeDeckId: String? = null
    private var hasFinished = false

    private val _state = MutableStateFlow(FlashcardsSessionState())
    val state: StateFlow<FlashcardsSessionState> = _state

    fun loadDeck(deckId: String) {
        if (activeDeckId == deckId && _state.value.sessionId != null) {
            return
        }

        activeDeckId = deckId
        hasFinished = false
        tracker = FlashcardsSessionTracker()
        _state.update { it.copy(isLoading = true, error = null, cards = emptyList(), sessionId = null) }

        viewModelScope.launch {
            val userId = authService.currentUserId()
            if (userId == null) {
                _state.update { it.copy(isLoading = false, error = "No authenticated user found.") }
                return@launch
            }

            when (val cardsResult = repository.getDeckCards(deckId)) {
                is Result.Success -> {
                    when (val sessionResult = repository.startSession(userId, deckId)) {
                        is Result.Success -> {
                            _state.update {
                                it.copy(
                                    isLoading = false,
                                    cards = cardsResult.data,
                                    sessionId = sessionResult.data.id
                                )
                            }
                        }

                        is Result.Failure -> {
                            _state.update {
                                it.copy(
                                    isLoading = false,
                                    error = "Failed to start session: ${sessionResult.error}"
                                )
                            }
                        }
                    }
                }

                is Result.Failure -> {
                    _state.update {
                        it.copy(
                            isLoading = false,
                            error = "Failed to load cards: ${cardsResult.error}"
                        )
                    }
                }
            }
        }
    }

    fun onCardKnown(card: Flashcard) {
        tracker.markKnown(card.id)
    }

    fun onCardUnknown(card: Flashcard) {
        tracker.markUnknown(card.id)
    }

    fun finishSession() {
        val sessionId = _state.value.sessionId ?: return
        if (hasFinished) return
        hasFinished = true

        viewModelScope.launch {
            _state.update { it.copy(isSaving = true) }
            val summary = tracker.buildSummary()
            val records = tracker.snapshotRecords()

            when (val result = repository.finishSession(sessionId, summary, records)) {
                is Result.Success -> {
                    _state.update { it.copy(isSaving = false) }
                }

                is Result.Failure -> {
                    _state.update {
                        it.copy(
                            isSaving = false,
                            error = "Failed to save session: ${result.error}"
                        )
                    }
                }
            }
        }
    }
}
