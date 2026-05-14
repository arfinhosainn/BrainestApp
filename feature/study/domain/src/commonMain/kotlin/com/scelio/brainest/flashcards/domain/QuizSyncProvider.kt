package com.scelio.brainest.flashcards.domain

import com.scelio.brainest.domain.util.DataError
import com.scelio.brainest.domain.util.EmptyResult

/**
 * Interface for syncing quiz data when a deck is synced.
 *
 * This breaks the cross-feature dependency between flashcards and quiz modules.
 * The quiz module provides an implementation, and the flashcards module only depends on this interface.
 */
interface QuizSyncProvider {
    /**
     * Syncs quiz questions for the given deck ID.
     *
     * @param deckId The deck identifier to sync quiz questions for
     * @return Success if synced, Failure with error details
     */
    suspend fun syncDeckQuizQuestions(deckId: String): EmptyResult<DataError.Remote>

    /**
     * Syncs quiz progress for the given deck ID.
     *
     * @param deckId The deck identifier to sync quiz progress for
     * @return Success if synced, Failure with error details
     */
    suspend fun syncDeckQuizProgress(deckId: String): EmptyResult<DataError.Remote>
}
