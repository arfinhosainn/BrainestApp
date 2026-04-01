package com.scelio.brainest.presentation.audio

import com.scelio.brainest.flashcards.domain.AudioChunkData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

enum class RecordingStatus {
    RECORDING,
    PAUSED,
    STOPPED
}

interface AudioRecorder {
    val amplitude: Flow<Float>
    val status: StateFlow<RecordingStatus>
    val chunks: Flow<AudioChunkData>

    suspend fun start()
    suspend fun pause()
    suspend fun resume()
    suspend fun stop()
    fun release()
}

class NoOpAudioRecorder : AudioRecorder {
    private val _amplitude = MutableStateFlow(0f)
    override val amplitude: Flow<Float> = _amplitude

    private val _status = MutableStateFlow(RecordingStatus.STOPPED)
    override val status: StateFlow<RecordingStatus> = _status

    private val _chunks = MutableSharedFlow<AudioChunkData>(
        extraBufferCapacity = 1
    )
    override val chunks: Flow<AudioChunkData> = _chunks

    override suspend fun start() {
        _status.value = RecordingStatus.RECORDING
    }

    override suspend fun pause() {
        _status.value = RecordingStatus.PAUSED
    }

    override suspend fun resume() {
        _status.value = RecordingStatus.RECORDING
    }

    override suspend fun stop() {
        _status.value = RecordingStatus.STOPPED
    }

    override fun release() = Unit
}
