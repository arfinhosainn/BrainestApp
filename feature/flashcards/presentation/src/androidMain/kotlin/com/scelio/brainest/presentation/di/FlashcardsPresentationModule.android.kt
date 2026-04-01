package com.scelio.brainest.presentation.di

import com.scelio.brainest.presentation.audio.AndroidAudioRecorder
import com.scelio.brainest.presentation.audio.AudioRecorder
import org.koin.core.module.Module
import org.koin.dsl.module

actual val platformFlashcardsPresentationModule: Module = module {
    single<AudioRecorder> { AndroidAudioRecorder() }
}
