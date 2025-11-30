package com.example.gastroconectaaplicacion.data.repository

import com.example.gastroconectaaplicacion.data.model.LoginRequest
import com.example.gastroconectaaplicacion.data.model.User
import com.example.gastroconectaaplicacion.data.network.RetrofitClient

class UserRepository {

    private val api = RetrofitClient.apiService

    suspend fun loginUser(email: String, pass: String): User? {
        return try {
            val response = api.login(LoginRequest(email, pass))
            if (response.isSuccessful) response.body() else null
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    suspend fun registerUser(user: User): User? {
        return try {
            val response = api.register(user)
            if (response.isSuccessful) response.body() else null
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    suspend fun getUserById(id: Long): User? {
        return try {
            val response = api.getUserById(id)
            if (response.isSuccessful) response.body() else null
        } catch (e: Exception) { null }
    }

    suspend fun toggleFollowUser(userId: Long, targetId: Long): User? {
        return try {
            val response = api.followUser(userId, targetId)
            if (response.isSuccessful) response.body() else null
        } catch (e: Exception) { null }
    }

    suspend fun toggleSaveRecipe(userId: Long, recipeId: Long): User? {
        return try {
            val response = api.saveRecipe(userId, recipeId)
            if (response.isSuccessful) response.body() else null
        } catch (e: Exception) { null }
    }
}