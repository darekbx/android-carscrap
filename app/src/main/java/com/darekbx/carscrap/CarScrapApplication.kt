package com.darekbx.carscrap

import android.app.Application
import com.darekbx.carscrap.di.appModule
import com.google.firebase.FirebaseApp
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.GlobalContext.startKoin

class CarScrapApplication: Application() {

    override fun onCreate() {
        super.onCreate()

        FirebaseApp.initializeApp(this)

        startKoin {
            androidLogger()
            androidContext(this@CarScrapApplication)
            modules(appModule)
        }
    }
}