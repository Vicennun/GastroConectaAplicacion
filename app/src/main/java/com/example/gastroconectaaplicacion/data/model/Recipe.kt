package com.example.gastroconectaaplicacion.data.model // Verifica

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters

@Entity(tableName = "recipes")
@TypeConverters(Converters::class)
data class Recipe(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val titulo: String,
    val descripcion: String,
    val tiempoPreparacion: String,
    val fotoUrl: String,
    val autorId: Long,
    val ingredientes: List<Ingredients>, // Ya tenías esto
    val pasos: List<String>,             // Ya tenías esto
    val etiquetasDieteticas: List<String>, // Ya tenías esto

    // --- NUEVO CAMPO ---
    val likes: List<Long> = emptyList() // Lista de IDs de usuarios que dieron like
    // Comentarios irían en otra tabla relacionada, lo omitimos por tiempo
)