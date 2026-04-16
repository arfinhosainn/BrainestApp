package com.scelio.brainest.home.data.di

import com.scelio.brainest.home.data.AchievementsRepositoryImpl
import com.scelio.brainest.home.data.WeeklyPointsRepositoryImpl
import com.scelio.brainest.home.domain.AchievementsRepository
import com.scelio.brainest.home.domain.WeeklyPointsRepository
import org.koin.dsl.module

val homeDataModule = module {
    single<AchievementsRepository> {
        AchievementsRepositoryImpl(
            supabase = get(),
            logger = get(),
            studyDao = get()
        )
    }

    single<WeeklyPointsRepository> {
        WeeklyPointsRepositoryImpl(
            supabase = get(),
            logger = get(),
            studyDao = get()
        )
    }
}
