package com.example.gastroconectaaplicacion.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gastroconectaaplicacion.data.model.Comentario
import com.example.gastroconectaaplicacion.data.model.Rating
import com.example.gastroconectaaplicacion.data.model.Recipe
import com.example.gastroconectaaplicacion.data.repository.RecipeRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.map
import android.util.Log

class RecipeViewModel(private val repository: RecipeRepository) : ViewModel() {

    private val _recipes = MutableStateFlow<List<Recipe>>(emptyList())
    val recipes: StateFlow<List<Recipe>> = _recipes.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    // Cargar recetas al iniciar
    init {
        refreshRecipes()
    }

    fun refreshRecipes() {
        viewModelScope.launch {
            _isLoading.value = true
            val list = repository.getAllRecipes()
            _recipes.value = list
            _isLoading.value = false
        }
    }

    fun addRecipe(recipe: Recipe, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            // _isLoading.value = true // Puedes descomentar si usas loading
            try {
                val newRecipe = repository.addRecipe(recipe)
                if (newRecipe != null) {
                    _recipes.value = listOf(newRecipe) + _recipes.value
                    onResult(true)
                } else {
                    Log.e("RecipeViewModel", "Error: La API devolvió null (posible error 400/500)")
                    onResult(false)
                }
            } catch (e: Exception) {
                // ¡ESTO ES VITAL! Ver el error real en el Logcat
                Log.e("RecipeViewModel", "Excepción al subir receta", e)
                onResult(false)
            }
            // _isLoading.value = false
        }
    }

    fun toggleLike(recipeId: Long, userId: Long) {
        viewModelScope.launch {
            val updated = repository.toggleLikeRecipe(recipeId, userId)
            if (updated != null) updateLocalRecipe(updated)
        }
    }

    fun addComment(recipeId: Long, comentario: Comentario) {
        viewModelScope.launch {
            val updated = repository.addComment(recipeId, comentario)
            if (updated != null) updateLocalRecipe(updated)
        }
    }

    fun rateRecipe(recipeId: Long, rating: Rating) {
        viewModelScope.launch {
            val updated = repository.rateRecipe(recipeId, rating)
            if (updated != null) updateLocalRecipe(updated)
        }
    }

    fun getRecipeById(id: Long): kotlinx.coroutines.flow.Flow<Recipe?> {
        // Observa la lista de recetas y busca la que coincida con el ID
        return recipes.map { list -> list.find { it.id == id } }
    }

    // Helper para actualizar una receta específica en la lista sin recargar todo
    private fun updateLocalRecipe(updatedRecipe: Recipe) {
        _recipes.value = _recipes.value.map { if (it.id == updatedRecipe.id) updatedRecipe else it }
    }
}