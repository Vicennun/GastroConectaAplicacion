package com.example.gastroconectaaplicacion.ui.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.gastroconectaaplicacion.data.repository.RecipeRepository
import com.example.gastroconectaaplicacion.data.repository.UserRepository

class ViewModelFactory(private val application: Application) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AuthViewModel::class.java)) {
            // Ya no pasamos DAO, el repositorio se encarga de Retrofit internamente
            return AuthViewModel(UserRepository()) as T
        }
        if (modelClass.isAssignableFrom(RecipeViewModel::class.java)) {
            return RecipeViewModel(RecipeRepository()) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}