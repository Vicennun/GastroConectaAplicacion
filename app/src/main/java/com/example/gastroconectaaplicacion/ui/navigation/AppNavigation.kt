package com.example.gastroconectaaplicacion.ui.navigation

import android.app.Application
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.gastroconectaaplicacion.ui.screens.*
import com.example.gastroconectaaplicacion.ui.viewmodel.AuthViewModel
import com.example.gastroconectaaplicacion.ui.viewmodel.RecipeViewModel
import com.example.gastroconectaaplicacion.ui.viewmodel.ViewModelFactory

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    // 1. Instanciar ViewModels COMPARTIDOS (Hoisting)
    val context = LocalContext.current
    val application = context.applicationContext as Application
    val factory = ViewModelFactory(application)

    // Estos ViewModels vivirán mientras la app esté abierta
    val authViewModel: AuthViewModel = viewModel(factory = factory)
    val recipeViewModel: RecipeViewModel = viewModel(factory = factory)

    NavHost(
        navController = navController,
        startDestination = AppScreens.LoginScreen.route
    ) {
        composable(route = AppScreens.LoginScreen.route) {
            // Pasamos el authViewModel compartido
            LoginScreen(navController, authViewModel)
        }
        composable(route = AppScreens.RegisterScreen.route) {
            RegisterScreen(navController, authViewModel)
        }
        composable(route = AppScreens.HomeScreen.route) {
            // Home necesita ver recetas
            HomeScreen(navController, recipeViewModel)
        }
        composable(route = AppScreens.CreateRecipeScreen.route) {
            // ¡AQUÍ SE ARREGLA TU ERROR! Pasamos los 3 argumentos
            CreateRecipeScreen(navController, authViewModel, recipeViewModel)
        }
        composable(
            route = AppScreens.RecipeDetailScreen.route,
            arguments = listOf(navArgument("recipeId") { type = NavType.LongType })
        ) { backStackEntry ->
            val recipeId = backStackEntry.arguments?.getLong("recipeId") ?: 0L
            // Detalle necesita ambos (para ver receta y para dar like/guardar)
            RecipeDetailScreen(navController, recipeId, authViewModel, recipeViewModel)
        }
        composable(route = AppScreens.ProfileScreen.route) {
            ProfileScreen(navController, authViewModel)
        }
    }
}