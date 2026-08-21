package com.ejemplo.examplelogin

import android.app.Application
import com.ejemplo.examplelogin.data.di.AppContainer

// ─── ExampleLoginApplication.kt ──────────────────────────────────────────────────────────────────
class ExampleLoginApplication : Application() {

    lateinit var contenedor: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()
        // applicationContext evita retener el contexto de una Activity concreta.
        contenedor = AppContainer(applicationContext)
    }
}