package com.scelio.brainest.presentation.studysets

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.scelio.brainest.domain.util.Result
import com.scelio.brainest.flashcards.domain.Deck
import com.scelio.brainest.flashcards.domain.FlashcardsGenerationError
import com.scelio.brainest.flashcards.domain.FlashcardsGenerationService
import com.scelio.brainest.flashcards.domain.FlashcardsRepository
import com.scelio.brainest.flashcards.domain.SmartNotesGenerationError
import com.scelio.brainest.flashcards.domain.SmartNotesGenerationService
import com.scelio.brainest.flashcards.domain.StudySource
import com.scelio.brainest.flashcards.domain.StudySourceType
import com.scelio.brainest.quiz.domain.QuizGenerationError
import com.scelio.brainest.quiz.domain.QuizGenerationService
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class StudySetDetailState(
    val isLoading: Boolean = false,
    val isGenerating: Boolean = false,
    val generationTarget: GenerationTarget? = null,
    val isSmartNotesLoading: Boolean = false,
    val deck: Deck? = null,
    val source: StudySource? = null,
    val quizCount: Int = 0,
    val smartNotes: String? = null,
    val smartNotesError: String? = null,
    val error: String? = null
)

enum class GenerationTarget {
    Flashcards,
    Quiz
}

sealed interface StudySetDetailEvent {
    data class OpenFlashcards(val deckId: String) : StudySetDetailEvent
    data class OpenQuiz(val deckId: String) : StudySetDetailEvent
}

class StudySetDetailViewModel(
    private val repository: FlashcardsRepository,
    private val flashcardsGenerationService: FlashcardsGenerationService,
    private val quizGenerationService: QuizGenerationService,
    private val smartNotesGenerationService: SmartNotesGenerationService
) : ViewModel() {

    private val _state = MutableStateFlow(StudySetDetailState())
    val state: StateFlow<StudySetDetailState> = _state

    private val _events = MutableSharedFlow<StudySetDetailEvent>(extraBufferCapacity = 1)
    val events = _events.asSharedFlow()

    fun load(deckId: String) {
        if (_state.value.isLoading) return
        val isNewDeck = _state.value.deck?.id != deckId
        _state.update {
            it.copy(
                isLoading = true,
                error = null,
                smartNotes = if (isNewDeck) null else it.smartNotes,
                smartNotesError = if (isNewDeck) null else it.smartNotesError,
                isSmartNotesLoading = if (isNewDeck) false else it.isSmartNotesLoading
            )
        }

        viewModelScope.launch {
            val deckResult = repository.getDeck(deckId)
            val sourceResult = repository.getStudySource(deckId)
            val quizResult = repository.getQuizQuestions(deckId)

            val deck = (deckResult as? Result.Success)?.data
            val source = (sourceResult as? Result.Success)?.data
            val quizCount = (quizResult as? Result.Success)?.data?.size ?: 0

            val error = when {
                deckResult is Result.Failure -> "Failed to load study set: ${deckResult.error}"
                sourceResult is Result.Failure -> "Failed to load study source: ${sourceResult.error}"
                quizResult is Result.Failure -> "Failed to load quiz: ${quizResult.error}"
                else -> null
            }

            _state.update {
                it.copy(
                    isLoading = false,
                    deck = deck,
                    source = source,
                    quizCount = quizCount,
                    smartNotes = source?.smartNotes ?: it.smartNotes,
                    smartNotesError = if (!source?.smartNotes.isNullOrBlank()) null else it.smartNotesError,
                    error = error
                )
            }

            maybeGenerateSmartNotes(source)
        }
    }

    fun generateFlashcards(count: Int) {
        val deck = _state.value.deck ?: return
        val source = _state.value.source
        if (source == null) {
            _state.update { it.copy(error = "Missing study source.") }
            return
        }
        if (_state.value.isGenerating) return
        if (deck.totalCards > 0) {
            _events.tryEmit(StudySetDetailEvent.OpenFlashcards(deck.id))
            return
        }

        _state.update {
            it.copy(
                isGenerating = true,
                generationTarget = GenerationTarget.Flashcards,
                error = null
            )
        }

        viewModelScope.launch {
            val generation = when (source.sourceType) {
                StudySourceType.DOCUMENT -> {
                    val text = source.sourceText.orEmpty().trim()
                    if (text.isNotBlank()) {
                        flashcardsGenerationService.generateFlashcards(text, count)
                    } else {
                        val fileId = source.sourceFileId
                        if (fileId.isNullOrBlank()) {
                            Result.Failure(
                                FlashcardsGenerationError.Empty("Missing document text.")
                            )
                        } else {
                            flashcardsGenerationService.generateFlashcardsFromFile(fileId, count)
                        }
                    }
                }
                StudySourceType.AUDIO -> {
                    val text = source.sourceText.orEmpty().trim()
                    if (text.isBlank()) {
                        Result.Failure(
                            FlashcardsGenerationError.Empty("Missing audio transcript.")
                        )
                    } else {
                        flashcardsGenerationService.generateFlashcards(text, count)
                    }
                }
            }

            when (generation) {
                is Result.Success -> {
                    val addResult = repository.addCards(deck.id, generation.data)
                    if (addResult is Result.Success) {
                        _state.update {
                            it.copy(
                                isGenerating = false,
                                generationTarget = null
                            )
                        }
                        load(deck.id)
                        _events.tryEmit(StudySetDetailEvent.OpenFlashcards(deck.id))
                    } else {
                        _state.update {
                            it.copy(
                                isGenerating = false,
                                generationTarget = null,
                                error = "Failed to save flashcards: ${(addResult as Result.Failure).error}"
                            )
                        }
                    }
                }
                is Result.Failure -> {
                    _state.update {
                        it.copy(
                            isGenerating = false,
                            generationTarget = null,
                            error = flashcardsErrorMessage(generation.error)
                        )
                    }
                }
            }
        }
    }

    fun generateQuiz(count: Int, multipleChoice: Boolean) {
        val deck = _state.value.deck ?: return
        val source = _state.value.source
        if (source == null) {
            _state.update { it.copy(error = "Missing study source.") }
            return
        }
        if (_state.value.isGenerating) return
        if (_state.value.quizCount > 0) {
            _events.tryEmit(StudySetDetailEvent.OpenQuiz(deck.id))
            return
        }

        _state.update {
            it.copy(
                isGenerating = true,
                generationTarget = GenerationTarget.Quiz,
                error = null
            )
        }

        viewModelScope.launch {
            val generation = when (source.sourceType) {
                StudySourceType.DOCUMENT -> {
                    val text = source.sourceText.orEmpty().trim()
                    if (text.isNotBlank()) {
                        quizGenerationService.generateQuizFromText(text, count, multipleChoice)
                    } else {
                        val fileId = source.sourceFileId
                        if (fileId.isNullOrBlank()) {
                            Result.Failure(
                                QuizGenerationError.Empty("Missing document text.")
                            )
                        } else {
                            quizGenerationService.generateQuizFromFile(fileId, count, multipleChoice)
                        }
                    }
                }
                StudySourceType.AUDIO -> {
                    val text = source.sourceText.orEmpty().trim()
                    if (text.isBlank()) {
                        Result.Failure(
                            QuizGenerationError.Empty("Missing audio transcript.")
                        )
                    } else {
                        quizGenerationService.generateQuizFromText(text, count, multipleChoice)
                    }
                }
            }

            when (generation) {
                is Result.Success -> {
                    val addResult = repository.addQuizQuestions(deck.id, generation.data)
                    if (addResult is Result.Success) {
                        _state.update {
                            it.copy(
                                isGenerating = false,
                                generationTarget = null
                            )
                        }
                        load(deck.id)
                        _events.tryEmit(StudySetDetailEvent.OpenQuiz(deck.id))
                    } else {
                        _state.update {
                            it.copy(
                                isGenerating = false,
                                generationTarget = null,
                                error = "Failed to save quiz: ${(addResult as Result.Failure).error}"
                            )
                        }
                    }
                }
                is Result.Failure -> {
                    _state.update {
                        it.copy(
                            isGenerating = false,
                            generationTarget = null,
                            error = quizErrorMessage(generation.error)
                        )
                    }
                }
            }
        }
    }

    fun generateSmartNotes() {
        generateSmartNotesForSource(_state.value.source, force = true)
    }

    private fun flashcardsErrorMessage(error: FlashcardsGenerationError): String {
        return when (error) {
            is FlashcardsGenerationError.Parse -> error.message
            is FlashcardsGenerationError.Empty -> error.message
            is FlashcardsGenerationError.Remote -> "Generation failed: ${error.error}"
        }
    }

    private fun quizErrorMessage(error: QuizGenerationError): String {
        return when (error) {
            is QuizGenerationError.Parse -> error.message
            is QuizGenerationError.Empty -> error.message
            is QuizGenerationError.Remote -> "Generation failed: ${error.error}"
        }
    }

    private fun maybeGenerateSmartNotes(source: StudySource?) {
        generateSmartNotesForSource(source, force = false)
    }

    private fun generateSmartNotesForSource(source: StudySource?, force: Boolean) {
        if (source == null) return
        if (_state.value.isSmartNotesLoading) return
        if (!force && !_state.value.smartNotes.isNullOrBlank()) return

        _state.update {
            it.copy(
                isSmartNotesLoading = true,
                smartNotesError = null
            )
        }

        viewModelScope.launch {
            val generation = when (source.sourceType) {
                StudySourceType.DOCUMENT -> {
                    val text = source.sourceText.orEmpty().trim()
                    if (text.isNotBlank()) {
                        smartNotesGenerationService.generateSmartNotes(text)
                    } else {
                        val fileId = source.sourceFileId
                        if (fileId.isNullOrBlank()) {
                            Result.Failure(
                                SmartNotesGenerationError.Invalid("Missing document text.")
                            )
                        } else {
                            smartNotesGenerationService.generateSmartNotesFromFile(fileId)
                        }
                    }
                }
                StudySourceType.AUDIO -> {
                    val text = source.sourceText.orEmpty().trim()
                    if (text.isBlank()) {
                        Result.Failure(
                            SmartNotesGenerationError.Invalid("Missing audio transcript.")
                        )
                    } else {
                        smartNotesGenerationService.generateSmartNotes(text)
                    }
                }
            }

            when (generation) {
                is Result.Success -> {
                    val updatedSource = source.copy(smartNotes = generation.data)
                    val saveResult = repository.saveStudySource(updatedSource)
                    _state.update {
                        it.copy(
                            isSmartNotesLoading = false,
                            source = updatedSource,
                            smartNotes = generation.data,
                            smartNotesError = null,
                            error = if (saveResult is Result.Failure) {
                                "Failed to save smart notes: ${saveResult.error}"
                            } else {
                                it.error
                            }
                        )
                    }
                }
                is Result.Failure -> {
                    _state.update {
                        it.copy(
                            isSmartNotesLoading = false,
                            smartNotesError = smartNotesErrorMessage(generation.error)
                        )
                    }
                }
            }
        }
    }

    private fun smartNotesErrorMessage(error: SmartNotesGenerationError): String {
        return when (error) {
            is SmartNotesGenerationError.Empty -> error.message
            is SmartNotesGenerationError.Invalid -> error.message
            is SmartNotesGenerationError.Remote -> "Smart notes failed: ${error.error}"
        }
    }
}
