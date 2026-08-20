package com.ejemplo.examplelogin.data.datasource.remote

import com.ejemplo.examplelogin.data.datasource.remote.dto.LoginRequestDto
import com.ejemplo.examplelogin.data.datasource.remote.dto.LoginResponseDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

// ─── data/datasource/remote/LoginApiService.kt ───────────────────────────────────────────────────
interface LoginApiService {

    /**
     * POST https://api.javiercarrasco.es/coffee/login
     *
     * Se devuelve `Response<T>` en lugar de `T` directamente para poder
     * inspeccionar el código HTTP (400, 401...) sin capturar excepciones.
     */
    @POST("login")
    suspend fun login(@Body credenciales: LoginRequestDto): Response<LoginResponseDto>
}