package com.scelio.brainest.presentation.di

import com.scelio.brainest.presentation.flashcards.FlashcardsGenerateViewModel
import com.scelio.brainest.presentation.flashcards.FlashcardsSessionViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val flashcardsPresentationModule = module {
    viewModelOf(::FlashcardsGenerateViewModel)
    viewModelOf(::FlashcardsSessionViewModel)
}
