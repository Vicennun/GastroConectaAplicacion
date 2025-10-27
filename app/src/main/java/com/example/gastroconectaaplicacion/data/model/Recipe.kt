package com.example.gastroconectaaplicacion.data.model

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
    val autorId: Long, // Esta será la "llave foránea" para relacionarla con el User

    // --- NUEVOS CAMPOS ---
    // Room usará los conversores que creamos

    val ingredientes: List<Ingredients>, // <- Convertido con Gson
    val pasos: List<String>,             // <- Convertido con SEPARADOR
    val etiquetasDieteticas: List<String> // <- Convertido con SEPARADOR

    // Faltan "likes" y "comentarios". Los veremos en Relaciones.
)