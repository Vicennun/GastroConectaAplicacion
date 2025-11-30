package com.example.gastroconectaaplicacion.data.network

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitClient {
    // Tu IP de AWS
    private const val BASE_URL = "http://54.87.102.198:8080/"

    // Configuración del cliente con Timeouts aumentados
    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS) // 60 segundos para conectar
        .readTimeout(60, TimeUnit.SECONDS)    // 60 segundos para esperar respuesta
        .writeTimeout(60, TimeUnit.SECONDS)   // 60 segundos para enviar datos (importante para fotos)
        .build()

    val apiService: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient) // <--- ASIGNAMOS EL CLIENTE AQUÍ
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }
}