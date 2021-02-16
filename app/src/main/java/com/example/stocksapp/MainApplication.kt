package com.example.stocksapp

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber

@HiltAndroidApp
class MainApplication : Application(){
    override fun onCreate() {
        super.onCreate()

        // TODO figure out why timber doesn't work
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }

        Timber.d("AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA")
    }
}