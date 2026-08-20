package com.ejemplo.examplelogin.data.model

/**
 * Representa una sesión activa.
 *
 * @param token cadena JWT completa devuelta por la API.
 * @param caducaEnMs instante de caducidad en milisegundos (época Unix),
 *                   o null si el token no contiene un claim `exp` legible.
 */
// ─── data/model/Sesion.kt ────────────────────────────────────────────────────────────────────────
data class Sesion(
    val token: String,
    val caducaEnMs: Long?
)