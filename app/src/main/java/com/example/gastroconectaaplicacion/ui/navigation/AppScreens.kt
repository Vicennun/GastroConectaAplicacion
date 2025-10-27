package com.example.gastroconectaaplicacion.ui.navigation

// Define todas las pantallas/rutas de nuestra aplicación
sealed class AppScreens(val route: String) {
    object LoginScreen : AppScreens("login_screen")
    object RegisterScreen : AppScreens("register_screen")

    // Añadiremos estas más adelante
    // object HomeScreen : AppScreens("home_screen")
    // object DetailScreen : AppScreens("detail_screen/{recipeId}") // Ruta con argumento
}