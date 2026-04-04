package com.scelio.brainest.presentation.di

import com.scelio.brainest.presentation.audio.AudioRecordingViewModel
import com.scelio.brainest.presentation.flashcards.FlashcardsGenerateViewModel
import com.scelio.brainest.presentation.flashcards.FlashcardsSessionViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.core.module.Module
import org.koin.dsl.module

expect val platformFlashcardsPresentationModule: Module

val flashcardsPresentationModule = module {
    includes(platformFlashcardsPresentationModule)
    viewModelOf(::FlashcardsGenerateViewModel)
    viewModelOf(::FlashcardsSessionViewModel)
    viewModelOf(::AudioRecordingViewModel)
}
