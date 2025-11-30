package com.example.gastroconectaaplicacion.data.repository

import android.util.Log
import com.example.gastroconectaaplicacion.data.model.Comentario
import com.example.gastroconectaaplicacion.data.model.Rating
import com.example.gastroconectaaplicacion.data.model.Recipe
import com.example.gastroconectaaplicacion.data.network.RetrofitClient

class RecipeRepository {

    private val api = RetrofitClient.apiService
    private val TAG = "RecipeRepository"

    suspend fun getAllRecipes(): List<Recipe> {
        return try {
            val response = api.getAllRecipes()
            if (response.isSuccessful) {
                response.body() ?: emptyList()
            } else {
                Log.e(TAG, "Error al obtener recetas: ${response.code()} - ${response.errorBody()?.string()}")
                emptyList()
            }
        } catch (e: Exception) {
            Log.e(TAG, "Excepción al obtener recetas", e)
            emptyList()
        }
    }

    suspend fun addRecipe(recipe: Recipe): Recipe? {
        return try {
            val response = api.createRecipe(recipe)
            if (response.isSuccessful) {
                Log.d(TAG, "¡Receta subida con éxito!")
                response.body()
            } else {
                // ¡AQUÍ VEREMOS EL ERROR REAL DEL SERVIDOR!
                val errorBody = response.errorBody()?.string()
                Log.e(TAG, "FALLÓ SUBIDA: Código ${response.code()}")
                Log.e(TAG, "FALLÓ SUBIDA: Razón $errorBody")
                null
            }
        } catch (e: Exception) {
            // ¡AQUÍ VEREMOS SI ES ERROR DE CONEXIÓN/INTERNET!
            Log.e(TAG, "EXCEPCIÓN AL SUBIR", e)
            null
        }
    }

    suspend fun toggleLikeRecipe(recipeId: Long, userId: Long): Recipe? {
        return try {
            val response = api.toggleLike(recipeId, userId)
            if (response.isSuccessful) response.body() else null
        } catch (e: Exception) { null }
    }

    suspend fun addComment(recipeId: Long, comentario: Comentario): Recipe? {
        return try {
            val response = api.commentRecipe(recipeId, comentario)
            if (response.isSuccessful) response.body() else null
        } catch (e: Exception) { null }
    }

    suspend fun rateRecipe(recipeId: Long, rating: Rating): Recipe? {
        return try {
            val response = api.rateRecipe(recipeId, rating)
            if (response.isSuccessful) response.body() else null
        } catch (e: Exception) { null }
    }
}