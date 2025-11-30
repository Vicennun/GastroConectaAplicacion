package com.example.gastroconectaaplicacion.ui.screens

import android.app.Application
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Person // Importante
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.gastroconectaaplicacion.ui.components.RecipeCard
import com.example.gastroconectaaplicacion.ui.navigation.AppScreens
import com.example.gastroconectaaplicacion.ui.viewmodel.RecipeViewModel
import com.example.gastroconectaaplicacion.ui.viewmodel.ViewModelFactory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavController,
    viewModel: RecipeViewModel
) {
    val recipes by viewModel.recipes.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("GastroConecta") },
                actions = {
                    IconButton(onClick = { navController.navigate(AppScreens.ProfileScreen.route) }) {
                        Icon(Icons.Filled.Person, contentDescription = "Mi Perfil")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { navController.navigate(AppScreens.CreateRecipeScreen.route) }) {
                Icon(Icons.Filled.Add, contentDescription = "Crear Receta")
            }
        }
    ) { paddingValues ->

        Box(modifier = Modifier.fillMaxSize().padding(paddingValues).padding(horizontal = 16.dp)) {
            if (recipes.isEmpty()) {
                // Opcional: Mostrar mensaje si está vacío pero no cargando
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // CORRECCIÓN 1: Usar '?: 0L' para la key
                    items(recipes, key = { it.id ?: 0L }) { recipe ->
                        RecipeCard(
                            recipe = recipe,
                            onClick = {
                                // CORRECCIÓN 2: Usar '?: 0L' para navegar
                                navController.navigate(AppScreens.RecipeDetailScreen.createRoute(recipe.id ?: 0L))
                            }
                        )
                    }
                }
            }
        }
    }
}