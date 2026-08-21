package com.ejemplo.examplelogin.navegacion

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.ejemplo.examplelogin.data.model.EstadoSesion
import com.ejemplo.examplelogin.screens.login.PantallaLogin
import com.ejemplo.examplelogin.screens.sesion.PantallaCargando
import com.ejemplo.examplelogin.screens.sesion.SesionViewModel
import com.ejemplo.examplelogin.screens.token.PantallaToken

// ─── navegacion/NavegacionApp.kt ─────────────────────────────────────────────────────────────────
@Composable
fun NavegacionApp(
    sesionViewModel: SesionViewModel = viewModel(factory = SesionViewModel.Factory)
) {
    val estado by sesionViewModel.estado.collectAsStateWithLifecycle()

    // Mientras DataStore no entregue su primer valor, no sabemos qué mostrar.
    when (val estadoActual = estado) {
        EstadoSesion.Comprobando -> PantallaCargando()
        else -> GrafoNavegacion(estadoSesion = estadoActual)
    }
}

@Composable
private fun GrafoNavegacion(estadoSesion: EstadoSesion) {
    val navController = rememberNavController()

    // El destino inicial se fija UNA sola vez: al entrar aquí el estado
    // "Comprobando" ya está resuelto, así que sabemos si hay sesión.
    val destinoInicial: Any = remember {
        if (estadoSesion is EstadoSesion.Activa) RutaToken else RutaLogin
    }

    // A partir de ese momento, cualquier cambio de sesión —login, logout o
    // caducidad— redirige automáticamente.
    LaunchedEffect(estadoSesion) {
        val destino: Any = if (estadoSesion is EstadoSesion.Activa) RutaToken else RutaLogin
        val yaEstamosAhi = navController.currentDestination?.hasRoute(destino::class) == true
        if (!yaEstamosAhi) {
            navController.navigate(destino) {
                // popUpTo(0) vacía la pila por completo: tras un cambio de
                // sesión no debe quedar ninguna pantalla anterior accesible
                // con el botón "atrás".
                popUpTo(0) { inclusive = true }
            }
        }
    }

    NavHost(navController = navController, startDestination = destinoInicial) {
        composable<RutaLogin> { PantallaLogin() }
        composable<RutaToken> { PantallaToken() }
    }
}