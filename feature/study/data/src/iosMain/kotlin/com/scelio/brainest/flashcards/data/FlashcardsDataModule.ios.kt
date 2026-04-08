package com.scelio.brainest.flashcards.data

import com.scelio.brainest.flashcards.database.DatabaseFactory
import org.koin.dsl.module

actual val platformFlashcardsDataModule = module {
    single { DatabaseFactory() }
}
