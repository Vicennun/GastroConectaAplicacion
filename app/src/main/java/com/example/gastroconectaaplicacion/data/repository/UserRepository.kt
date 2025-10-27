package com.example.gastroconectaaplicacion.data.repository

import com.example.gastroconectaaplicacion.data.dao.UserDao
import com.example.gastroconectaaplicacion.data.model.User

// 1. Pedimos el DAO en el constructor
class UserRepository(private val userDao: UserDao) {

    // 2. Función para registrar
    suspend fun registerUser(user: User) {
        // (Aquí en el futuro iría la lógica para "hashear" la contraseña)
        userDao.insertUser(user)
    }

    // 3. Función para login
    // Compara el email y la contraseña (aún no hasheada, igual que en tu app React)
    suspend fun loginUser(email: String, password: String): User? {
        val user = userDao.getUserByEmail(email)

        // Comprobamos si el usuario existe Y si la contraseña coincide
        if (user != null && user.password_hash == password) {
            return user
        }
        return null // Si no coincide, retorna nulo
    }

    suspend fun getUserById(id: Long): User? {
        return userDao.getUserById(id)
    }
}