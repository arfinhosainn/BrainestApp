package com.scelio.brainest.domain.util

import kotlinx.coroutines.delay

/**
 * Polls [provider] until it returns a non-null value or [maxAttempts] is reached.
 *
 * This is a workaround for auth state propagation delays after login/registration.
 * Prefer direct synchronous retrieval when possible.
 *
 * @param provider Suspended function that returns the value to wait for
 * @param maxAttempts Maximum number of polling attempts
 * @param delayMs Delay between attempts in milliseconds
 * @return The first non-null value, or null if exhausted
 */
suspend fun <T : Any> awaitValue(
    provider: suspend () -> T?,
    maxAttempts: Int = 10,
    delayMs: Long = 200
): T? {
    repeat(maxAttempts) {
        val value = provider()
        if (value != null) return value
        delay(delayMs)
    }
    return null
}
