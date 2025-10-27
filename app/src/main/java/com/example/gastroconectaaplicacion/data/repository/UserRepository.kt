package com.example.gastroconectaaplicacion.data.repository // Verifica

import com.example.gastroconectaaplicacion.data.dao.UserDao
import com.example.gastroconectaaplicacion.data.model.User

class UserRepository(private val userDao: UserDao) {

    suspend fun registerUser(user: User) {
        userDao.insertUser(user)
    }

    suspend fun loginUser(email: String, password: String): User? {
        val user = userDao.getUserByEmail(email)
        // Simplificado: Compara hash directamente (deberías usar una librería de hashing)
        if (user != null && user.password_hash == password) {
            return user
        }
        return null
    }

    suspend fun getUserById(id: Long): User? {
        return userDao.getUserById(id)
    }

    // --- NUEVAS FUNCIONES ---

    // Guarda/Quita receta del recetario del usuario
    suspend fun toggleSaveRecipe(userId: Long, recipeId: Long) {
        val user = userDao.getUserById(userId) ?: return // Sal si el usuario no existe
        val updatedRecetario = if (user.recetario.contains(recipeId)) {
            user.recetario - recipeId // Quita el ID
        } else {
            user.recetario + recipeId // Añade el ID
        }
        userDao.updateUser(user.copy(recetario = updatedRecetario))
    }

    // Sigue/Deja de seguir a otro usuario
    suspend fun toggleFollowUser(currentUserId: Long, targetUserId: Long) {
        if (currentUserId == targetUserId) return // No te puedes seguir a ti mismo

        val currentUser = userDao.getUserById(currentUserId) ?: return
        val targetUser = userDao.getUserById(targetUserId) ?: return

        val isFollowing = currentUser.siguiendo.contains(targetUserId)

        // Actualiza lista 'siguiendo' del usuario actual
        val updatedFollowing = if (isFollowing) {
            currentUser.siguiendo - targetUserId
        } else {
            currentUser.siguiendo + targetUserId
        }
        userDao.updateUser(currentUser.copy(siguiendo = updatedFollowing))

        // Actualiza lista 'seguidores' del usuario objetivo
        val updatedFollowers = if (isFollowing) {
            targetUser.seguidores - currentUserId
        } else {
            targetUser.seguidores + currentUserId
        }
        userDao.updateUser(targetUser.copy(seguidores = updatedFollowers))
    }
}