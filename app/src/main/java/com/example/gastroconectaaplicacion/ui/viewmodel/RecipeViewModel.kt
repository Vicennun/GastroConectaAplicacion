package com.example.gastroconectaaplicacion.ui.viewmodel // Verifica paquete

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gastroconectaaplicacion.data.model.Recipe // Verifica import
import com.example.gastroconectaaplicacion.data.repository.RecipeRepository // Verifica import
import kotlinx.coroutines.flow.* // Necesitas importar Flow
import kotlinx.coroutines.launch

class RecipeViewModel(private val recipeRepository: RecipeRepository) : ViewModel() {

    // Flujo de todas las recetas
    val allRecipes: StateFlow<List<Recipe>> = recipeRepository.allRecipes
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000L),
            initialValue = emptyList()
        )

    // --- AÑADE ESTA FUNCIÓN ---
    // Función para obtener una receta específica por ID como Flow
    // La UI observará este Flow para obtener la receta
    fun getRecipeById(id: Long): Flow<Recipe?> {
        return recipeRepository.getRecipeById(id)
    }
    // --- --- --- --- --- ---

    // Función básica para añadir receta
    fun addRecipe(recipe: Recipe) {
        viewModelScope.launch {
            try {
                recipeRepository.insertRecipe(recipe)
                // TODO: Añadir manejo de estado de UI (éxito/error)
            } catch (e: Exception) {
                // TODO: Añadir manejo de estado de UI (éxito/error)
            }
        }
    }

    // Aquí añadirías funciones para getRecipesByAuthor, updateRecipe, deleteRecipe, etc.
    // fun getRecipesByAuthor(authorId: Long): StateFlow<List<Recipe>> { ... }
}

// Opcional: Podrías crear un RecipeUiState similar al AuthUiState
// sealed class RecipeUiState { ... }