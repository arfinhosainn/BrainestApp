package com.scelio.brainest.domain.onboarding

import com.scelio.brainest.domain.util.DataError
import com.scelio.brainest.domain.util.EmptyResult

interface OnboardingSyncer {
    suspend fun sync(userId: String): EmptyResult<DataError.Remote>
}
