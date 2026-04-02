package com.scelio.brainest.presentation.audio

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.scelio.brainest.domain.auth.AuthService
import com.scelio.brainest.domain.util.Result
import com.scelio.brainest.flashcards.domain.AudioTranscriptionService
import com.scelio.brainest.flashcards.domain.FlashcardsGenerationError
import com.scelio.brainest.flashcards.domain.FlashcardsGenerationService
import com.scelio.brainest.flashcards.domain.FlashcardsRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.math.ln
import kotlin.math.max
import kotlin.time.Clock

private const val AmplitudeWindowSize = 50
private const val AmplitudeNoiseFloor = 0.01f
private const val AmplitudeLogCurve = 30f
private const val MinVisualAmplitude = 0.04f
private const val DefaultFlashcardCount = 10

data class AudioRecordingUiState(
    val amplitudes: List<Float> = List(AmplitudeWindowSize) { 0f },
    val status: RecordingStatus = RecordingStatus.STOPPED,
    val transcript: String = "",
    val isGenerating: Boolean = false,
    val generationError: String? = null
)

sealed interface AudioRecordingEvent {
    data class GenerationComplete(val deckId: String) : AudioRecordingEvent
}

class AudioRecordingViewModel(
    private val recorder: AudioRecorder,
    private val transcriptionService: AudioTranscriptionService,
    private val generationService: FlashcardsGenerationService,
    private val repository: FlashcardsRepository,
    private val authService: AuthService
) : ViewModel() {

    private val _state = MutableStateFlow(AudioRecordingUiState())
    val state: StateFlow<AudioRecordingUiState> = _state

    private val _transcriptionErrors = MutableSharedFlow<String>(extraBufferCapacity = 1)
    val transcriptionErrors = _transcriptionErrors.asSharedFlow()

    private val _events = MutableSharedFlow<AudioRecordingEvent>(extraBufferCapacity = 1)
    val events = _events.asSharedFlow()

    init {
        viewModelScope.launch {
            recorder.status.collect { status ->
                _state.update { it.copy(status = status) }
            }
        }

        viewModelScope.launch {
            recorder.amplitude.collect { amplitude ->
                pushAmplitude(amplitude)
            }
        }

        viewModelScope.launch {
            recorder.chunks.collect { chunk ->
                when (val result = transcriptionService.transcribeChunk(chunk)) {
                    is Result.Success -> {
                        val text = result.data.text.trim()
                        if (text.isNotBlank()) {
                            _state.update { state ->
                                val next = if (state.transcript.isBlank()) {
                                    text
                                } else {
                                    "${state.transcript} $text"
                                }
                                state.copy(transcript = next)
                            }
                        }
                    }
                    is Result.Failure -> {
                        _transcriptionErrors.tryEmit(result.error.toString())
                    }
                }
            }
        }
    }

    fun startRecording() {
        viewModelScope.launch { recorder.start() }
    }

    fun togglePauseResume() {
        viewModelScope.launch {
            when (_state.value.status) {
                RecordingStatus.RECORDING -> recorder.pause()
                RecordingStatus.PAUSED,
                RecordingStatus.STOPPED -> recorder.resume()
            }
        }
    }

    fun stopRecording() {
        viewModelScope.launch { recorder.stop() }
    }

    fun finishRecordingAndGenerate() {
        if (_state.value.isGenerating) return

        viewModelScope.launch {
            recorder.stop()

            val transcript = _state.value.transcript.trim()
            if (transcript.isBlank()) {
                _state.update {
                    it.copy(generationError = "Record some audio before generating flashcards.")
                }
                return@launch
            }

            _state.update { it.copy(isGenerating = true, generationError = null) }

            val userId = authService.currentUserId()
            if (userId == null) {
                _state.update {
                    it.copy(
                        isGenerating = false,
                        generationError = "No authenticated user found."
                    )
                }
                return@launch
            }

            when (val generation = generationService.generateFlashcards(
                prompt = transcript,
                count = DefaultFlashcardCount
            )) {
                is Result.Success -> {
                    val cards = generation.data
                    val title = "Audio Deck ${Clock.System.now().toEpochMilliseconds()}"
                    when (val deckResult = repository.createDeck(
                        userId = userId,
                        title = title,
                        sourceFilename = "audio-recording"
                    )) {
                        is Result.Success -> {
                            val deck = deckResult.data
                            when (val addResult = repository.addCards(deck.id, cards)) {
                                is Result.Success -> {
                                    _state.update { it.copy(isGenerating = false) }
                                    _events.tryEmit(AudioRecordingEvent.GenerationComplete(deck.id))
                                }

                                is Result.Failure -> {
                                    _state.update {
                                        it.copy(
                                            isGenerating = false,
                                            generationError = "Failed to save cards: ${addResult.error}"
                                        )
                                    }
                                }
                            }
                        }

                        is Result.Failure -> {
                            _state.update {
                                it.copy(
                                    isGenerating = false,
                                    generationError = "Failed to create deck: ${deckResult.error}"
                                )
                            }
                        }
                    }
                }

                is Result.Failure -> {
                    _state.update {
                        it.copy(
                            isGenerating = false,
                            generationError = generationErrorMessage(generation.error)
                        )
                    }
                }
            }
        }
    }

    override fun onCleared() {
        recorder.release()
    }

    private fun pushAmplitude(rawAmplitude: Float) {
        val normalized = ((rawAmplitude - AmplitudeNoiseFloor) / (1f - AmplitudeNoiseFloor))
            .coerceIn(0f, 1f)
        val curved = ln(1f + AmplitudeLogCurve * normalized) / ln(1f + AmplitudeLogCurve)
        val clamped = curved.coerceIn(0f, 1f)
        _state.update { state ->
            val visual = if (state.status == RecordingStatus.STOPPED) {
                0f
            } else {
                max(clamped, MinVisualAmplitude)
            }
            val updated = (listOf(visual) + state.amplitudes).take(AmplitudeWindowSize)
            state.copy(amplitudes = updated)
        }
    }

    private fun generationErrorMessage(error: FlashcardsGenerationError): String {
        return when (error) {
            is FlashcardsGenerationError.Parse -> error.message
            is FlashcardsGenerationError.Empty -> error.message
            is FlashcardsGenerationError.Remote -> "Generation failed: ${error.error}"
        }
    }
}
