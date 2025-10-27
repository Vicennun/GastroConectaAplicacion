package com.example.gastroconectaaplicacion.ui.navigation

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

// Importaremos estas pantallas en el siguiente paso
// import com.example.gastroconectaapp.ui.screens.LoginScreen
// import com.example.gastroconectaapp.ui.screens.RegisterScreen

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = AppScreens.LoginScreen.route // La app empieza en Login
    ) {

        // Ruta para la pantalla de Login
        composable(route = AppScreens.LoginScreen.route) {
            // Por ahora, solo un texto. Luego crearemos la pantalla completa.
            Text("Pantalla de Login")
            // LoginScreen(navController) // <-- Esto lo usaremos después
        }

        // Ruta para la pantalla de Registro
        composable(route = AppScreens.RegisterScreen.route) {
            // Por ahora, solo un texto.
            Text("Pantalla de Registro")
            // RegisterScreen(navController) // <-- Esto lo usaremos después
        }

        // (Aquí añadiremos las otras rutas: Home, Detalle, etc.)
    }
}