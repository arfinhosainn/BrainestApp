package com.scelio.brainest.data.di

import com.scelio.brainest.data.auth.SupabaseAuthService
import com.scelio.brainest.data.logging.KermitLogger
import com.scelio.brainest.data.networking.HttpClientFactory
import com.scelio.brainest.domain.auth.AuthService
import com.scelio.brainest.domain.logging.BrainestLogger
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

expect val platformCoreDataModule: Module

val coreDataModule = module {
    includes(platformCoreDataModule)
    single<BrainestLogger> { KermitLogger }
    single {
        HttpClientFactory(get()).create(get())
    }
    singleOf(::SupabaseAuthService) bind AuthService::class
}