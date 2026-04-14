package com.scelio.brainest.presentation.di

import com.scelio.brainest.presentation.settings.SettingsViewModel
import org.koin.dsl.module
import org.koin.core.module.dsl.viewModelOf

val settingsPresentationModule = module {
    viewModelOf(::SettingsViewModel)
}
