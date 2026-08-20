package com.ejemplo.examplelogin.data.datasource.remote

import com.ejemplo.examplelogin.BuildConfig // para acceder a BuildConfig.DEBUG se debe constructir el proyecto al menos una vez
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

// ─── data/datasource/remote/RetrofitClient.kt ────────────────────────────────────────────────────
object RetrofitClient {

    private const val URL_BASE = "https://api.javiercarrasco.es/coffee/"

    /**
     * Interceptor que vuelca en el Logcat la petición y la respuesta completas.
     * En una compilación de release se desactiva: el cuerpo de /login contiene
     * la contraseña en claro y el token.
     */
    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BODY
        else HttpLoggingInterceptor.Level.NONE
    }

    private val okHttpClient: OkHttpClient = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(15, TimeUnit.SECONDS)
        .build()

    val retrofit: LoginApiService by lazy {
        Retrofit.Builder()
            .baseUrl(URL_BASE)              // debe terminar en "/"
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(LoginApiService::class.java)
    }
}