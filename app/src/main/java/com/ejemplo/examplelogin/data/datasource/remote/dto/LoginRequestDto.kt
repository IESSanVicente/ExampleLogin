package com.ejemplo.examplelogin.data.datasource.remote.dto

/**
 * Cuerpo de la petición POST /login.
 * Los nombres de las propiedades DEBEN coincidir con los del JSON que espera la API.
 */
// ─── data/datasource/remote/dto/LoginRequestDto.kt ───────────────────────────────────────────────
data class LoginRequestDto(
    val usuario: String,
    val password: String
)