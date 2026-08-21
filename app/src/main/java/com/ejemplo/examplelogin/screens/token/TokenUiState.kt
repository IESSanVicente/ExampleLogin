package com.ejemplo.examplelogin.screens.token

// ─── screens/token/TokenUiState.kt ───────────────────────────────────────────────────────────────
data class TokenUiState(
    val token: String = "",
    val caducaEnMs: Long? = null,
    val segundosRestantes: Long? = null
)