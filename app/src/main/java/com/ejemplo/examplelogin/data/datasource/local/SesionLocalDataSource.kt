package com.ejemplo.examplelogin.data.datasource.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

// ─── data/datasource/local/SesionLocalDataSource.kt ──────────────────────────────────────────────

/**
 * Delegado que crea el DataStore. DEBE declararse a nivel de fichero (top level):
 * garantiza que exista una única instancia por nombre de fichero en todo el proceso.
 * Crear dos DataStore sobre el mismo fichero provoca una IllegalStateException.
 *
 * El fichero resultante es: /data/data/<paquete>/files/datastore/sesion_login.preferences_pb
 */
private val Context.dataStoreSesion: DataStore<Preferences> by preferencesDataStore(
    name = "sesion_login"
)

/**
 * Fuente de datos local. Encapsula por completo el acceso a DataStore:
 * ninguna otra clase conoce el nombre del fichero ni el de las claves.
 */
class SesionLocalDataSource(private val context: Context) {

    /**
     * Flujo con el token almacenado (null si no hay sesión).
     * Emite un valor nuevo cada vez que el contenido del DataStore cambia.
     */
    val token: Flow<String?> = context.dataStoreSesion.data
        .catch { excepcion ->
            // Un fichero corrupto o ilegible lanza IOException. En ese caso
            // emitimos preferencias vacías (equivale a "no hay sesión") en
            // lugar de dejar que la excepción rompa la recolección.
            if (excepcion is IOException) emit(emptyPreferences()) else throw excepcion
        }
        .map { preferencias -> preferencias[Claves.TOKEN] }

    /** Guarda el token. La función suspende hasta que la escritura se confirma en disco. */
    suspend fun guardarToken(token: String) {
        context.dataStoreSesion.edit { preferencias ->
            preferencias[Claves.TOKEN] = token
        }
    }

    /** Elimina la sesión almacenada. */
    suspend fun borrarSesion() {
        context.dataStoreSesion.edit { preferencias ->
            preferencias.remove(Claves.TOKEN)
        }
    }

    private object Claves {
        /**
         * Las claves de DataStore están tipadas: stringPreferencesKey solo
         * admite valores String. Existen equivalentes para Int, Boolean, etc.
         */
        val TOKEN = stringPreferencesKey("token")
    }
}