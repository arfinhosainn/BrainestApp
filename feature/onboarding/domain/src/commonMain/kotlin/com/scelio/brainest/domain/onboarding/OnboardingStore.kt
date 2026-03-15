package com.scelio.brainest.domain.onboarding

import kotlinx.coroutines.flow.Flow

interface OnboardingStore {
    val data: Flow<OnboardingData>
    suspend fun save(data: OnboardingData)
    suspend fun clear()
}
