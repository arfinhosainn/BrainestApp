package com.scelio.brainest.data.di

import com.scelio.brainest.data.onboarding.OnboardingSyncerImpl
import com.scelio.brainest.domain.logging.BrainestLogger
import com.scelio.brainest.data.onboarding.local.OnboardingStoreImpl
import com.scelio.brainest.domain.onboarding.OnboardingStore
import com.scelio.brainest.domain.onboarding.OnboardingSyncer
import org.koin.core.module.Module
import org.koin.dsl.module

expect val platformOnboardingDataModule: Module

val onboardingDataModule = module {
    includes(platformOnboardingDataModule)
    single<OnboardingStore> { OnboardingStoreImpl(get()) }
    single<OnboardingSyncer> { OnboardingSyncerImpl(get(), get(), get<BrainestLogger>()) }
}
