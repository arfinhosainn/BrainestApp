package com.scelio.brainest.presentation.audio

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.scelio.brainest.domain.util.Result
import com.scelio.brainest.flashcards.domain.AudioTranscriptionService
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.math.ln
import kotlin.math.max

private const val AmplitudeWindowSize = 50
private const val AmplitudeNoiseFloor = 0.01f
private const val AmplitudeLogCurve = 30f
private const val MinVisualAmplitude = 0.04f

data class AudioRecordingUiState(
    val amplitudes: List<Float> = List(AmplitudeWindowSize) { 0f },
    val status: RecordingStatus = RecordingStatus.STOPPED,
    val transcript: String = ""
)

class AudioRecordingViewModel(
    private val recorder: AudioRecorder,
    private val transcriptionService: AudioTranscriptionService
) : ViewModel() {

    private val _state = MutableStateFlow(AudioRecordingUiState())
    val state: StateFlow<AudioRecordingUiState> = _state

    private val _transcriptionErrors = MutableSharedFlow<String>(extraBufferCapacity = 1)
    val transcriptionErrors = _transcriptionErrors.asSharedFlow()

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
}
