package com.scellio.brainest.di

import com.scelio.brainest.data.di.coreDataModule
import com.scelio.brainest.presentation.di.authPresentationModule
import org.koin.core.context.startKoin
import org.koin.dsl.KoinAppDeclaration

fun initKoin(config: KoinAppDeclaration? = null) {
    startKoin {
        config?.invoke(this)
        modules(
            coreDataModule,
            authPresentationModule
        )
    }
}