package com.ejemplo.examplelogin.screens.token

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLocale
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import java.text.SimpleDateFormat
import java.util.Date

// ─── screens/token/PantallaToken.kt ──────────────────────────────────────────────────────────────
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaToken(
    modifier: Modifier = Modifier,
    viewModel: TokenViewModel = viewModel(factory = TokenViewModel.Factory)
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = { TopAppBar(title = { Text("Sesión iniciada") }) },
        modifier = modifier
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Text("Token recibido", style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(8.dp))

            // SelectionContainer permite copiar el token con una pulsación larga.
            SelectionContainer {
                Card(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = uiState.token,
                        style = MaterialTheme.typography.bodySmall,
                        fontFamily = FontFamily.Monospace,
                        modifier = Modifier.padding(12.dp)
                    )
                }
            }

            Spacer(Modifier.height(24.dp))

            Text("Caducidad", style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(8.dp))

            val caducidad = uiState.caducaEnMs
            if (caducidad == null) {
                Text("El token no declara fecha de caducidad (claim `exp`).")
            } else {
                val formato =
                    SimpleDateFormat("dd/MM/yyyy HH:mm:ss", LocalLocale.current.platformLocale)
                Text("Caduca el: ${formato.format(Date(caducidad))}")
                uiState.segundosRestantes?.let { restantes ->
                    Text("Tiempo restante: ${formatearDuracion(restantes)}")
                }
            }

            Spacer(Modifier.height(32.dp))

            Button(
                onClick = viewModel::cerrarSesion,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.error
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Cerrar sesión")
            }
        }
    }
}

/** Convierte segundos en un texto del tipo "1 h 03 min 20 s". */
private fun formatearDuracion(segundos: Long): String {
    val horas = segundos / 3600
    val minutos = (segundos % 3600) / 60
    val resto = segundos % 60
    return buildString {
        if (horas > 0) append("$horas h ")
        if (horas > 0 || minutos > 0) append("%02d min ".format(minutos))
        append("%02d s".format(resto))
    }
}