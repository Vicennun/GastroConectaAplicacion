package com.example.gastroconectaaplicacion.data.repository

import com.example.gastroconectaaplicacion.data.model.Comentario
import com.example.gastroconectaaplicacion.data.model.Rating
import com.example.gastroconectaaplicacion.data.model.Recipe
import com.example.gastroconectaaplicacion.data.network.RetrofitClient

class RecipeRepository {

    private val api = RetrofitClient.apiService

    suspend fun getAllRecipes(): List<Recipe> {
        return try {
            val response = api.getAllRecipes()
            if (response.isSuccessful) response.body() ?: emptyList() else emptyList()
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    suspend fun addRecipe(recipe: Recipe): Recipe? {
        return try {
            val response = api.createRecipe(recipe)
            if (response.isSuccessful) response.body() else null
        } catch (e: Exception) {
            e.printStackTrace()
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