package com.scelio.brainest.presentation.audio

import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import com.scelio.brainest.flashcards.domain.AudioChunkData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.Flow
import kotlin.math.sqrt
import kotlin.math.max

class AndroidAudioRecorder(
    private val chunkSeconds: Int = 4,
    private val overlapSeconds: Int = 1
) : AudioRecorder {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    private val _amplitude = MutableStateFlow(0f)
    override val amplitude: Flow<Float> = _amplitude

    private val _status = MutableStateFlow(RecordingStatus.STOPPED)
    override val status: StateFlow<RecordingStatus> = _status

    private val _chunks = MutableSharedFlow<AudioChunkData>(
        extraBufferCapacity = 2
    )
    override val chunks: Flow<AudioChunkData> = _chunks

    private var audioRecord: AudioRecord? = null
    private var readJob: Job? = null
    private var ringBuffer = ShortArray(0)
    private var ringIndex = 0
    private var totalSamplesWritten = 0L
    private var lastChunkSample = 0L
    private var chunkCounter = 0L

    override suspend fun start() {
        if (_status.value == RecordingStatus.RECORDING) return
        ensureRecorder()
        resetChunking()
        audioRecord?.startRecording()
        _status.value = RecordingStatus.RECORDING
        startReadLoop()
    }

    override suspend fun pause() {
        if (_status.value != RecordingStatus.RECORDING) return
        _status.value = RecordingStatus.PAUSED
        tryStop()
        resetChunking()
    }

    override suspend fun resume() {
        if (_status.value == RecordingStatus.RECORDING) return
        ensureRecorder()
        resetChunking()
        audioRecord?.startRecording()
        _status.value = RecordingStatus.RECORDING
        startReadLoop()
    }

    override suspend fun stop() {
        _status.value = RecordingStatus.STOPPED
        tryStop()
        audioRecord?.release()
        audioRecord = null
        readJob?.cancel()
        readJob = null
        resetChunking()
        _amplitude.value = 0f
    }

    override fun release() {
        _status.value = RecordingStatus.STOPPED
        tryStop()
        audioRecord?.release()
        audioRecord = null
        readJob?.cancel()
        readJob = null
        resetChunking()
    }

    private fun ensureRecorder() {
        if (audioRecord != null) return
        val minBuffer = AudioRecord.getMinBufferSize(
            SAMPLE_RATE,
            CHANNEL_CONFIG,
            AUDIO_FORMAT
        )
        val bufferSize = max(minBuffer, SAMPLE_RATE / 2)
        audioRecord = AudioRecord(
            MediaRecorder.AudioSource.MIC,
            SAMPLE_RATE,
            CHANNEL_CONFIG,
            AUDIO_FORMAT,
            bufferSize
        )
        ringBuffer = ShortArray(chunkSamples())
    }

    private fun tryStop() {
        try {
            audioRecord?.stop()
        } catch (_: IllegalStateException) {
            // Ignore stop errors when not recording.
        }
    }

    private fun startReadLoop() {
        if (readJob?.isActive == true) return
        val buffer = ShortArray(READ_BUFFER_SIZE)
        readJob = scope.launch {
            while (isActive) {
                if (_status.value != RecordingStatus.RECORDING) {
                    _amplitude.value = 0f
                    delay(IDLE_DELAY_MS)
                    continue
                }

                val recorder = audioRecord
                if (recorder == null) {
                    delay(IDLE_DELAY_MS)
                    continue
                }

                val read = recorder.read(buffer, 0, buffer.size)
                if (read > 0) {
                    writeSamples(buffer, read)
                    _amplitude.value = rmsAmplitude(buffer, read)
                } else {
                    delay(READ_RETRY_DELAY_MS)
                }
            }
        }
    }

    private fun rmsAmplitude(buffer: ShortArray, read: Int): Float {
        var sum = 0.0
        for (i in 0 until read) {
            val value = buffer[i].toDouble()
            sum += value * value
        }
        val rms = sqrt(sum / read)
        return (rms / Short.MAX_VALUE).toFloat().coerceIn(0f, 1f)
    }

    private fun writeSamples(buffer: ShortArray, read: Int) {
        if (_status.value != RecordingStatus.RECORDING) return
        if (ringBuffer.isEmpty()) return
        val chunkSamples = chunkSamples()
        for (i in 0 until read) {
            ringBuffer[ringIndex] = buffer[i]
            ringIndex = (ringIndex + 1) % chunkSamples
            totalSamplesWritten += 1
        }

        val stepSamples = stepSamples()
        while (totalSamplesWritten >= chunkSamples &&
            totalSamplesWritten - lastChunkSample >= stepSamples
        ) {
            val snapshot = ShortArray(chunkSamples)
            for (i in 0 until chunkSamples) {
                val index = (ringIndex + i) % chunkSamples
                snapshot[i] = ringBuffer[index]
            }
            emitChunk(snapshot)
            lastChunkSample += stepSamples
        }
    }

    private fun emitChunk(samples: ShortArray) {
        val wavBytes = buildWavBytes(samples, SAMPLE_RATE, CHANNELS_MONO)
        val name = "chunk-${chunkCounter++}.wav"
        _chunks.tryEmit(
            AudioChunkData(
                bytes = wavBytes,
                mimeType = "audio/wav",
                fileName = name
            )
        )
    }

    private fun resetChunking() {
        ringIndex = 0
        totalSamplesWritten = 0L
        lastChunkSample = 0L
        chunkCounter = 0L
        if (ringBuffer.isNotEmpty()) {
            ringBuffer.fill(0)
        }
    }

    private fun chunkSamples(): Int = SAMPLE_RATE * chunkSeconds

    private fun stepSamples(): Int = SAMPLE_RATE * (chunkSeconds - overlapSeconds)

    private fun buildWavBytes(
        samples: ShortArray,
        sampleRate: Int,
        channels: Int
    ): ByteArray {
        val dataSize = samples.size * 2
        val totalSize = 44 + dataSize
        val buffer = ByteArray(totalSize)

        fun writeString(offset: Int, value: String) {
            value.toByteArray().copyInto(buffer, offset)
        }

        fun writeIntLE(offset: Int, value: Int) {
            buffer[offset] = (value and 0xFF).toByte()
            buffer[offset + 1] = (value shr 8 and 0xFF).toByte()
            buffer[offset + 2] = (value shr 16 and 0xFF).toByte()
            buffer[offset + 3] = (value shr 24 and 0xFF).toByte()
        }

        fun writeShortLE(offset: Int, value: Int) {
            buffer[offset] = (value and 0xFF).toByte()
            buffer[offset + 1] = (value shr 8 and 0xFF).toByte()
        }

        writeString(0, "RIFF")
        writeIntLE(4, 36 + dataSize)
        writeString(8, "WAVE")
        writeString(12, "fmt ")
        writeIntLE(16, 16)
        writeShortLE(20, 1)
        writeShortLE(22, channels)
        writeIntLE(24, sampleRate)
        val byteRate = sampleRate * channels * 2
        writeIntLE(28, byteRate)
        writeShortLE(32, channels * 2)
        writeShortLE(34, 16)
        writeString(36, "data")
        writeIntLE(40, dataSize)

        var offset = 44
        for (sample in samples) {
            writeShortLE(offset, sample.toInt())
            offset += 2
        }
        return buffer
    }

    private companion object {
        const val SAMPLE_RATE = 16_000
        const val CHANNEL_CONFIG = AudioFormat.CHANNEL_IN_MONO
        const val AUDIO_FORMAT = AudioFormat.ENCODING_PCM_16BIT
        const val CHANNELS_MONO = 1
        const val READ_BUFFER_SIZE = 1024
        const val IDLE_DELAY_MS = 50L
        const val READ_RETRY_DELAY_MS = 10L
    }
}
