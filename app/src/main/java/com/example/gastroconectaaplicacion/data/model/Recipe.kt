package com.example.gastroconectaaplicacion.data.model

data class Recipe(
    val id: Long = 0,
    val titulo: String,
    val descripcion: String,
    val tiempoPreparacion: String,
    val autorId: Long,
    val autorNombre: String,
    val foto: String, // Recibirá URL o Base64
    val confirmado: Boolean = false,

    val pasos: List<String> = emptyList(),
    val etiquetasDieteticas: List<String> = emptyList(),
    val likes: List<Long> = emptyList(),

    // Backend envía strings "Harina - 1 taza"
    val ingredientesSimples: List<String> = emptyList(),

    val comentarios: List<Comentario> = emptyList(),
    val ratings: List<RecipeRating> = emptyList() // Cambiar List<Rating> por List<RecipeRating>
)

// Cambiar nombre de la clase data class Rating a:
data class RecipeRating(
    val userId: Long,
    val score: Int
)

// Clases auxiliares para listas complejas
data class Comentario(
    val autorId: Long,
    val autorNombre: String,
    val texto: String,
    val fecha: String? = null
)



// Clase helper para la UI (si la necesitas para formularios locales)
data class IngredientUI(
    val nombre: String,
    val cantidad: String
)