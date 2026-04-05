package com.scelio.brainest.flashcards.domain

import com.scelio.brainest.domain.util.Result

interface DocumentTranscriptionService {
    suspend fun transcribeFile(
        fileId: String
    ): Result<DocumentTranscriptionResult, DocumentTranscriptionError>
}
