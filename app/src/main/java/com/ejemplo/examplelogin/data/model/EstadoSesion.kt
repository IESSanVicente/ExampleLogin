package com.ejemplo.examplelogin.data.model

/**
 * Estado de la sesión desde el punto de vista de la interfaz.
 *
 * `Comprobando` es el estado inicial: DataStore todavía no ha entregado
 * su primer valor y la aplicación aún no sabe si hay sesión guardada.
 */
// ─── data/model/EstadoSesion.kt ──────────────────────────────────────────────────────────────────
sealed interface EstadoSesion {
    data object Comprobando : EstadoSesion
    data object NoAutenticada : EstadoSesion
    data class Activa(val sesion: Sesion) : EstadoSesion
}