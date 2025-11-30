package com.example.gastroconectaaplicacion.ui.screens // Verifica

import android.app.Application
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.gastroconectaaplicacion.ui.components.RecipeCard // <-- IMPORTA TU CARD
import com.example.gastroconectaaplicacion.ui.navigation.AppScreens // Verifica
import com.example.gastroconectaaplicacion.ui.viewmodel.RecipeViewModel // Verifica
import com.example.gastroconectaaplicacion.ui.viewmodel.ViewModelFactory // Verifica

@OptIn(ExperimentalMaterial3Api::class) // Para Scaffold y FAB
@Composable
fun HomeScreen(
    navController: NavController,
    viewModel: RecipeViewModel // <--- CAMBIO
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
            // Aquí podrías añadir icono de búsqueda/filtros
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { navController.navigate(AppScreens.CreateRecipeScreen.route) }) {
                Icon(Icons.Filled.Add, contentDescription = "Crear Receta")
            }
        }
    ) { paddingValues -> // paddingValues contiene el padding necesario por TopAppBar/FAB

        Box(modifier = Modifier.fillMaxSize().padding(paddingValues).padding(horizontal = 16.dp)) { // Aplica padding
            if (recipes.isEmpty()) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(8.dp) // Espacio entre cards
                ) {
                    items(recipes, key = { it.id }) { recipe -> // Usa ID como key para eficiencia
                        RecipeCard(
                            recipe = recipe,
                            onClick = {
                                // Navega al detalle pasando el ID
                                navController.navigate(AppScreens.RecipeDetailScreen.createRoute(recipe.id))
                            }
                        )
                    }
                }
            }
            // Aquí irían los filtros si los implementas
        }
    }
}