package com.example.gastroconectaaplicacion.ui.navigation

sealed class AppScreens(val route: String) {
    object LoginScreen : AppScreens("login_screen")
    object RegisterScreen : AppScreens("register_screen")
    object HomeScreen : AppScreens("home_screen")
    object CreateRecipeScreen : AppScreens("create_recipe_screen")
    // Detalle necesita argumento
    object RecipeDetailScreen : AppScreens("recipe_detail_screen/{recipeId}") {
        fun createRoute(recipeId: Long) = "recipe_detail_screen/$recipeId"
    }
    object ProfileScreen : AppScreens("profile_screen")

    // --- NUEVA RUTA: Perfil Público ---
    object PublicProfileScreen : AppScreens("public_profile_screen/{userId}") {
        fun createRoute(userId: Long) = "public_profile_screen/$userId"
    }
}