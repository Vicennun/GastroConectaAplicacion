package com.example.gastroconectaaplicacion.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.gastroconectaaplicacion.data.model.User

@Dao
interface UserDao {

    // Estrategia OnConflict: Si intentamos insertar un email que ya existe,
    // aborta la transacción. Esto previene emails duplicados.
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertUser(user: User) // 'suspend' para corutinas

    @Query("SELECT * FROM users WHERE email = :email LIMIT 1")
    suspend fun getUserByEmail(email: String): User? // '?' = puede ser nulo

    @Query("SELECT * FROM users WHERE id = :id LIMIT 1")
    suspend fun getUserById(id: Long): User?
}