package com.scelio.brainest.flashcards.domain

import com.scelio.brainest.domain.util.Result

interface FlashcardsGenerationService {
    suspend fun generateFlashcards(
        prompt: String,
        count: Int
    ): Result<List<FlashcardInput>, FlashcardsGenerationError>
}
