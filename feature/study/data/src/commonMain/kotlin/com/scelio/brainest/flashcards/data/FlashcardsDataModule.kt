package com.scelio.brainest.flashcards.data

import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import com.plcoding.feature.study.data.BuildKonfig
import com.scelio.brainest.domain.logging.BrainestLogger
import com.scelio.brainest.flashcards.database.BrainestStudyDatabase
import com.scelio.brainest.flashcards.database.DatabaseFactory
import com.scelio.brainest.flashcards.database.StudyDao
import com.scelio.brainest.flashcards.domain.AudioTranscriptionService
import com.scelio.brainest.flashcards.domain.DocumentTranscriptionService
import com.scelio.brainest.flashcards.domain.FlashcardsGenerationService
import com.scelio.brainest.flashcards.domain.FlashcardsRepository
import com.scelio.brainest.flashcards.domain.OpenAiFileService
import com.scelio.brainest.flashcards.domain.SmartNotesGenerationService
import com.scelio.brainest.quiz.domain.QuizGenerationService
import com.scelio.brainest.quiz.domain.QuizRepository
import com.scelio.brainest.quiz.data.QuizGenerationServiceImpl
import com.scelio.brainest.quiz.data.QuizRepositoryImpl
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.SupervisorJob
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
    single<QuizGenerationService> {
        QuizGenerationServiceImpl(
            httpClient = get(),
            apiKey = get(named("openai_api_key"))
        )
    }
    single<OpenAiFileService> {
        OpenAiFileServiceImpl(
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
    single<DocumentTranscriptionService> {
        DocumentTranscriptionServiceImpl(
            httpClient = get(),
            apiKey = get(named("openai_api_key"))
        )
    }
    single<SmartNotesGenerationService> {
        SmartNotesGenerationServiceImpl(
            httpClient = get(),
            apiKey = get(named("openai_api_key"))
        )
    }
    single<CoroutineScope> {
        CoroutineScope(SupervisorJob() + Dispatchers.IO)
    }
    single<BrainestStudyDatabase> {
        get<DatabaseFactory>()
            .create()
            .setDriver(BundledSQLiteDriver())
            .build()
    }
    single<StudyDao> {
        get<BrainestStudyDatabase>().studyDao()
    }
    single<FlashcardsRepository> {
        FlashcardsRepositoryImpl(
            supabase = get(),
            logger = get<BrainestLogger>(),
            studyDao = get(),
            coroutineScope = get(),
            quizRepository = get()
        )
    }
    single<QuizRepository> {
        QuizRepositoryImpl(
            supabase = get(),
            logger = get<BrainestLogger>(),
            studyDao = get(),
            coroutineScope = get()
        )
    }
}
