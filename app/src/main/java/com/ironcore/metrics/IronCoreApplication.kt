package com.ironcore.metrics

import android.app.Application
import com.ironcore.metrics.data.health.VitalsMonitorWorker
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class IronCoreApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        VitalsMonitorWorker.enqueue(this)
    }
}
