package com.scelio.brainest.data.di

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.scelio.brainest.database.preference.DATASTORE_ONBOARDING
import com.scelio.brainest.database.preference.createDataStore
import kotlinx.cinterop.ExperimentalForeignApi
import org.koin.dsl.module
import platform.Foundation.NSDocumentDirectory
import platform.Foundation.NSFileManager
import platform.Foundation.NSUserDomainMask

actual val platformOnboardingDataModule = module {
    single<DataStore<Preferences>> {
        val path = documentDirectory() + "/$DATASTORE_ONBOARDING"
        createDataStore { path }
    }
}

@OptIn(ExperimentalForeignApi::class)
private fun documentDirectory(): String {
    val documentDirectory = NSFileManager.defaultManager.URLForDirectory(
        directory = NSDocumentDirectory,
        inDomain = NSUserDomainMask,
        appropriateForURL = null,
        create = false,
        error = null
    )

    return requireNotNull(documentDirectory?.path)
}
