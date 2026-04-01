package com.scelio.brainest.flashcards.domain

import com.scelio.brainest.domain.util.Result

interface AudioTranscriptionService {
    suspend fun transcribeChunk(
        chunk: AudioChunkData
    ): Result<AudioTranscriptionResult, AudioTranscriptionError>
}
