package com.example.gastroconectaaplicacion.ui.navigation // Verifica paquete

sealed class AppScreens(val route: String) {
    object LoginScreen : AppScreens("login_screen")
    object RegisterScreen : AppScreens("register_screen")
    object HomeScreen : AppScreens("home_screen") // Ruta para Home
    object CreateRecipeScreen : AppScreens("create_recipe_screen") // Ruta para Crear Receta
    // Ruta para Detalle (con argumento recipeId)
    object RecipeDetailScreen : AppScreens("recipe_detail_screen/{recipeId}") {
        // Función helper para construir la ruta al navegar
        fun createRoute(recipeId: Long) = "recipe_detail_screen/$recipeId"
    }
    object ProfileScreen : AppScreens("profile_screen") // Ruta para Mi Perfil
    // Podrías añadir otras, como ver el perfil de otro usuario
}