package com.example.jarvis

import android.app.Application
import com.example.jarvis.network.WsServerHolder

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        WsServerHolder.ensureStarted(8081)
    }
}