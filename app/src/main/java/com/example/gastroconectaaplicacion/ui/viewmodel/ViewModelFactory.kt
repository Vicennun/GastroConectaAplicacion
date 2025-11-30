package com.example.gastroconectaaplicacion.ui.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.gastroconectaaplicacion.data.local.SessionManager // Importar esto
import com.example.gastroconectaaplicacion.data.repository.RecipeRepository
import com.example.gastroconectaaplicacion.data.repository.UserRepository

class ViewModelFactory(private val application: Application) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AuthViewModel::class.java)) {
            // Creamos el SessionManager pasándole el contexto de la aplicación
            val sessionManager = SessionManager(application.applicationContext)

            // Se lo pasamos al AuthViewModel junto con el repositorio
            return AuthViewModel(UserRepository(), sessionManager) as T
        }
        if (modelClass.isAssignableFrom(RecipeViewModel::class.java)) {
            return RecipeViewModel(RecipeRepository()) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}