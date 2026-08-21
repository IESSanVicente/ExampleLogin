package com.ejemplo.examplelogin.screens.login

// ─── screens/login/LoginUiState.kt ───────────────────────────────────────────────────────────────
data class LoginUiState(
    val usuario: String = "",
    val password: String = "",
    val cargando: Boolean = false,
    val mensajeError: String? = null,
    val hayConexion: Boolean = true
) {
    /** El botón solo se habilita si ambos campos tienen contenido. */
    val formularioValido: Boolean
        get() = usuario.isNotBlank() && password.isNotBlank()
}