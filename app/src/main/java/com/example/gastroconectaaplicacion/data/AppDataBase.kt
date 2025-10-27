package com.example.gastroconectaaplicacion.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.gastroconectaaplicacion.data.dao.RecipeDao
import com.example.gastroconectaaplicacion.data.dao.UserDao
import com.example.gastroconectaaplicacion.data.model.Converters
import com.example.gastroconectaaplicacion.data.model.Recipe
import com.example.gastroconectaaplicacion.data.model.User

// 1. Define las tablas (Entities), la versión y los conversores
@Database(entities = [User::class, Recipe::class], version = 1, exportSchema = false)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {

    // 2. Conecta los DAOs (Room implementará esto por nosotros)
    abstract fun userDao(): UserDao
    abstract fun recipeDao(): RecipeDao

    // 3. Singleton (Patrón para asegurar una sola instancia de la DB)
    companion object {
        // @Volatile asegura que el valor de INSTANCE esté siempre actualizado
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            // Si INSTANCE no es nulo, la retorna.
            // Si es nulo, crea la base de datos de forma segura.
            return com.example.gastroconectaaplicacion.di.AppDatabase.Companion.INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "gastroconecta_database" // Nombre del archivo de la DB
                )
                    // .fallbackToDestructiveMigration() // Útil en desarrollo si cambias las tablas
                    .build()
                com.example.gastroconectaaplicacion.di.AppDatabase.Companion.INSTANCE = instance
                // Retorna la instancia
                instance
            }
        }
    }
}