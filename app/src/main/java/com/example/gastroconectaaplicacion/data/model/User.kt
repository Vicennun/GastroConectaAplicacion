package com.example.gastroconectaaplicacion.data.model

data class User(
    val id: Long? = null, // <--- CAMBIO CLAVE: Long? = null
    val name: String,
    val email: String,
    val password: String,
    val rol: String = "user",

    val recetario: List<Long> = emptyList(),
    val siguiendo: List<Long> = emptyList(),
    val seguidores: List<Long> = emptyList()
)