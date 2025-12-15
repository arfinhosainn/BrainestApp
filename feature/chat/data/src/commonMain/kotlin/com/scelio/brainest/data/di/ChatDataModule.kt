package com.scelio.brainest.data.di

import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import com.scelio.brainest.data.chat.OpenAIApiService
import com.scelio.brainest.data.chat.OpenAIApiServiceImpl
import com.scelio.brainest.database.DatabaseFactory
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

expect val platformChatDataModule: Module


val chatDataModule = module {
    includes(platformChatDataModule)

    singleOf(::OpenAIApiServiceImpl) bind OpenAIApiService::class
    single {
        get<DatabaseFactory>()
            .create()
            .setDriver(BundledSQLiteDriver())
            .build()
    }


}