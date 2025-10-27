package com.example.gastroconectaaplicacion.ui.viewmodel // Verifica

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gastroconectaaplicacion.data.model.Recipe
import com.example.gastroconectaaplicacion.data.repository.RecipeRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class RecipeViewModel(private val recipeRepository: RecipeRepository) : ViewModel() {

    val allRecipes: StateFlow<List<Recipe>> = recipeRepository.allRecipes
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000L),
            initialValue = emptyList()
        )

    fun getRecipeById(id: Long): Flow<Recipe?> {
        return recipeRepository.getRecipeById(id)
    }

    fun addRecipe(recipe: Recipe) {
        viewModelScope.launch {
            recipeRepository.insertRecipe(recipe)
        }
    }

    // --- NUEVA FUNCIÓN ---
    fun toggleLike(userId: Long?, recipeId: Long) {
        if (userId == null) return // Solo usuarios logueados pueden dar like
        viewModelScope.launch {
            try {
                recipeRepository.toggleLikeRecipe(userId, recipeId)
                // La UI se actualizará automáticamente porque observa el Flow de la receta
            } catch (e: Exception) { /* Manejar error */ }
        }
    }
}