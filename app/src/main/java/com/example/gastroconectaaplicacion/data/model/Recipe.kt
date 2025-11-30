package com.example.gastroconectaaplicacion.data.model

data class Recipe(
    val id: Long? = null, // El ID es opcional para nuevas recetas
    val titulo: String,
    val descripcion: String,
    val tiempoPreparacion: String,
    val autorId: Long,
    val autorNombre: String,
    val foto: String,
    val confirmado: Boolean = false,

    val pasos: List<String> = emptyList(),
    val etiquetasDieteticas: List<String> = emptyList(),
    val likes: List<Long> = emptyList(),

    val ingredientesSimples: List<String> = emptyList(),

    val comentarios: List<Comentario> = emptyList(),
    val ratings: List<Rating> = emptyList()
)