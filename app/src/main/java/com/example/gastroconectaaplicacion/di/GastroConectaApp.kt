package com.example.gastroconectaaplicacion.di // Asegúrate que el package sea el correcto

import android.app.Application
import com.example.gastroconectaaplicacion.data.AppDatabase // Importa tu AppDatabase
import com.example.gastroconectaaplicacion.data.repository.RecipeRepository
import com.example.gastroconectaaplicacion.data.repository.UserRepository

/*
 * Esta SÍ es la clase Application correcta.
 */
class GastroConectaApp : Application() {

    // Base de datos (lazy init)
    private val database by lazy {
        AppDatabase.getDatabase(this)
    }

    // Repositorios (lazy init)
    val userRepository by lazy {
        UserRepository(database.userDao())
    }

    val recipeRepository by lazy {
        RecipeRepository(database.recipeDao())
    }
}