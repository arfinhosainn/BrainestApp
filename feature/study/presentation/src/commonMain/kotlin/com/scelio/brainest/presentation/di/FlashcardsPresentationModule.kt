package com.scelio.brainest.presentation.di

import com.scelio.brainest.presentation.audio.AudioRecordingViewModel
import com.scelio.brainest.presentation.flashcards.FlashcardsSessionViewModel
import com.scelio.brainest.presentation.quiz.QuizSessionViewModel
import com.scelio.brainest.presentation.studysets.StudySetDetailViewModel
import com.scelio.brainest.presentation.studysets.StudySetsViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.core.module.Module
import org.koin.dsl.module

expect val platformFlashcardsPresentationModule: Module

val flashcardsPresentationModule = module {
    includes(platformFlashcardsPresentationModule)
    viewModelOf(::FlashcardsSessionViewModel)
    viewModelOf(::AudioRecordingViewModel)
    viewModelOf(::StudySetsViewModel)
    viewModelOf(::StudySetDetailViewModel)
    viewModelOf(::QuizSessionViewModel)
}
