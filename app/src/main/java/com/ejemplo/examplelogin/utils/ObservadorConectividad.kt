package com.ejemplo.examplelogin.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.conflate
import kotlinx.coroutines.flow.distinctUntilChanged

// ─── utils/ObservadorConectividad.kt ─────────────────────────────────────────────────────────────
class ObservadorConectividad(context: Context) {

    private val gestor = context.getSystemService(ConnectivityManager::class.java)

    /**
     * Flujo que emite true cuando hay una red conectada y VALIDADA
     * (con salida real a internet), y false en caso contrario.
     *
     * callbackFlow adapta una API basada en callbacks a un Flow de corrutinas:
     * registra el callback al empezar a recolectar y lo libera en awaitClose.
     */
    val estado: Flow<Boolean> = callbackFlow {
        val callback = object : ConnectivityManager.NetworkCallback() {

            override fun onCapabilitiesChanged(red: Network, caps: NetworkCapabilities) {
                trySend(caps.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED))
            }

            override fun onLost(red: Network) { trySend(false) }
            override fun onUnavailable() { trySend(false) }
        }

        val peticion = NetworkRequest.Builder()
            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            .build()

        gestor.registerNetworkCallback(peticion, callback)
        trySend(hayConexion())          // valor inicial

        awaitClose { gestor.unregisterNetworkCallback(callback) }
    }.distinctUntilChanged().conflate()

    /** Comprobación puntual y síncrona del estado de la red. */
    fun hayConexion(): Boolean {
        val red = gestor.activeNetwork ?: return false
        val caps = gestor.getNetworkCapabilities(red) ?: return false
        return caps.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
    }
}