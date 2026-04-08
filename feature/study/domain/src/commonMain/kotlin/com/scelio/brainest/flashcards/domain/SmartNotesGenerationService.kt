package com.scelio.brainest.flashcards.domain

import com.scelio.brainest.domain.util.Result

interface SmartNotesGenerationService {
    suspend fun generateSmartNotes(
        text: String
    ): Result<String, SmartNotesGenerationError>

    suspend fun generateSmartNotesFromFile(
        fileId: String
    ): Result<String, SmartNotesGenerationError>
}
