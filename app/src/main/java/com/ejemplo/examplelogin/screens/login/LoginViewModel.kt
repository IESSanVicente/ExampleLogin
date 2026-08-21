package com.ejemplo.examplelogin.screens.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.ejemplo.examplelogin.ExampleLoginApplication
import com.ejemplo.examplelogin.data.model.ResultadoLogin
import com.ejemplo.examplelogin.data.repository.AutenticacionRepository
import com.ejemplo.examplelogin.utils.ObservadorConectividad
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

// ─── screens/login/LoginViewModel.kt ─────────────────────────────────────────────────────────────
class LoginViewModel(
    private val repositorio: AutenticacionRepository,
    observadorConectividad: ObservadorConectividad
) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    init {
        // Mantiene el estado de conexión sincronizado mientras vive el ViewModel.
        viewModelScope.launch {
            observadorConectividad.estado.collect { conectado ->
                _uiState.update { it.copy(hayConexion = conectado) }
            }
        }
    }

    fun alCambiarUsuario(valor: String) {
        _uiState.update { it.copy(usuario = valor, mensajeError = null) }
    }

    fun alCambiarPassword(valor: String) {
        _uiState.update { it.copy(password = valor, mensajeError = null) }
    }

    fun iniciarSesion() {
        val estado = _uiState.value
        if (!estado.formularioValido || estado.cargando) return

        if (!estado.hayConexion) {
            _uiState.update { it.copy(mensajeError = "Sin conexión a internet.") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(cargando = true, mensajeError = null) }

            when (val resultado = repositorio.login(estado.usuario, estado.password)) {
                // No se navega desde aquí: al guardarse el token, el flujo de
                // sesión emite y el grafo de navegación redirige solo.
                is ResultadoLogin.Exito ->
                    _uiState.update { it.copy(cargando = false) }

                ResultadoLogin.CredencialesIncorrectas ->
                    mostrarError("Usuario o contraseña incorrectos.")

                ResultadoLogin.DatosIncompletos ->
                    mostrarError("Debes rellenar usuario y contraseña.")

                ResultadoLogin.SinConexion ->
                    mostrarError("No se ha podido contactar con el servidor. Revisa tu conexión.")

                is ResultadoLogin.ErrorServidor ->
                    mostrarError("Error del servidor (${resultado.codigo}). Inténtalo más tarde.")

                ResultadoLogin.RespuestaInesperada ->
                    mostrarError("Respuesta inesperada del servidor.")
            }
        }
    }

    private fun mostrarError(mensaje: String) {
        _uiState.update { it.copy(cargando = false, mensajeError = mensaje) }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val aplicacion = this[APPLICATION_KEY] as ExampleLoginApplication
                LoginViewModel(
                    repositorio = aplicacion.contenedor.autenticacionRepository,
                    observadorConectividad = aplicacion.contenedor.observadorConectividad
                )
            }
        }
    }
}