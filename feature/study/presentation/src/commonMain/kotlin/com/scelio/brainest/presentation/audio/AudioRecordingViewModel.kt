package com.scelio.brainest.presentation.audio

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.scelio.brainest.domain.auth.AuthService
import com.scelio.brainest.domain.util.Result
import com.scelio.brainest.domain.util.awaitValue
import com.scelio.brainest.flashcards.domain.AudioTranscriptionService
import com.scelio.brainest.flashcards.domain.FlashcardsRepository
import com.scelio.brainest.flashcards.domain.StudySource
import com.scelio.brainest.flashcards.domain.StudySourceType
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.math.ln
import kotlin.math.max
import kotlin.time.Clock
import kotlin.time.TimeMark
import kotlin.time.TimeSource
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

private const val AmplitudeWindowSize = 50
private const val AmplitudeNoiseFloor = 0.01f
private const val AmplitudeLogCurve = 30f
private const val MinVisualAmplitude = 0.04f
private const val ElapsedTickerIntervalMillis = 1_000L
private const val AmplitudeUiUpdateIntervalMillis = 33L
private val EmptyAmplitudeWindow = List(AmplitudeWindowSize) { 0f }

data class AudioRecordingUiState(
    val status: RecordingStatus = RecordingStatus.STOPPED,
    val elapsedMillis: Long = 0L,
    val transcript: String = "",
    val isGenerating: Boolean = false,
    val generationError: String? = null
)

sealed interface AudioRecordingEvent {
    data class StudySetReady(val deckId: String) : AudioRecordingEvent
}

class AudioRecordingViewModel(
    private val recorder: AudioRecorder,
    private val transcriptionService: AudioTranscriptionService,
    private val repository: FlashcardsRepository,
    private val authService: AuthService
) : ViewModel() {

    private val _state = MutableStateFlow(AudioRecordingUiState())
    val state: StateFlow<AudioRecordingUiState> = _state

    private val _amplitudes = MutableStateFlow(EmptyAmplitudeWindow)
    val amplitudes: StateFlow<List<Float>> = _amplitudes

    private val _transcriptionErrors = MutableSharedFlow<String>(extraBufferCapacity = 1)
    val transcriptionErrors = _transcriptionErrors.asSharedFlow()

    private val _events = MutableSharedFlow<AudioRecordingEvent>(extraBufferCapacity = 1)
    val events = _events.asSharedFlow()

    private var elapsedTickerJob: Job? = null
    private var activeRecordingStartedAt: TimeMark? = null
    private var accumulatedElapsedMillis = 0L
    private var lastAmplitudeUiUpdateMark: TimeMark? = null

    init {
        viewModelScope.launch {
            recorder.status.collect { status ->
                onRecordingStatusChanged(status)
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
        resetElapsedTime()
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
                    it.copy(generationError = "Record some audio before generating.")
                }
                return@launch
            }

            _state.update { it.copy(isGenerating = true, generationError = null) }

            val userId = awaitUserId()
            if (userId == null) {
                _state.update {
                    it.copy(
                        isGenerating = false,
                        generationError = "No authenticated user found."
                    )
                }
                return@launch
            }

            val title = deriveTitleFromTranscript(transcript)
            when (val deckResult = repository.createDeck(
                userId = userId,
                title = title,
                sourceFilename = "audio-recording.m4a"
            )) {
                is Result.Success -> {
                    val deck = deckResult.data
                    val sourceResult = repository.saveStudySource(
                        buildStudySource(
                            deckId = deck.id,
                            transcript = transcript
                        )
                    )
                    if (sourceResult is Result.Failure) {
                        _state.update {
                            it.copy(
                                isGenerating = false,
                                generationError = "Failed to save study source: ${sourceResult.error}"
                            )
                        }
                        return@launch
                    }
                    _state.update { it.copy(isGenerating = false) }
                    _events.tryEmit(AudioRecordingEvent.StudySetReady(deck.id))
                }

                is Result.Failure -> {
                    _state.update {
                        it.copy(
                            isGenerating = false,
                            generationError = "Failed to create study set: ${deckResult.error}"
                        )
                    }
                }
            }
        }
    }

    override fun onCleared() {
        elapsedTickerJob?.cancel()
        recorder.release()
    }

    private fun onRecordingStatusChanged(status: RecordingStatus) {
        _state.update { it.copy(status = status) }

        when (status) {
            RecordingStatus.RECORDING -> startElapsedTicker()
            RecordingStatus.PAUSED,
            RecordingStatus.STOPPED -> stopElapsedTicker()
        }

        if (status == RecordingStatus.STOPPED) {
            resetAmplitudeWindow()
        }
    }

    private fun startElapsedTicker() {
        if (activeRecordingStartedAt == null) {
            activeRecordingStartedAt = TimeSource.Monotonic.markNow()
        }
        publishElapsedTime()

        if (elapsedTickerJob?.isActive == true) return

        elapsedTickerJob = viewModelScope.launch {
            while (isActive) {
                delay(ElapsedTickerIntervalMillis)
                publishElapsedTime()
            }
        }
    }

    private fun stopElapsedTicker() {
        val currentSegment = activeRecordingStartedAt
        if (currentSegment != null) {
            accumulatedElapsedMillis += currentSegment.elapsedNow().inWholeMilliseconds
            activeRecordingStartedAt = null
        }

        elapsedTickerJob?.cancel()
        elapsedTickerJob = null
        publishElapsedTime()
    }

    private fun publishElapsedTime() {
        val activeElapsedMillis = activeRecordingStartedAt
            ?.elapsedNow()
            ?.inWholeMilliseconds
            ?: 0L

        _state.update {
            it.copy(elapsedMillis = accumulatedElapsedMillis + activeElapsedMillis)
        }
    }

    private fun resetElapsedTime() {
        elapsedTickerJob?.cancel()
        elapsedTickerJob = null
        activeRecordingStartedAt = null
        accumulatedElapsedMillis = 0L
        _state.update { it.copy(elapsedMillis = 0L) }
        resetAmplitudeWindow()
    }

    private fun pushAmplitude(rawAmplitude: Float) {
        if (!shouldPublishAmplitudeFrame()) return

        val normalized = ((rawAmplitude - AmplitudeNoiseFloor) / (1f - AmplitudeNoiseFloor))
            .coerceIn(0f, 1f)
        val curved = ln(1f + AmplitudeLogCurve * normalized) / ln(1f + AmplitudeLogCurve)
        val clamped = curved.coerceIn(0f, 1f)

        val visual = if (_state.value.status == RecordingStatus.STOPPED) {
            0f
        } else {
            max(clamped, MinVisualAmplitude)
        }

        _amplitudes.update { current ->
            val size = current.size
            if (size == 0) return@update listOf(visual)

            val updated = MutableList(size) { 0f }
            updated[0] = visual
            for (index in 1 until size) {
                updated[index] = current[index - 1]
            }
            updated
        }
    }

    private fun shouldPublishAmplitudeFrame(): Boolean {
        val lastMark = lastAmplitudeUiUpdateMark
        if (lastMark == null) {
            lastAmplitudeUiUpdateMark = TimeSource.Monotonic.markNow()
            return true
        }

        val elapsedMillis = lastMark.elapsedNow().inWholeMilliseconds
        if (elapsedMillis < AmplitudeUiUpdateIntervalMillis) {
            return false
        }

        lastAmplitudeUiUpdateMark = TimeSource.Monotonic.markNow()
        return true
    }

    private fun resetAmplitudeWindow() {
        lastAmplitudeUiUpdateMark = null
        _amplitudes.value = EmptyAmplitudeWindow
    }

    private fun deriveTitleFromTranscript(transcript: String): String {
        val words = transcript.split(Regex("\\s+")).filter { it.isNotBlank() }
        val candidate = words.take(6).joinToString(" ")
        return if (candidate.isBlank()) {
            "Audio Set ${Clock.System.now().toEpochMilliseconds()}"
        } else {
            candidate
        }
    }

    @OptIn(ExperimentalUuidApi::class)
    private fun buildStudySource(
        deckId: String,
        transcript: String
    ): StudySource {
        return StudySource(
            id = Uuid.random().toString(),
            deckId = deckId,
            sourceType = StudySourceType.AUDIO,
            sourceText = transcript,
            sourceFileId = null,
            sourceFilename = "audio-recording.m4a",
            createdAt = Clock.System.now()
        )
    }

    private suspend fun awaitUserId(): String? {
        return awaitValue({ authService.currentUserId() })
    }
}
