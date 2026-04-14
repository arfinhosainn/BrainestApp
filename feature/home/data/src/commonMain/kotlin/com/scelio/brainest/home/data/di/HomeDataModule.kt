package com.scelio.brainest.home.data.di

import com.scelio.brainest.flashcards.database.StudyDao
import com.scelio.brainest.home.data.WeeklyPointsRepositoryImpl
import com.scelio.brainest.home.domain.WeeklyPointsRepository
import org.koin.dsl.module

val homeDataModule = module {
    single<WeeklyPointsRepository> {
        WeeklyPointsRepositoryImpl(
            supabase = get(),
            logger = get(),
            studyDao = get()
        )
    }
}
