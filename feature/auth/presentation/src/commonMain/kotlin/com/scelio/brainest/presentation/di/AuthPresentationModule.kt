package com.scelio.brainest.presentation.di

import com.scelio.brainest.presentation.register.RegisterViewModel
import org.koin.dsl.module
import org.koin.core.module.dsl.viewModelOf



val authPresentationModule = module {
    viewModelOf(::RegisterViewModel)
}