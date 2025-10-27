package com.example.gastroconectaaplicacion.ui.navigation // Verifica paquete

import androidx.compose.material3.Text // Asegúrate que esté importado
import androidx.compose.runtime.Composable
import androidx.navigation.NavType // Necesario para argumentos
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument // Necesario para argumentos

// Importa TODAS tus pantallas
import com.example.gastroconectaaplicacion.ui.screens.LoginScreen // Verifica import
import com.example.gastroconectaaplicacion.ui.screens.RegisterScreen // Verifica import
import com.example.gastroconectaaplicacion.ui.screens.HomeScreen // Verifica import
import com.example.gastroconectaaplicacion.ui.screens.CreateRecipeScreen // Verifica import
import com.example.gastroconectaaplicacion.ui.screens.RecipeDetailScreen // Verifica import
import com.example.gastroconectaaplicacion.ui.screens.ProfileScreen // Verifica import

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = AppScreens.LoginScreen.route // La app empieza en Login
    ) {
        composable(route = AppScreens.LoginScreen.route) {
            LoginScreen(navController)
        }
        composable(route = AppScreens.RegisterScreen.route) {
            RegisterScreen(navController)
        }
        composable(route = AppScreens.HomeScreen.route) {
            HomeScreen(navController)
        }
        composable(route = AppScreens.CreateRecipeScreen.route) {
            CreateRecipeScreen(navController)
        }
        // Ruta para Detalle, extrayendo el ID
        composable(
            route = AppScreens.RecipeDetailScreen.route,
            arguments = listOf(navArgument("recipeId") { type = NavType.LongType })
        ) { backStackEntry ->
            // Extrae el ID de los argumentos de la ruta
            val recipeId = backStackEntry.arguments?.getLong("recipeId") ?: 0L // Usa 0L como ID inválido por defecto
            RecipeDetailScreen(navController, recipeId)
        }
        composable(route = AppScreens.ProfileScreen.route) {
            ProfileScreen(navController)
        }
        // Puedes añadir más rutas aquí si es necesario
    }
}