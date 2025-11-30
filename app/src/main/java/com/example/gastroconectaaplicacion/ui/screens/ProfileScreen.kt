package com.example.gastroconectaaplicacion.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.RestaurantMenu
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.gastroconectaaplicacion.data.model.Recipe
import com.example.gastroconectaaplicacion.ui.components.RecipeCard
import com.example.gastroconectaaplicacion.ui.navigation.AppScreens
import com.example.gastroconectaaplicacion.ui.viewmodel.AuthViewModel
import com.example.gastroconectaaplicacion.ui.viewmodel.RecipeViewModel

@Composable
fun ProfileScreen(
    navController: NavController,
    authViewModel: AuthViewModel,
    recipeViewModel: RecipeViewModel // <--- 1. AGREGAMOS ESTE PARÁMETRO
) {

    val currentUser by authViewModel.currentUser.collectAsState()

    // Obtenemos todas las recetas para poder filtrarlas
    val allRecipes by recipeViewModel.recipes.collectAsState()

    // Comprobar si el usuario está logueado
    LaunchedEffect(currentUser) {
        if (currentUser == null) {
            navController.navigate(AppScreens.LoginScreen.route) {
                popUpTo(0) { inclusive = true }
            }
        }
    }

    currentUser?.let { user ->
        // --- LÓGICA DE FILTRADO ---
        // 1. Mis Recetas: Las que yo escribí (autorId coincide con mi ID)
        val misRecetas = remember(allRecipes, user.id) {
            allRecipes.filter { it.autorId == user.id }
        }

        // 2. Guardadas: Las que están en mi lista "recetario"
        val recetasGuardadas = remember(allRecipes, user.recetario) {
            allRecipes.filter { user.recetario.contains(it.id) }
        }

        // Estado para controlar qué pestaña está activa (0 = Mis Recetas, 1 = Guardadas)
        var selectedTabIndex by remember { mutableIntStateOf(0) }
        val tabs = listOf("Mis Recetas", "Guardadas")

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 16.dp), // Quitamos padding horizontal global para que las tabs lleguen al borde
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // --- ENCABEZADO DEL PERFIL ---
            Column(
                modifier = Modifier.padding(horizontal = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("Mi Perfil", style = MaterialTheme.typography.headlineMedium)
                Spacer(modifier = Modifier.height(8.dp))
                Text("Hola, ${user.name}", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Text(user.email, style = MaterialTheme.typography.bodyMedium, color = Color.Gray)

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = { authViewModel.logout() },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Cerrar Sesión")
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // --- PESTAÑAS (TABS) ---
            TabRow(selectedTabIndex = selectedTabIndex) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTabIndex == index,
                        onClick = { selectedTabIndex = index },
                        text = { Text(title) },
                        icon = {
                            Icon(
                                imageVector = if (index == 0) Icons.Filled.RestaurantMenu else Icons.Filled.BookmarkBorder,
                                contentDescription = null
                            )
                        }
                    )
                }
            }

            // --- CONTENIDO DE LA LISTA ---
            Box(modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp)) {
                when (selectedTabIndex) {
                    0 -> RecipeListContent(
                        recipes = misRecetas,
                        emptyMessage = "No has publicado ninguna receta aún.",
                        navController = navController
                    )
                    1 -> RecipeListContent(
                        recipes = recetasGuardadas,
                        emptyMessage = "No has guardado ninguna receta.",
                        navController = navController
                    )
                }
            }
        }
    } ?: run {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    }
}

// Componente auxiliar para mostrar la lista o el mensaje vacío
@Composable
fun RecipeListContent(
    recipes: List<Recipe>,
    emptyMessage: String,
    navController: NavController
) {
    if (recipes.isEmpty()) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = emptyMessage,
                style = MaterialTheme.typography.bodyLarge,
                color = Color.Gray,
                textAlign = TextAlign.Center
            )
        }
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(top = 8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(bottom = 16.dp)
        ) {
            items(recipes) { recipe ->
                RecipeCard(
                    recipe = recipe,
                    onClick = {
                        navController.navigate(AppScreens.RecipeDetailScreen.createRoute(recipe.id))
                    }
                )
            }
        }
    }
}