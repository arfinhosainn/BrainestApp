package com.scellio.brainest.di

import com.scelio.brainest.data.di.chatDataModule
import com.scelio.brainest.data.di.coreDataModule
import com.brainest.presentation.di.onboardingPresentationModule
import com.scelio.brainest.presentation.di.authPresentationModule
import com.scelio.brainest.presentation.di.chatPresentationModule
import com.scelio.brainest.presentation.di.corePresentationModule
import org.koin.core.context.startKoin
import org.koin.dsl.KoinAppDeclaration

fun initKoin(config: KoinAppDeclaration? = null) {
    startKoin {
        config?.invoke(this)
        modules(
            coreDataModule,
            authPresentationModule,
            corePresentationModule,
            chatPresentationModule,
            onboardingPresentationModule,
            appModule,
            chatDataModule

        )
    }
}
