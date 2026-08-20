package com.ejemplo.examplelogin.data.datasource.remote

import com.ejemplo.examplelogin.data.datasource.remote.dto.LoginRequestDto
import com.ejemplo.examplelogin.data.model.ResultadoLogin
import java.io.IOException

// ─── data/datasource/remote/RemoteDataSource.kt ──────────────────────────────────────────────────
class RemoteDataSource(private val apiService: LoginApiService = RetrofitClient.retrofit) {

    suspend fun login(usuario: String, password: String): ResultadoLogin {
        return try {
            val respuesta = apiService.login(LoginRequestDto(usuario, password))

            if (respuesta.isSuccessful) {
                val token = respuesta.body()?.token
                if (token.isNullOrBlank()) ResultadoLogin.RespuestaInesperada
                else ResultadoLogin.Exito(token)
            } else {
                when (respuesta.code()) {
                    400 -> ResultadoLogin.DatosIncompletos
                    401, 403 -> ResultadoLogin.CredencialesIncorrectas
                    else -> ResultadoLogin.ErrorServidor(respuesta.code())
                }
            }
        } catch (e: IOException) {
            // Sin red, DNS que no resuelve, timeout de conexión o de lectura.
            ResultadoLogin.SinConexion
        } catch (e: Exception) {
            // JSON malformado, error de conversión de Gson, etc.
            ResultadoLogin.RespuestaInesperada
        }
    }
}