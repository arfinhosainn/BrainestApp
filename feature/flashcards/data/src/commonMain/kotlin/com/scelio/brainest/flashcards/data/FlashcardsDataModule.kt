package com.scelio.brainest.flashcards.data

import com.plcoding.feature.flashcards.data.BuildKonfig
import com.scelio.brainest.domain.logging.BrainestLogger
import com.scelio.brainest.flashcards.domain.AudioTranscriptionService
import com.scelio.brainest.flashcards.domain.FlashcardsGenerationService
import com.scelio.brainest.flashcards.domain.FlashcardsRepository
import org.koin.core.module.Module
import org.koin.core.qualifier.named
import org.koin.dsl.module

expect val platformFlashcardsDataModule: Module

val flashcardsDataModule = module {
    includes(platformFlashcardsDataModule)
    single(named("deepinfra_api_key")) {
        BuildKonfig.DEEPINFRA_API_KEY
    }
    single<FlashcardsGenerationService> {
        FlashcardsGenerationServiceImpl(
            httpClient = get(),
            apiKey = get(named("openai_api_key"))
        )
    }
    single<AudioTranscriptionService> {
        AudioTranscriptionServiceImpl(
            httpClient = get(),
            apiKey = get(named("deepinfra_api_key"))
        )
    }
    single<FlashcardsRepository> {
        FlashcardsRepositoryImpl(
            supabase = get(),
            logger = get<BrainestLogger>()
        )
    }
}
