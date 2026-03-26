package com.example.joopjoop

import android.app.Application
import com.example.joopjoop.core.di.AppContainer

class JoopJoopApplication : Application() {
    // 앱이 살아있는 동안 단 하나만 존재할 창고
    lateinit var container: AppContainer

    override fun onCreate() {
        super.onCreate()
        container = AppContainer()
    }
}