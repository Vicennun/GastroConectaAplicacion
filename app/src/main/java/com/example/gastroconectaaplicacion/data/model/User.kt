package com.example.gastroconectaaplicacion.data.model // Verifica

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters // Añade este import

@Entity(tableName = "users")
@TypeConverters(Converters::class) // <-- ¡AÑADE ESTO! Le dice a Room que use los conversores
data class User(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val nombre: String,
    val email: String,
    val password_hash: String,

    // --- NUEVOS CAMPOS ---
    val recetario: List<Long> = emptyList(), // Lista de IDs de recetas guardadas
    val siguiendo: List<Long> = emptyList(), // Lista de IDs de usuarios que sigue
    val seguidores: List<Long> = emptyList() // Lista de IDs de usuarios que le siguen
)