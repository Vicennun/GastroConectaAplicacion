package com.example.gastroconectaaplicacion.data.model

data class Comentario(
    val autorId: Long,
    val autorNombre: String,
    val texto: String,
    val fecha: String? = null
)