package com.ejemplo.examplelogin.screens.sesion

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.ejemplo.examplelogin.ExampleLoginApplication
import com.ejemplo.examplelogin.data.model.EstadoSesion
import com.ejemplo.examplelogin.data.repository.AutenticacionRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

/**
 * ViewModel de ámbito de aplicación: traduce el flujo del repositorio al
 * estado que consume el grafo de navegación.
 */
// ─── screens/sesion/SesionViewModel.kt ───────────────────────────────────────────────────────────
class SesionViewModel(repositorio: AutenticacionRepository) : ViewModel() {

    val estado: StateFlow<EstadoSesion> = repositorio.sesion
        .map { sesion ->
            if (sesion == null) EstadoSesion.NoAutenticada
            else EstadoSesion.Activa(sesion)
        }
        .stateIn(
            scope = viewModelScope,
            // Mantiene la suscripción 5 s tras perder el último recolector,
            // para no reiniciar la lectura ante un cambio de configuración.
            started = SharingStarted.WhileSubscribed(5_000),
            // Valor inicial: aún no sabemos nada.
            initialValue = EstadoSesion.Comprobando
        )

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val aplicacion = this[APPLICATION_KEY] as ExampleLoginApplication
                SesionViewModel(aplicacion.contenedor.autenticacionRepository)
            }
        }
    }
}