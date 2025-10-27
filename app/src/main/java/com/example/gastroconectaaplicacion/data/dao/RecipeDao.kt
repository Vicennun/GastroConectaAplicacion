package com.example.gastroconectaaplicacion.data.dao // Verifica

import androidx.room.* // Importa todo de Room
import com.example.gastroconectaaplicacion.data.model.Recipe
import kotlinx.coroutines.flow.Flow

@Dao
interface RecipeDao {

    @Insert
    suspend fun insertRecipe(recipe: Recipe)

    @Query("SELECT * FROM recipes ORDER BY id DESC")
    fun getAllRecipes(): Flow<List<Recipe>>

    @Query("SELECT * FROM recipes WHERE id = :id LIMIT 1")
    fun getRecipeById(id: Long): Flow<Recipe?> // Ya la tenías, asegúrate que devuelve Flow

    @Query("SELECT * FROM recipes WHERE autorId = :autorId ORDER BY id DESC")
    fun getRecipesByAuthor(autorId: Long): Flow<List<Recipe>>

    // --- NUEVA FUNCIÓN ---
    @Update
    suspend fun updateRecipe(recipe: Recipe) // Para guardar cambios (likes)
}