package com.scellio.brainest

import android.app.Application
import com.scellio.brainest.di.initKoin
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger

class BrainestApplication: Application() {

    override fun onCreate() {
        super.onCreate()
        initKoin {
            androidContext(this@BrainestApplication)
            androidLogger()
        }
    }
}