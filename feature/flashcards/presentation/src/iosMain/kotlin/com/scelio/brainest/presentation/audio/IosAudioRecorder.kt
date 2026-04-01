package com.scelio.brainest.presentation.audio

import com.scelio.brainest.flashcards.domain.AudioChunkData
import kotlinx.cinterop.ObjCObjectVar
import kotlinx.cinterop.alloc
import kotlinx.cinterop.memScoped
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import platform.AVFoundation.AVAudioEngine
import platform.AVFoundation.AVAudioPCMBuffer
import platform.AVFoundation.AVAudioSession
import platform.AVFoundation.AVAudioSessionCategoryPlayAndRecord
import platform.AVFoundation.AVAudioSessionModeDefault
import platform.AVFoundation.AVAudioSessionOptionDefaultToSpeaker
import platform.AVFoundation.AVAudioSessionOptionMixWithOthers
import platform.AVFoundation.AVAudioTime
import platform.Foundation.NSError
import kotlin.math.max
import kotlin.math.sqrt

class IosAudioRecorder(
    private val chunkSeconds: Int = 4,
    private val overlapSeconds: Int = 1
) : AudioRecorder {
    private val _amplitude = MutableStateFlow(0f)
    override val amplitude: Flow<Float> = _amplitude

    private val _status = MutableStateFlow(RecordingStatus.STOPPED)
    override val status: StateFlow<RecordingStatus> = _status

    private val _chunks = MutableSharedFlow<AudioChunkData>(
        extraBufferCapacity = 2
    )
    override val chunks: Flow<AudioChunkData> = _chunks

    private var engine: AVAudioEngine? = null
    private var hasTap = false
    private var engineRunning = false
    private var ringBuffer = ShortArray(0)
    private var ringIndex = 0
    private var totalSamplesWritten = 0L
    private var lastChunkSample = 0L
    private var chunkCounter = 0L
    private var sampleRate = 0

    override suspend fun start() {
        if (_status.value == RecordingStatus.RECORDING) return
        configureSession()
        val audioEngine = ensureEngine()
        installTapIfNeeded(audioEngine)
        startEngine(audioEngine)
        _status.value = RecordingStatus.RECORDING
        resetChunking()
    }

    override suspend fun pause() {
        if (_status.value != RecordingStatus.RECORDING) return
        _status.value = RecordingStatus.PAUSED
        engine?.pause()
        engineRunning = false
        _amplitude.value = 0f
        resetChunking()
    }

    override suspend fun resume() {
        if (_status.value == RecordingStatus.RECORDING) return
        configureSession()
        val audioEngine = ensureEngine()
        installTapIfNeeded(audioEngine)
        startEngine(audioEngine)
        _status.value = RecordingStatus.RECORDING
        resetChunking()
    }

    override suspend fun stop() {
        _status.value = RecordingStatus.STOPPED
        engine?.apply {
            inputNode.removeTapOnBus(0u)
            stop()
        }
        hasTap = false
        engineRunning = false
        engine = null
        _amplitude.value = 0f
        resetChunking()
        deactivateSession()
    }

    override fun release() {
        _status.value = RecordingStatus.STOPPED
        engine?.apply {
            inputNode.removeTapOnBus(0u)
            stop()
        }
        hasTap = false
        engineRunning = false
        engine = null
        _amplitude.value = 0f
        resetChunking()
        deactivateSession()
    }

    private fun ensureEngine(): AVAudioEngine {
        return engine ?: AVAudioEngine().also { engine = it }
    }

    private fun installTapIfNeeded(audioEngine: AVAudioEngine) {
        if (hasTap) return
        val inputNode = audioEngine.inputNode
        val format = inputNode.inputFormatForBus(0u)
        ensureChunking(format.sampleRate)
        inputNode.installTapOnBus(
            bus = 0u,
            bufferSize = 1024u,
            format = format
        ) { buffer: AVAudioPCMBuffer, _: AVAudioTime? ->
            if (_status.value != RecordingStatus.RECORDING) return@installTapOnBus
            appendBufferSamples(buffer)
            _amplitude.value = bufferAmplitude(buffer)
        }
        hasTap = true
    }

    private fun startEngine(audioEngine: AVAudioEngine) {
        if (engineRunning) return
        memScoped {
            val error = alloc<ObjCObjectVar<NSError?>>()
            audioEngine.startAndReturnError(error.ptr)
        }
        engineRunning = true
    }

    private fun bufferAmplitude(buffer: AVAudioPCMBuffer): Float {
        val frameLength = buffer.frameLength.toInt().coerceAtLeast(1)
        val channelCount = buffer.format.channelCount.toInt().coerceAtLeast(1)
        val floatChannels = buffer.floatChannelData
        if (floatChannels != null) {
            var sum = 0.0
            for (ch in 0 until channelCount) {
                val channel = floatChannels[ch] ?: continue
                for (i in 0 until frameLength) {
                    val sample = channel[i]
                    sum += sample * sample
                }
            }
            val rms = sqrt(sum / (frameLength * channelCount))
            return rms.toFloat().coerceIn(0f, 1f)
        }

        val int16Channels = buffer.int16ChannelData
        if (int16Channels != null) {
            var sum = 0.0
            for (ch in 0 until channelCount) {
                val channel = int16Channels[ch] ?: continue
                for (i in 0 until frameLength) {
                    val sample = channel[i].toDouble()
                    sum += sample * sample
                }
            }
            val rms = sqrt(sum / (frameLength * channelCount))
            return (rms / Short.MAX_VALUE).toFloat().coerceIn(0f, 1f)
        }

        return 0f
    }

    private fun appendBufferSamples(buffer: AVAudioPCMBuffer) {
        if (ringBuffer.isEmpty()) return
        val frameLength = buffer.frameLength.toInt()
        if (frameLength <= 0) return
        val chunkSamples = chunkSamples()
        val floatChannels = buffer.floatChannelData
        if (floatChannels != null) {
            val channel = floatChannels[0] ?: return
            for (i in 0 until frameLength) {
                val sample = channel[i].coerceIn(-1f, 1f)
                val shortValue = (sample * Short.MAX_VALUE).toInt().toShort()
                ringBuffer[ringIndex] = shortValue
                ringIndex = (ringIndex + 1) % chunkSamples
                totalSamplesWritten += 1
            }
            emitChunksIfReady()
            return
        }

        val int16Channels = buffer.int16ChannelData
        if (int16Channels != null) {
            val channel = int16Channels[0] ?: return
            for (i in 0 until frameLength) {
                ringBuffer[ringIndex] = channel[i]
                ringIndex = (ringIndex + 1) % chunkSamples
                totalSamplesWritten += 1
            }
            emitChunksIfReady()
        }
    }

    private fun emitChunksIfReady() {
        val chunkSamples = chunkSamples()
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
        val wavBytes = buildWavBytes(samples, sampleRate, 1)
        val name = "chunk-${chunkCounter++}.wav"
        _chunks.tryEmit(
            AudioChunkData(
                bytes = wavBytes,
                mimeType = "audio/wav",
                fileName = name
            )
        )
    }

    private fun ensureChunking(rawSampleRate: Double) {
        val newRate = rawSampleRate.toInt().coerceAtLeast(8000)
        if (newRate == sampleRate && ringBuffer.isNotEmpty()) return
        sampleRate = newRate
        ringBuffer = ShortArray(chunkSamples())
        resetChunking()
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

    private fun chunkSamples(): Int = max(sampleRate * chunkSeconds, 1)

    private fun stepSamples(): Int = max(sampleRate * (chunkSeconds - overlapSeconds), 1)

    private fun buildWavBytes(
        samples: ShortArray,
        sampleRate: Int,
        channels: Int
    ): ByteArray {
        val dataSize = samples.size * 2
        val totalSize = 44 + dataSize
        val buffer = ByteArray(totalSize)

        fun writeString(offset: Int, value: String) {
            value.encodeToByteArray().copyInto(buffer, offset)
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

    private fun configureSession() {
        val session = AVAudioSession.sharedInstance()
        session.setCategory(
            category = AVAudioSessionCategoryPlayAndRecord,
            withOptions = AVAudioSessionOptionDefaultToSpeaker or AVAudioSessionOptionMixWithOthers,
            error = null
        )
        session.setMode(AVAudioSessionModeDefault, error = null)
        session.setActive(true, error = null)
    }

    private fun deactivateSession() {
        AVAudioSession.sharedInstance().setActive(false, error = null)
    }
}
