package com.ejemplo.examplelogin.utils

import android.util.Base64
import org.json.JSONObject

/**
 * Utilidades para leer (que NO validar) un token JWT en el cliente.
 */
// ─── utils/Jwt.kt ────────────────────────────────────────────────────────────────────────────────
object Jwt {

    /**
     * Extrae el claim `exp` del payload y lo devuelve en milisegundos.
     *
     * @return instante de caducidad en ms (época Unix), o null si el token
     *         no tiene el formato esperado o carece del claim `exp`.
     */
    fun caducidadMs(token: String): Long? {
        return try {
            val partes = token.split(".")
            if (partes.size < 2) return null

            // El payload viaja en Base64 URL-safe y normalmente sin relleno '='.
            val bytes = Base64.decode(
                partes[1],
                Base64.URL_SAFE or Base64.NO_WRAP or Base64.NO_PADDING
            )
            val payload = JSONObject(String(bytes, Charsets.UTF_8))

            // optLong devuelve 0 si el claim no existe, en lugar de lanzar excepción.
            val expSegundos = payload.optLong("exp", 0L)
            if (expSegundos <= 0L) null else expSegundos * 1_000L
        } catch (e: Exception) {
            null
        }
    }

    /**
     * Indica si el token ya ha caducado.
     *
     * @param margenMs margen de seguridad: se considera caducado un poco antes
     *                 de tiempo para evitar que expire justo durante una petición.
     * @return false si el token no declara caducidad (no podemos afirmar que haya caducado).
     */
    fun haCaducado(token: String, margenMs: Long = 30_000L): Boolean {
        val caducidad = caducidadMs(token) ?: return false
        return System.currentTimeMillis() >= caducidad - margenMs
    }
}