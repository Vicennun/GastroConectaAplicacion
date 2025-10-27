package com.example.gastroconectaaplicacion.data.dao // Verifica

import androidx.room.* // Importa todo de Room
import com.example.gastroconectaaplicacion.data.model.User
import kotlinx.coroutines.flow.Flow // No necesitas Flow aquí por ahora

@Dao
interface UserDao {

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertUser(user: User)

    @Query("SELECT * FROM users WHERE email = :email LIMIT 1")
    suspend fun getUserByEmail(email: String): User?

    @Query("SELECT * FROM users WHERE id = :id LIMIT 1")
    suspend fun getUserById(id: Long): User?

    // --- NUEVA FUNCIÓN ---
    @Update
    suspend fun updateUser(user: User) // Para guardar cambios (seguir, recetario)
}