package com.scelio.brainest.presentation.studysets

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.scelio.brainest.domain.auth.AuthService
import com.scelio.brainest.domain.util.Result
import com.scelio.brainest.flashcards.domain.DocumentTranscriptionError
import com.scelio.brainest.flashcards.domain.DocumentTranscriptionService
import com.scelio.brainest.flashcards.domain.Deck
import com.scelio.brainest.flashcards.domain.FlashcardsRepository
import com.scelio.brainest.flashcards.domain.OpenAiFileService
import com.scelio.brainest.flashcards.domain.StudySource
import com.scelio.brainest.flashcards.domain.StudySourceType
import com.scelio.brainest.presentation.flashcards.PickedDocument
import kotlin.time.Clock
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay

data class StudySetItemUi(
    val id: String,
    val title: String,
    val createdAt: kotlin.time.Instant,
    val flashcardsCount: Int,
    val quizCount: Int,
    val flashcardsSwiped: Int,
    val quizzesCompleted: Int
)

data class StudySetsState(
    val isLoading: Boolean = false,
    val sets: List<StudySetItemUi> = emptyList(),
    val error: String? = null,
    val isCreating: Boolean = false,
    val creationError: String? = null
)

sealed interface StudySetsEvent {
    data class OpenSetDetail(
        val deckId: String,
        val promptGeneration: Boolean
    ) : StudySetsEvent
}

class StudySetsViewModel(
    private val repository: FlashcardsRepository,
    private val fileService: OpenAiFileService,
    private val documentTranscriptionService: DocumentTranscriptionService,
    private val authService: AuthService
) : ViewModel() {

    private val _state = MutableStateFlow(StudySetsState())
    val state: StateFlow<StudySetsState> = _state

    private val _events = MutableSharedFlow<StudySetsEvent>(extraBufferCapacity = 1)
    val events = _events.asSharedFlow()

    private var observeSetsJob: Job? = null
    private var observedUserId: String? = null

    fun loadSets() {
        viewModelScope.launch {
            val userId = awaitUserId()
            if (userId == null) {
                _state.update { it.copy(isLoading = false, error = "No authenticated user found.") }
                return@launch
            }

            if (observedUserId != userId) {
                observedUserId = userId
                observeStudySets(userId)
            }

            val hasLocalSets = _state.value.sets.isNotEmpty()
            _state.update { it.copy(isLoading = !hasLocalSets, error = null) }

            when (val syncResult = repository.syncStudySetSummaries(userId)) {
                is Result.Success -> {
                    _state.update {
                        if (it.isLoading && it.sets.isEmpty()) {
                            it.copy(isLoading = false)
                        } else {
                            it
                        }
                    }
                }

                is Result.Failure -> {
                    if (_state.value.sets.isEmpty()) {
                        _state.update {
                            it.copy(
                                isLoading = false,
                                error = "Failed to sync study sets: ${syncResult.error}"
                            )
                        }
                    }
                }
            }
        }
    }

    fun createSetFromDocument(document: PickedDocument) {
        if (_state.value.isCreating) return

        val validationError = validateDocument(document)
        if (validationError != null) {
            _state.update { it.copy(creationError = validationError) }
            return
        }

        _state.update { it.copy(isCreating = true, creationError = null) }

        viewModelScope.launch {
            val userId = awaitUserId()
            if (userId == null) {
                _state.update { it.copy(isCreating = false, creationError = "No authenticated user found.") }
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
                        isCreating = false,
                        creationError = "Document upload failed: ${uploadResult.error}"
                    )
                }
                return@launch
            }

            val fileId = (uploadResult as Result.Success).data
            try {
                val transcriptionResult = documentTranscriptionService.transcribeFile(fileId)
                if (transcriptionResult is Result.Failure) {
                    _state.update {
                        it.copy(
                            isCreating = false,
                            creationError = "Document transcription failed: ${
                                documentTranscriptionErrorMessage(transcriptionResult.error)
                            }"
                        )
                    }
                    return@launch
                }

                val transcript = (transcriptionResult as Result.Success).data.text.trim()
                if (transcript.isBlank()) {
                    _state.update {
                        it.copy(
                            isCreating = false,
                            creationError = "Document transcription failed: empty text."
                        )
                    }
                    return@launch
                }

                val title = document.fileName.substringBeforeLast('.').ifBlank {
                    "Study Set ${Clock.System.now().toEpochMilliseconds()}"
                }

                when (val deckResult = repository.createDeck(
                    userId = userId,
                    title = title,
                    sourceFilename = document.fileName
                )) {
                    is Result.Success -> {
                        val deck = deckResult.data
                        val source = buildStudySource(
                            deck = deck,
                            type = StudySourceType.DOCUMENT,
                            sourceText = transcript,
                            sourceFileId = null,
                            sourceFilename = document.fileName
                        )
                        val sourceResult = repository.saveStudySource(source)
                        if (sourceResult is Result.Failure) {
                            _state.update {
                                it.copy(
                                    isCreating = false,
                                    creationError = "Failed to save study source: ${sourceResult.error}"
                                )
                            }
                            return@launch
                        }
                        _state.update { it.copy(isCreating = false) }
                        _events.tryEmit(StudySetsEvent.OpenSetDetail(deck.id, promptGeneration = true))
                    }

                    is Result.Failure -> {
                        _state.update {
                            it.copy(
                                isCreating = false,
                                creationError = "Failed to create study set: ${deckResult.error}"
                            )
                        }
                    }
                }
            } finally {
                fileService.deleteFile(fileId)
            }
        }
    }

    fun clearCreationError() {
        _state.update { it.copy(creationError = null) }
    }

    fun setCreationError(message: String) {
        _state.update { it.copy(creationError = message) }
    }

    private fun observeStudySets(userId: String) {
        observeSetsJob?.cancel()
        observeSetsJob = repository.observeStudySetSummaries(userId)
            .combine(repository.observeDeckStudyProgressSummaries(userId)) { sets, progressSummaries ->
                val progressByDeckId = progressSummaries.associateBy { it.deckId }
                sets.map { set ->
                    val progress = progressByDeckId[set.id]
                    StudySetItemUi(
                        id = set.id,
                        title = set.title,
                        createdAt = set.createdAt,
                        flashcardsCount = set.flashcardsCount,
                        quizCount = set.quizCount,
                        flashcardsSwiped = progress?.flashcardsSwiped ?: 0,
                        quizzesCompleted = progress?.quizzesCompleted ?: 0
                    )
                }
            }
            .onEach { sets ->
                _state.update { state ->
                    state.copy(
                        isLoading = false,
                        sets = sets,
                        error = if (sets.isNotEmpty()) null else state.error
                    )
                }
            }
            .launchIn(viewModelScope)
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

    private fun documentTranscriptionErrorMessage(error: DocumentTranscriptionError): String {
        return when (error) {
            is DocumentTranscriptionError.Empty -> error.message
            is DocumentTranscriptionError.Invalid -> error.message
            is DocumentTranscriptionError.Remote -> "Transcription failed: ${error.error}"
        }
    }

    @OptIn(ExperimentalUuidApi::class)
    private fun buildStudySource(
        deck: Deck,
        type: StudySourceType,
        sourceText: String?,
        sourceFileId: String?,
        sourceFilename: String?
    ): StudySource {
        return StudySource(
            id = Uuid.random().toString(),
            deckId = deck.id,
            sourceType = type,
            sourceText = sourceText,
            sourceFileId = sourceFileId,
            sourceFilename = sourceFilename,
            createdAt = Clock.System.now()
        )
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
