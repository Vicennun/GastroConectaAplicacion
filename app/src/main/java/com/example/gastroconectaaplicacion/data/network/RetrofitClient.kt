package com.example.gastroconectaaplicacion.data.network

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitClient {
    // Asegúrate de que esta sea tu IP Elástica actual de AWS
    private const val BASE_URL = "http://54.87.102.198:8080/"

    // Configuración del cliente para tolerar subidas lentas (fotos)
    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS) // Tiempo para conectar al servidor
        .readTimeout(60, TimeUnit.SECONDS)    // Tiempo esperando respuesta
        .writeTimeout(60, TimeUnit.SECONDS)   // Tiempo enviando datos (subida)
        .build()

    val apiService: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient) // <--- AQUÍ CONECTAMOS LA CONFIGURACIÓN
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }
}