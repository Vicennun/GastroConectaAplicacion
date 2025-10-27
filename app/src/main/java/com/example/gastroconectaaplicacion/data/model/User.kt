package com.example.gastroconectaaplicacion.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users") // Así se llamará la tabla en la base de datos
data class User(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0, // Room se encargará de generar el ID
    val nombre: String,
    val email: String,
    val password_hash: String // ¡Importante! Nunca guardes contraseñas en texto plano
    // Faltan campos como "recetario", "siguiendo", "seguidores".
    // Los añadiremos después cuando veamos las "Relaciones"
)