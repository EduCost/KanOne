package com.educost.kanone

import android.app.Application
import com.educost.kanone.domain.logs.LogHandler
import dagger.hilt.android.HiltAndroidApp
import jakarta.inject.Inject

@HiltAndroidApp
class App: Application() {

    @Inject
    lateinit var logHandler: LogHandler

    override fun onCreate() {
        super.onCreate()
        logHandler.captureUncaughtExceptions()
    }

}