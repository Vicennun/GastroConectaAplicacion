package com.example.gastroconectaaplicacion.data.local

import android.content.Context
import android.content.SharedPreferences
import com.example.gastroconectaaplicacion.data.model.User
import com.google.gson.Gson

class SessionManager(context: Context) {

    private val prefs: SharedPreferences = context.getSharedPreferences("user_session", Context.MODE_PRIVATE)
    private val gson = Gson()

    // Guardar usuario
    fun saveUser(user: User) {
        val json = gson.toJson(user)
        prefs.edit().putString("user_data", json).apply()
    }

    // Obtener usuario guardado
    fun getUser(): User? {
        val json = prefs.getString("user_data", null)
        return if (json != null) {
            try {
                gson.fromJson(json, User::class.java)
            } catch (e: Exception) {
                null
            }
        } else {
            null
        }
    }

    // Cerrar sesión
    fun clearSession() {
        prefs.edit().remove("user_data").apply()
    }
}