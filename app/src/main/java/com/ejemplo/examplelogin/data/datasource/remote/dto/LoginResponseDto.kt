package com.ejemplo.examplelogin.data.datasource.remote.dto

/**
 * Respuesta de POST /login.
 * El token se declara nullable porque, si la API cambiase el nombre del campo,
 * Gson lo dejaría a null en lugar de lanzar una excepción.
 */
// ─── data/datasource/remote/dto/LoginResponseDto.kt ──────────────────────────────────────────────
data class LoginResponseDto(
    val token: String?
)