package com.scelio.brainest.data.di

import com.scelio.brainest.data.auth.SupabaseAuthService
import com.scelio.brainest.data.logging.KermitLogger
import com.scelio.brainest.data.networking.HttpClientFactory
import com.scelio.brainest.domain.auth.AuthService
import com.scelio.brainest.domain.logging.BrainestLogger
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.auth.FlowType
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.logging.LogLevel
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.serializer.KotlinXSerializer
import kotlinx.serialization.json.Json
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module
import kotlin.time.Duration.Companion.seconds

expect val platformCoreDataModule: Module

val coreDataModule = module {
    includes(platformCoreDataModule)
    single<BrainestLogger> { KermitLogger }
    single {
        HttpClientFactory(get()).create(get())
    }
    singleOf(::SupabaseAuthService) bind AuthService::class

    single {
        createSupabaseClient (
            supabaseUrl = "https://ezhmtxiqaalkhngvshda.supabase.co",
            supabaseKey = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6ImV6aG10eGlxYWFsa2huZ3ZzaGRhIiwicm9sZSI6ImFub24iLCJpYXQiOjE3NjM4OTg2MjEsImV4cCI6MjA3OTQ3NDYyMX0.IoUrlWhuiVwALa-SmsOid-I7VZx9h0DZSq-azcyu-2s"
        ) {
            defaultLogLevel = LogLevel.DEBUG
            requestTimeout = 60.seconds // 60 seconds
            install(Auth) {
                flowType = FlowType.PKCE
                autoSaveToStorage = true
                autoLoadFromStorage = true
                alwaysAutoRefresh = true

            }
            install(Postgrest)
            defaultSerializer = KotlinXSerializer(Json {
                ignoreUnknownKeys = true
                encodeDefaults = true
            })
        }
    }
}