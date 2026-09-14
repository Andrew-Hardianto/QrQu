package com.drew.qrqu

import android.app.Application
import com.drew.qrqu.di.AppContainer
import com.drew.qrqu.di.DefaultAppContainer

class QrQuApplication : Application() {
    lateinit var container: AppContainer

    override fun onCreate() {
        super.onCreate()
        container = DefaultAppContainer(this)
    }
}

