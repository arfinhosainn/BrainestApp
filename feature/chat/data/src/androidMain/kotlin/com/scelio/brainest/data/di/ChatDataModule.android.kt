package com.scelio.brainest.data.di

import com.scelio.brainest.database.DatabaseFactory
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module


actual val platformChatDataModule = module {
    single { DatabaseFactory(androidContext()) }
}