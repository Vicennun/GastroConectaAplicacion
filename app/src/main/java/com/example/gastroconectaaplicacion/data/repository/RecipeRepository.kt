package com.example.gastroconectaaplicacion.data.repository

import com.example.gastroconectaaplicacion.data.dao.RecipeDao
import com.example.gastroconectaaplicacion.data.model.Recipe
import kotlinx.coroutines.flow.Flow

// 1. Pedimos el DAO en el constructor
class RecipeRepository(private val recipeDao: RecipeDao) {

    // 2. Exponemos los 'Flows' del DAO directamente.
    // Compose se conectará a estos para recibir actualizaciones en vivo.
    val allRecipes: Flow<List<Recipe>> = recipeDao.getAllRecipes()

    fun getRecipeById(id: Long): Flow<Recipe?> {
        return recipeDao.getRecipeById(id)
    }

    fun getRecipesByAuthor(autorId: Long): Flow<List<Recipe>> {
        return recipeDao.getRecipesByAuthor(autorId)
    }

    // 3. Las funciones 'suspend' simplemente llaman al DAO.
    // El ViewModel llamará a esta función cuando se cree una receta.
    suspend fun insertRecipe(recipe: Recipe) {
        recipeDao.insertRecipe(recipe)
    }
}