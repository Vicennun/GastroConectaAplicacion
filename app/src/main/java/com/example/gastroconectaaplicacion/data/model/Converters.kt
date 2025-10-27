package com.example.gastroconectaaplicacion.data.model

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class Converters {

    private val gson = Gson()

    // --- Para List<String> (Pasos y Etiquetas) ---
    private val SEPARADOR = "||"

    @TypeConverter
    fun fromStringList(list: List<String>): String {
        // Convierte ["Paso 1", "Paso 2"] a "Paso 1||Paso 2"
        return list.joinToString(SEPARADOR)
    }

    @TypeConverter
    fun toStringList(data: String): List<String> {
        // Convierte "Paso 1||Paso 2" de nuevo a ["Paso 1", "Paso 2"]
        return if (data.isEmpty()) {
            emptyList()
        } else {
            data.split(SEPARADOR)
        }
    }

    // --- Para List<Ingrediente> (Ingredientes) ---

    @TypeConverter
    fun fromIngredienteList(list: List<Ingredients>): String {
        // Convierte la lista de objetos a un string JSON
        return gson.toJson(list)
    }

    @TypeConverter
    fun toIngredienteList(data: String): List<Ingredients> {
        if (data.isEmpty()) {
            return emptyList()
        }
        // Convierte el string JSON de nuevo a una lista de objetos
        val listType = object : TypeToken<List<Ingredients>>() {}.type
        return gson.fromJson(data, listType)
    }
}