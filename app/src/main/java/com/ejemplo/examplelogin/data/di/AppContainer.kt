package com.ejemplo.examplelogin.data.di

import android.content.Context
import com.ejemplo.examplelogin.data.datasource.local.SesionLocalDataSource
import com.ejemplo.examplelogin.data.datasource.remote.RemoteDataSource
import com.ejemplo.examplelogin.data.repository.AutenticacionRepository
import com.ejemplo.examplelogin.utils.ObservadorConectividad

/**
 * Contenedor manual de dependencias: crea una única instancia de cada
 * colaborador y la comparte con toda la aplicación. Sustituye a Hilt/Koin
 * en proyectos de este tamaño.
 */
// ─── data/di/AppContainer.kt ─────────────────────────────────────────────────────────────────────
class AppContainer(context: Context) {

    private val remoteDataSource = RemoteDataSource()
    private val sesionLocalDataSource = SesionLocalDataSource(context)

    val autenticacionRepository: AutenticacionRepository =
        AutenticacionRepository(remoteDataSource, sesionLocalDataSource)

    val observadorConectividad: ObservadorConectividad =
        ObservadorConectividad(context)
}