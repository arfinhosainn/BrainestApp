package com.scelio.brainest.data.di

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.scelio.brainest.database.preference.DATASTORE_ONBOARDING
import com.scelio.brainest.database.preference.createDataStore
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

actual val platformOnboardingDataModule = module {
    single<DataStore<Preferences>> {
        createDataStore {
            androidContext().filesDir.resolve(DATASTORE_ONBOARDING).absolutePath
        }
    }
}
