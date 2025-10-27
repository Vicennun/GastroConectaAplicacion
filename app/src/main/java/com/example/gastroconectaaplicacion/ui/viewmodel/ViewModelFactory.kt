package com.example.gastroconectaaplicacion.ui.viewmodel // Verifica este paquete

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.gastroconectaaplicacion.di.GastroConectaApp // Verifica este import

class ViewModelFactory(private val application: Application) : ViewModelProvider.Factory {

    // Obtenemos los repositorios desde nuestra clase Application
    private val app = application as GastroConectaApp
    private val userRepository = app.userRepository
    private val recipeRepository = app.recipeRepository // <-- Necesitas este

    override fun <T : ViewModel> create(modelClass: Class<T>): T {

        // Si la UI pide un AuthViewModel...
        if (modelClass.isAssignableFrom(AuthViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AuthViewModel(userRepository) as T
        }

        // --- AÑADE ESTO ---
        // Si la UI pide un RecipeViewModel...
        if (modelClass.isAssignableFrom(RecipeViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return RecipeViewModel(recipeRepository) as T // Le pasamos el RecipeRepository
        }
        // --- --- --- ---

        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}