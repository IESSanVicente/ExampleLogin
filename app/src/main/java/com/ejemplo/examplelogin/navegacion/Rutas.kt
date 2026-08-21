package com.ejemplo.examplelogin.navegacion

import kotlinx.serialization.Serializable

/**
 * Rutas type-safe de Navigation Compose (T3). Al ser objetos serializables,
 * el compilador verifica los destinos: no hay cadenas mágicas que se puedan
 * escribir mal.
 */
// ─── navegacion/Rutas.kt ─────────────────────────────────────────────────────────────────────────
@Serializable
object RutaLogin

@Serializable
object RutaToken