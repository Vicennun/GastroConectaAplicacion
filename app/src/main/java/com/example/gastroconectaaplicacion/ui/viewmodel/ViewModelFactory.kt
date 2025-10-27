package com.example.gastroconectaaplicacion.ui.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.gastroconectaaplicacion.di.GastroConectaApp

/*
 * Esta clase es la "Fábrica de Cerebros".
 * Sabe cómo crear TODOS nuestros ViewModels porque tiene
 * acceso a los repositorios que definimos en la clase Application.
 */
class ViewModelFactory(private val application: Application) : ViewModelProvider.Factory {

    // Obtenemos los repositorios desde nuestra clase Application
    private val app = application as GastroConectaApp
    private val userRepository = app.userRepository
    private val recipeRepository = app.recipeRepository

    override fun <T : ViewModel> create(modelClass: Class<T>): T {

        // Si la UI (Compose) pide un AuthViewModel...
        if (modelClass.isAssignableFrom(AuthViewModel::class.java)) {
            // ...lo creamos y le pasamos el userRepository
            @Suppress("UNCHECKED_CAST")
            return AuthViewModel(userRepository) as T
        }

        // (Aquí añadiremos el 'RecipeViewModel' en el futuro)
        // if (modelClass.isAssignableFrom(RecipeViewModel::class.java)) {
        //     @Suppress("UNCHECKED_CAST")
        //     return RecipeViewModel(recipeRepository) as T
        // }

        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}