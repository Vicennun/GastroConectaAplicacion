package com.example.gastroconectaaplicacion.data.repository // Verifica

import com.example.gastroconectaaplicacion.data.dao.RecipeDao
import com.example.gastroconectaaplicacion.data.model.Recipe
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

class RecipeRepository(private val recipeDao: RecipeDao) {

    val allRecipes: Flow<List<Recipe>> = recipeDao.getAllRecipes()

    fun getRecipeById(id: Long): Flow<Recipe?> {
        return recipeDao.getRecipeById(id)
    }

    fun getRecipesByAuthor(autorId: Long): Flow<List<Recipe>> {
        return recipeDao.getRecipesByAuthor(autorId)
    }

    suspend fun insertRecipe(recipe: Recipe) {
        recipeDao.insertRecipe(recipe)
    }

    // --- NUEVA FUNCIÓN ---
    // Añade/Quita like de un usuario a una receta
    suspend fun toggleLikeRecipe(userId: Long, recipeId: Long) {
        val recipeFlow = recipeDao.getRecipeById(recipeId)
        val recipe = recipeFlow.first() ?: return

        val updatedLikes = if (recipe.likes.contains(userId)) {
            recipe.likes - userId // Quita el like
        } else {
            recipe.likes + userId // Añade el like
        }
        recipeDao.updateRecipe(recipe.copy(likes = updatedLikes))
    }
}