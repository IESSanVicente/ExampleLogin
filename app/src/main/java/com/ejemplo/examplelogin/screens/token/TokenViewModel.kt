package com.ejemplo.examplelogin.screens.token

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.ejemplo.examplelogin.ExampleLoginApplication
import com.ejemplo.examplelogin.data.model.Sesion
import com.ejemplo.examplelogin.data.repository.AutenticacionRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.milliseconds

// ─── screens/token/TokenViewModel.kt ─────────────────────────────────────────────────────────────
@OptIn(ExperimentalCoroutinesApi::class)
class TokenViewModel(private val repositorio: AutenticacionRepository) : ViewModel() {

    /**
     * Estado de la pantalla: los datos de la sesión más una cuenta atrás
     * que se recalcula cada segundo.
     *
     * flatMapLatest cancela la cuenta atrás anterior cada vez que cambia la
     * sesión, evitando que queden corrutinas huérfanas actualizando el estado.
     */
    val uiState: StateFlow<TokenUiState> = repositorio.sesion
        .flatMapLatest { sesion -> cuentaAtras(sesion) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = TokenUiState()
        )

    init {
        // Vigilante de caducidad: espera exactamente hasta el instante `exp`
        // y entonces borra la sesión. Al escribir en DataStore, el flujo de
        // sesión emite null y el grafo de navegación devuelve al login.
        viewModelScope.launch {
            repositorio.sesion.collectLatest { sesion ->
                val caducidad = sesion?.caducaEnMs ?: return@collectLatest
                val esperaMs = caducidad - System.currentTimeMillis()
                if (esperaMs > 0) delay(esperaMs)
                repositorio.cerrarSesion()
            }
        }
    }

    /** Logout explícito del usuario. */
    fun cerrarSesion() {
        viewModelScope.launch { repositorio.cerrarSesion() }
    }

    /**
     * Emite un TokenUiState por segundo con el tiempo restante actualizado.
     * Si el token no declara caducidad, emite una sola vez y termina.
     */
    private fun cuentaAtras(sesion: Sesion?): Flow<TokenUiState> = flow {
        if (sesion == null) {
            emit(TokenUiState())
            return@flow
        }
        val caducidad = sesion.caducaEnMs
        if (caducidad == null) {
            emit(TokenUiState(token = sesion.token))
            return@flow
        }
        while (true) {
            val restantes = ((caducidad - System.currentTimeMillis()) / 1_000).coerceAtLeast(0L)
            emit(
                TokenUiState(
                    token = sesion.token,
                    caducaEnMs = caducidad,
                    segundosRestantes = restantes
                )
            )
            if (restantes <= 0L) break
            delay(1_000.milliseconds)
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val aplicacion = this[APPLICATION_KEY] as ExampleLoginApplication
                TokenViewModel(aplicacion.contenedor.autenticacionRepository)
            }
        }
    }
}