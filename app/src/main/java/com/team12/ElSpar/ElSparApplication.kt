package com.team12.ElSpar

import android.app.Application

class ElSparApplication : Application() {
    lateinit var container: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()
        container = DefaultAppContainer(applicationContext)
    }

    override fun onTerminate() {
        super.onTerminate()
        container.model.close()
    }
}