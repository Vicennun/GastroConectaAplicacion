package com.example.gastroconectaaplicacion.data.network

import com.example.gastroconectaaplicacion.data.model.Recipe
import com.example.gastroconectaaplicacion.data.model.User
import com.example.gastroconectaaplicacion.data.model.LoginRequest
import com.example.gastroconectaaplicacion.data.model.Comentario
import com.example.gastroconectaaplicacion.data.model.Rating
import retrofit2.Response
import retrofit2.http.*

interface ApiService {

    // --- USUARIOS ---

    @POST("api/v1/users/login")
    suspend fun login(@Body request: LoginRequest): Response<User>

    @POST("api/v1/users")
    suspend fun register(@Body user: User): Response<User>

    @GET("api/v1/users/{id}")
    suspend fun getUserById(@Path("id") id: Long): Response<User>

    // Seguir a un usuario
    @POST("api/v1/users/{userId}/seguir/{targetId}")
    suspend fun followUser(
        @Path("userId") userId: Long,
        @Path("targetId") targetId: Long
    ): Response<User>

    // Guardar receta en favoritos
    @POST("api/v1/users/{userId}/guardar/{recipeId}")
    suspend fun saveRecipe(
        @Path("userId") userId: Long,
        @Path("recipeId") recipeId: Long
    ): Response<User>


    // --- RECETAS ---

    @GET("api/v1/recetas")
    suspend fun getAllRecipes(): Response<List<Recipe>>

    @POST("api/v1/recetas")
    suspend fun createRecipe(@Body recipe: Recipe): Response<Recipe>

    // Dar Like
    @POST("api/v1/recetas/{id}/like")
    suspend fun toggleLike(
        @Path("id") recipeId: Long,
        @Query("userId") userId: Long
    ): Response<Recipe>

    // Comentar
    @POST("api/v1/recetas/{id}/comentar")
    suspend fun commentRecipe(
        @Path("id") recipeId: Long,
        @Body comentario: Comentario
    ): Response<Recipe>

    // Calificar (Rating)
    @POST("api/v1/recetas/{id}/rate")
    suspend fun rateRecipe(
        @Path("id") recipeId: Long,
        @Body rating: Rating
    ): Response<Recipe>
}