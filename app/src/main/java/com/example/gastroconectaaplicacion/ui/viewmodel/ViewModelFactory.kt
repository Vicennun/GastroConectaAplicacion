package com.example.gastroconectaaplicacion.ui.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.gastroconectaaplicacion.data.local.SessionManager // Importante: Importar el SessionManager
import com.example.gastroconectaaplicacion.data.repository.RecipeRepository
import com.example.gastroconectaaplicacion.data.repository.UserRepository

class ViewModelFactory(private val application: Application) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {

        // Configuración para AuthViewModel
        if (modelClass.isAssignableFrom(AuthViewModel::class.java)) {
            // 1. Creamos la instancia de SessionManager pasándole el contexto
            val sessionManager = SessionManager(application.applicationContext)

            // 2. Se lo pasamos al ViewModel junto con el repositorio
            return AuthViewModel(UserRepository(), sessionManager) as T
        }

        // Configuración para RecipeViewModel (se mantiene igual)
        if (modelClass.isAssignableFrom(RecipeViewModel::class.java)) {
            return RecipeViewModel(RecipeRepository()) as T
        }

        throw IllegalArgumentException("Unknown ViewModel class")
    }
}