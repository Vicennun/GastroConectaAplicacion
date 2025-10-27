package com.example.gastroconectaaplicacion.data.model // Verifica

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken // Make sure this one is also there

class Converters {

    private val gson = Gson()
    private val SEPARADOR_STRING = "||"
    private val SEPARADOR_LONG = "," // Usamos coma para separar IDs numéricos

    // --- Para List<String> (Pasos y Etiquetas) ---
    @TypeConverter
    fun fromStringList(list: List<String>?): String {
        return list?.joinToString(SEPARADOR_STRING) ?: "" // Maneja nulos
    }

    @TypeConverter
    fun toStringList(data: String?): List<String> {
        return if (data.isNullOrEmpty()) emptyList() else data.split(SEPARADOR_STRING)
    }

    // --- Para List<Ingredients> ---
    @TypeConverter
    fun fromIngredienteList(list: List<Ingredients>?): String {
        return gson.toJson(list ?: emptyList<Ingredients>()) // Maneja nulos
    }

    @TypeConverter
    fun toIngredienteList(data: String?): List<Ingredients> {
        if (data.isNullOrEmpty()) return emptyList()
        val listType = object : TypeToken<List<Ingredients>>() {}.type
        return gson.fromJson(data, listType) ?: emptyList() // Maneja nulos
    }

    // --- NUEVO: Para List<Long> (Likes, Recetario, Siguiendo, Seguidores) ---
    @TypeConverter
    fun fromLongList(list: List<Long>?): String {
        // Convierte [1, 5, 10] a "1,5,10"
        return list?.joinToString(SEPARADOR_LONG) ?: "" // Maneja nulos
    }

    @TypeConverter
    fun toLongList(data: String?): List<Long> {
        // Convierte "1,5,10" a [1, 5, 10]
        return if (data.isNullOrEmpty()) {
            emptyList()
        } else {
            try {
                data.split(SEPARADOR_LONG).mapNotNull { it.toLongOrNull() } // Convierte cada parte a Long, ignora si falla
            } catch (e: Exception) {
                emptyList() // En caso de error, devuelve lista vacía
            }
        }
    }
}