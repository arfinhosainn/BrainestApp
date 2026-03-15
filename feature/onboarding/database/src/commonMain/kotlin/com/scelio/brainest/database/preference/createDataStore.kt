package com.scelio.brainest.database.preference

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import okio.Path.Companion.toPath

fun createDataStore(productPath: () -> String): DataStore<Preferences> {
    return PreferenceDataStoreFactory.createWithPath(
        produceFile = { productPath().toPath() }
    )
}

const val DATASTORE_ONBOARDING = "onboarding.preferences_pb"
