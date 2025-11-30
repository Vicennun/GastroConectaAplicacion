package com.example.gastroconectaaplicacion.data.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    // ¡IMPORTANTE! Pon aquí la IP PÚBLICA de tu AWS EC2 (igual que en el React)
    // No uses 'localhost', el emulador no entenderá.
    private const val BASE_URL = "http://54.87.102.198:8080/"

    val apiService: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }
}