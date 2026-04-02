package com.scelio.brainest.presentation.flashcards

import androidx.compose.foundation.text.input.TextFieldState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.scelio.brainest.domain.auth.AuthService
import com.scelio.brainest.domain.util.Result
import com.scelio.brainest.flashcards.domain.FlashcardsGenerationError
import com.scelio.brainest.flashcards.domain.FlashcardsGenerationService
import com.scelio.brainest.flashcards.domain.FlashcardsRepository
import com.scelio.brainest.flashcards.domain.OpenAiFileService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay
import kotlin.time.Clock

data class FlashcardsGenerateState(
    val titleState: TextFieldState = TextFieldState(),
    val promptState: TextFieldState = TextFieldState(),
    val countState: TextFieldState = TextFieldState(),
    val isGenerating: Boolean = false,
    val message: String? = null,
    val error: String? = null,
    val lastDeckId: String? = null
)

class FlashcardsGenerateViewModel(
    private val generationService: FlashcardsGenerationService,
    private val repository: FlashcardsRepository,
    private val fileService: OpenAiFileService,
    private val authService: AuthService
) : ViewModel() {

    private val _state = MutableStateFlow(FlashcardsGenerateState())
    val state: StateFlow<FlashcardsGenerateState> = _state

    fun generateAndSave() {
        if (_state.value.isGenerating) return

        val prompt = _state.value.promptState.text.toString().trim()
        if (prompt.isBlank()) {
            _state.update { it.copy(error = "Enter a prompt to generate flashcards.") }
            return
        }

        val count = _state.value.countState.text.toString().trim().toIntOrNull()
            ?.coerceIn(3, 50) ?: 10

        val title = _state.value.titleState.text.toString().trim()
            .ifBlank { "Generated Deck ${Clock.System.now().toEpochMilliseconds()}" }

        _state.update { it.copy(isGenerating = true, error = null, message = null, lastDeckId = null) }

        viewModelScope.launch {
            val userId = awaitUserId()
            if (userId == null) {
                _state.update {
                    it.copy(isGenerating = false, error = "No authenticated user found. Please try again.")
                }
                return@launch
            }

            when (val generation = generationService.generateFlashcards(prompt, count)) {
                is Result.Success -> {
                    val cards = generation.data
                    when (val deckResult = repository.createDeck(
                        userId = userId,
                        title = title,
                        sourceFilename = "gpt-4"
                    )) {
                        is Result.Success -> {
                            val deck = deckResult.data
                            when (val addResult = repository.addCards(deck.id, cards)) {
                                is Result.Success -> {
                                    _state.update {
                                        it.copy(
                                            isGenerating = false,
                                            message = "Saved ${cards.size} cards to ${deck.title}.",
                                            lastDeckId = deck.id
                                        )
                                    }
                                }

                                is Result.Failure -> {
                                    _state.update {
                                        it.copy(
                                            isGenerating = false,
                                            error = "Failed to save cards: ${addResult.error}"
                                        )
                                    }
                                }
                            }
                        }

                        is Result.Failure -> {
                            _state.update {
                                it.copy(
                                    isGenerating = false,
                                    error = "Failed to create deck: ${deckResult.error}"
                                )
                            }
                        }
                    }
                }

                is Result.Failure -> {
                    val message = when (val error = generation.error) {
                        is FlashcardsGenerationError.Parse -> error.message
                        is FlashcardsGenerationError.Empty -> error.message
                        is FlashcardsGenerationError.Remote -> "Generation failed: ${error.error}"
                    }
                    _state.update { it.copy(isGenerating = false, error = message) }
                }
            }
        }
    }

    fun generateFromDocument(document: PickedDocument) {
        if (_state.value.isGenerating) return

        val validationError = validateDocument(document)
        if (validationError != null) {
            _state.update { it.copy(error = validationError) }
            return
        }

        _state.update { it.copy(isGenerating = true, error = null, message = null, lastDeckId = null) }

        viewModelScope.launch {
            val userId = awaitUserId()
            if (userId == null) {
                _state.update {
                    it.copy(isGenerating = false, error = "No authenticated user found. Please try again.")
                }
                return@launch
            }

            val uploadResult = fileService.uploadDocument(
                fileData = document.bytes,
                fileName = document.fileName,
                mimeType = document.mimeType
            )

            if (uploadResult is Result.Failure) {
                _state.update {
                    it.copy(
                        isGenerating = false,
                        error = "Document upload failed: ${uploadResult.error}"
                    )
                }
                return@launch
            }

            val fileId = (uploadResult as Result.Success).data
            try {
                val count = _state.value.countState.text.toString().trim().toIntOrNull()
                    ?.coerceIn(3, 50) ?: 10

                when (val generation = generationService.generateFlashcardsFromFile(fileId, count)) {
                    is Result.Success -> {
                        val cards = generation.data
                        val title = document.fileName.substringBeforeLast('.').ifBlank {
                            "Generated Deck ${Clock.System.now().toEpochMilliseconds()}"
                        }
                        when (val deckResult = repository.createDeck(
                            userId = userId,
                            title = title,
                            sourceFilename = document.fileName
                        )) {
                            is Result.Success -> {
                                val deck = deckResult.data
                                when (val addResult = repository.addCards(deck.id, cards)) {
                                    is Result.Success -> {
                                        _state.update {
                                            it.copy(
                                                isGenerating = false,
                                                message = "Saved ${cards.size} cards to ${deck.title}.",
                                                lastDeckId = deck.id
                                            )
                                        }
                                    }

                                    is Result.Failure -> {
                                        _state.update {
                                            it.copy(
                                                isGenerating = false,
                                                error = "Failed to save cards: ${addResult.error}"
                                            )
                                        }
                                    }
                                }
                            }

                            is Result.Failure -> {
                                _state.update {
                                    it.copy(
                                        isGenerating = false,
                                        error = "Failed to create deck: ${deckResult.error}"
                                    )
                                }
                            }
                        }
                    }

                    is Result.Failure -> {
                        val message = when (val error = generation.error) {
                            is FlashcardsGenerationError.Parse -> error.message
                            is FlashcardsGenerationError.Empty -> error.message
                            is FlashcardsGenerationError.Remote -> "Generation failed: ${error.error}"
                        }
                        _state.update { it.copy(isGenerating = false, error = message) }
                    }
                }
            } finally {
                fileService.deleteFile(fileId)
            }
        }
    }

    fun clearLastDeckId() {
        _state.update { it.copy(lastDeckId = null) }
    }

    fun onDocumentError(message: String) {
        _state.update { it.copy(error = message) }
    }

    private fun validateDocument(document: PickedDocument): String? {
        val maxBytes = 20 * 1024 * 1024
        if (document.bytes.size > maxBytes) {
            return "Document is too large. Max size is 20MB."
        }

        val fileName = document.fileName.lowercase()
        val mimeType = document.mimeType.lowercase()
        val isSupported = mimeType in setOf(
            "application/pdf",
            "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
            "text/plain"
        ) || fileName.endsWith(".pdf") || fileName.endsWith(".docx") || fileName.endsWith(".txt")

        return if (isSupported) null else "Unsupported file type. Use PDF, DOCX, or TXT."
    }

    private suspend fun awaitUserId(
        maxAttempts: Int = 10,
        delayMs: Long = 200
    ): String? {
        repeat(maxAttempts) {
            val userId = authService.currentUserId()
            if (!userId.isNullOrBlank()) {
                return userId
            }
            delay(delayMs)
        }
        return null
    }
}
