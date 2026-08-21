package com.ejemplo.examplelogin

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.ejemplo.examplelogin.navegacion.NavegacionApp
import com.ejemplo.examplelogin.ui.theme.ExampleLoginTheme

// ─── MainActivity.kt ─────────────────────────────────────────────────────────────────────────────
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Ya no se comprueba la sesión aquí: DataStore es asíncrono y la
        // comprobación la resuelve SesionViewModel dentro de NavegacionApp.
        setContent {
            ExampleLoginTheme {
                NavegacionApp()
            }
        }
    }
}