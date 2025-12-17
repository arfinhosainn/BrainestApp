package com.scelio.brainest.data.di

import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import com.scelio.brainest.data.chat.ChatRepositoryImpl
import com.scelio.brainest.data.chat.OpenAIApiService
import com.scelio.brainest.data.chat.OpenAIApiServiceImpl
import com.scelio.brainest.database.BrainestChatDatabase
import com.scelio.brainest.database.ChatDao
import com.scelio.brainest.database.DatabaseFactory
import com.scelio.brainest.domain.chat.ChatRepository
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.core.qualifier.named
import org.koin.dsl.bind
import org.koin.dsl.module

expect val platformChatDataModule: Module


val chatDataModule = module {
    includes(platformChatDataModule)



     single(named("openai_api_key")) { 
            "your-openai-api-key-here" // TODO: Move to BuildConfig or secure storage
        }
    single<OpenAIApiService> { 
            OpenAIApiServiceImpl(
                httpClient = get(),
                apiKey = get(named("openai_api_key"))
                // baseUrl uses default value
            ) 
        }

    // 1. First: Create the database instanceass
    single<BrainestChatDatabase> {
        get<DatabaseFactory>()
            .create()
            .setDriver(BundledSQLiteDriver())
            .build()
    }

    // 2. Then: Get ChatDao from the database
    single<ChatDao> {
        get<BrainestChatDatabase>().chatDao()
    }

    // 3. Finally: Create the repository (which needs ChatDao)
    singleOf(::ChatRepositoryImpl) bind ChatRepository::class


}