package com.scelio.brainest.presentation.flashcards

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.scelio.brainest.domain.auth.AuthService
import com.scelio.brainest.domain.util.Result
import com.scelio.brainest.flashcards.domain.Flashcard
import com.scelio.brainest.flashcards.domain.FlashcardResult
import com.scelio.brainest.flashcards.domain.FlashcardsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.time.Clock

data class FlashcardsSessionState(
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val cards: List<Flashcard> = emptyList(),
    val currentIndex: Int = 0,
    val knownCount: Int = 0,
    val unknownCount: Int = 0,
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
        _state.update {
            it.copy(
                isLoading = true,
                error = null,
                cards = emptyList(),
                currentIndex = 0,
                knownCount = 0,
                unknownCount = 0,
                sessionId = null
            )
        }

        viewModelScope.launch {
            val userId = authService.currentUserId()
            if (userId == null) {
                _state.update { it.copy(isLoading = false, error = "No authenticated user found.") }
                return@launch
            }

            when (val cardsResult = repository.getDeckCards(deckId)) {
                is Result.Success -> {
                    val progressResult = repository.getFlashcardProgress(deckId)
                    val progress = (progressResult as? Result.Success)?.data.orEmpty()
                    val knownCount = progress.count { it.lastResult == FlashcardResult.KNOWN }
                    val unknownCount = progress.count { it.lastResult == FlashcardResult.UNKNOWN }
                    val currentIndex = progress.size.coerceAtMost(cardsResult.data.size)

                    when (val sessionResult = repository.startSession(userId, deckId)) {
                        is Result.Success -> {
                            _state.update {
                                it.copy(
                                    isLoading = false,
                                    cards = cardsResult.data,
                                    currentIndex = currentIndex,
                                    knownCount = knownCount,
                                    unknownCount = unknownCount,
                                    error = if (progressResult is Result.Failure) {
                                        "Failed to load saved progress: ${progressResult.error}"
                                    } else {
                                        null
                                    },
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
        recordCardSwipe(card, FlashcardResult.KNOWN)
    }

    fun onCardUnknown(card: Flashcard) {
        recordCardSwipe(card, FlashcardResult.UNKNOWN)
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

    private fun recordCardSwipe(card: Flashcard, result: FlashcardResult) {
        val swipedAt = Clock.System.now()
        when (result) {
            FlashcardResult.KNOWN -> tracker.markKnown(card.id, swipedAt)
            FlashcardResult.UNKNOWN -> tracker.markUnknown(card.id, swipedAt)
        }

        viewModelScope.launch {
            when (val saveResult = repository.recordFlashcardSwipe(
                deckId = card.deckId,
                cardId = card.id,
                result = result,
                swipedAt = swipedAt
            )) {
                is Result.Success -> {
                    _state.update { state ->
                        state.copy(
                            currentIndex = (state.currentIndex + 1).coerceAtMost(state.cards.size),
                            knownCount = state.knownCount + if (result == FlashcardResult.KNOWN) 1 else 0,
                            unknownCount = state.unknownCount + if (result == FlashcardResult.UNKNOWN) 1 else 0
                        )
                    }
                }
                is Result.Failure -> {
                    _state.update {
                        it.copy(error = "Failed to save progress: ${saveResult.error}")
                    }
                }
            }
        }
    }
}
