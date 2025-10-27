package com.example.gastroconectaaplicacion.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.gastroconectaaplicacion.data.model.Recipe
import kotlinx.coroutines.flow.Flow

@Dao
interface RecipeDao {

    @Insert
    suspend fun insertRecipe(recipe: Recipe)

    // Usamos Flow para que la UI se actualice automáticamente
    // cuando haya un cambio en la tabla de recetas (Reactividad).
    @Query("SELECT * FROM recipes ORDER BY id DESC")
    fun getAllRecipes(): Flow<List<Recipe>> // Flow es un flujo de datos

    @Query("SELECT * FROM recipes WHERE id = :id LIMIT 1")
    fun getRecipeById(id: Long): Flow<Recipe?>

    @Query("SELECT * FROM recipes WHERE autorId = :autorId ORDER BY id DESC")
    fun getRecipesByAuthor(autorId: Long): Flow<List<Recipe>>

    // Podríamos añadir funciones de Update y Delete aquí en el futuro
}