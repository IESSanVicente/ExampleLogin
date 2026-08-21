package com.ejemplo.examplelogin.screens.sesion

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

// ─── screens/sesion/PantallaCargando.kt ──────────────────────────────────────────────────────────
/** Se muestra mientras DataStore entrega su primer valor. */
@Composable
fun PantallaCargando(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}