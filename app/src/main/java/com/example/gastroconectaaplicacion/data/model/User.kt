package com.example.gastroconectaaplicacion.data.model

data class User(
    val id: Long = 0,
    val name: String,       // Backend usa "name", no "nombre"
    val email: String,
    val password: String,   // Backend espera "password"
    val rol: String = "user",

    // Listas de IDs que manda el backend
    val recetario: List<Long> = emptyList(),
    val siguiendo: List<Long> = emptyList(),
    val seguidores: List<Long> = emptyList()
)