package com.scelio.brainest.flashcards.data

import com.scelio.brainest.flashcards.database.DatabaseFactory
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

actual val platformFlashcardsDataModule = module {
    single { DatabaseFactory(androidContext()) }
}
