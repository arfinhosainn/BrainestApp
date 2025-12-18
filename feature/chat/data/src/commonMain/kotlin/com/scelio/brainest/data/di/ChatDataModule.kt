package com.scelio.brainest.data.di

import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import com.scelio.brainest.data.chat.ChatRepositoryImpl
import com.scelio.brainest.data.chat.OpenAIApiService
import com.scelio.brainest.data.chat.OpenAIApiServiceImpl
import com.scelio.brainest.data.chat.SupabaseChatServiceImpl
import com.scelio.brainest.database.BrainestChatDatabase
import com.scelio.brainest.database.ChatDao
import com.scelio.brainest.database.DatabaseFactory
import com.scelio.brainest.domain.chat.ChatRepository
import com.scelio.brainest.domain.chat.SupabaseChatService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.SupervisorJob
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.core.qualifier.named
import org.koin.dsl.bind
import org.koin.dsl.module

expect val platformChatDataModule: Module


val chatDataModule = module {
    includes(platformChatDataModule)

    // API Key
    single(named("openai_api_key")) {
        "your-openai-api-key-here"
    }

    // OpenAI Service
    single<OpenAIApiService> {
        OpenAIApiServiceImpl(
            httpClient = get(),
            apiKey = get(named("openai_api_key"))
        )
    }

    // Supabase Service (used internally by repository)
    single<SupabaseChatService> {
        SupabaseChatServiceImpl(
            supabase = get() // ← Make sure you have SupabaseClient defined
        )
    }

    // CoroutineScope for background sync
    single<CoroutineScope> {
        CoroutineScope(SupervisorJob() + Dispatchers.IO)
    }

    // Database
    single<BrainestChatDatabase> {
        get<DatabaseFactory>()
            .create()
            .setDriver(BundledSQLiteDriver())
            .build()
    }

    // ChatDao
    single<ChatDao> {
        get<BrainestChatDatabase>().chatDao()
    }

    // Repository - all 4 dependencies satisfied ✅
    single<ChatRepository> {
        ChatRepositoryImpl(
            chatDao = get(),              // ✅
            openAI = get(),               // ✅
            supabaseService = get(),      // ✅
            coroutineScope = get()        // ✅
        )
    }
}