package com.ejemplo.examplelogin.data.repository

import com.ejemplo.examplelogin.data.datasource.local.SesionLocalDataSource
import com.ejemplo.examplelogin.data.datasource.remote.RemoteDataSource
import com.ejemplo.examplelogin.data.model.ResultadoLogin
import com.ejemplo.examplelogin.data.model.Sesion
import com.ejemplo.examplelogin.utils.Jwt
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

// ─── data/repository/AutenticacionRepository.kt ──────────────────────────────────────────────────
class AutenticacionRepository(
    private val remoto: RemoteDataSource,
    private val local: SesionLocalDataSource
) {

    /**
     * Sesión actual de la aplicación: única fuente de verdad.
     *
     * Emite null cuando no hay token guardado o cuando el que hay ya ha
     * caducado; en caso contrario, emite la sesión con su fecha de caducidad.
     */
    val sesion: Flow<Sesion?> = local.token.map { token ->
        when {
            token == null -> null
            Jwt.haCaducado(token) -> null
            else -> Sesion(token = token, caducaEnMs = Jwt.caducidadMs(token))
        }
    }

    /**
     * Intenta autenticar al usuario. Si tiene éxito, persiste el token:
     * la escritura hace que `sesion` emita automáticamente la nueva sesión.
     */
    suspend fun login(usuario: String, password: String): ResultadoLogin {
        val resultado = remoto.login(usuario.trim(), password)
        if (resultado is ResultadoLogin.Exito) {
            local.guardarToken(resultado.token)
        }
        return resultado
    }

    /**
     * Cierra la sesión. Se usa tanto para el logout explícito del usuario
     * como para descartar un token caducado.
     */
    suspend fun cerrarSesion() {
        local.borrarSesion()
    }
}