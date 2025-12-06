package com.scellio.brainest.di

import com.scellio.brainest.MainViewModel
import com.scelio.brainest.data.di.coreDataModule
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val appModule = module {
    includes(coreDataModule)
    viewModelOf(::MainViewModel)
}