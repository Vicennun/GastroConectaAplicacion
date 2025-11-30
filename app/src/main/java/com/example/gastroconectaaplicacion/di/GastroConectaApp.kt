package com.example.gastroconectaaplicacion.di

import android.app.Application
import com.example.gastroconectaaplicacion.data.repository.RecipeRepository
import com.example.gastroconectaaplicacion.data.repository.UserRepository

class GastroConectaApp : Application() {

    // ELIMINADO: Ya no iniciamos AppDatabase aquí.

    // Repositorios: Ahora se inician sin parámetros
    // (porque usan RetrofitClient internamente)
    val userRepository by lazy {
        UserRepository()
    }

    val recipeRepository by lazy {
        RecipeRepository()
    }
}