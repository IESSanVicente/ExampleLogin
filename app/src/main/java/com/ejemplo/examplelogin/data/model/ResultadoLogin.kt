package com.ejemplo.examplelogin.data.model

/**
 * Resultado de un intento de autenticación.
 * Al ser una interfaz sellada, el compilador obliga a tratar todos los casos
 * en los `when` que la consumen: si mañana añadimos un caso nuevo, el
 * compilador señalará todos los puntos que hay que revisar.
 */
// ─── data/model/ResultadoLogin.kt ────────────────────────────────────────────────────────────────
sealed interface ResultadoLogin {
    data class Exito(val token: String) : ResultadoLogin
    data object CredencialesIncorrectas : ResultadoLogin
    data object DatosIncompletos : ResultadoLogin
    data object SinConexion : ResultadoLogin
    data class ErrorServidor(val codigo: Int) : ResultadoLogin
    data object RespuestaInesperada : ResultadoLogin
}