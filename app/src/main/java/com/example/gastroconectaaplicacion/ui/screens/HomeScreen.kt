package com.example.gastroconectaaplicacion.ui.screens // Verifica este paquete

import android.app.Application
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.gastroconectaaplicacion.ui.viewmodel.RecipeViewModel // Verifica este import
import com.example.gastroconectaaplicacion.ui.viewmodel.ViewModelFactory // Verifica este import

@Composable
fun HomeScreen(navController: NavController) {
    // Obtener ViewModel
    val context = LocalContext.current
    val application = context.applicationContext as Application
    val viewModel: RecipeViewModel = viewModel(factory = ViewModelFactory(application))

    // Observar la lista de recetas
    val recipes by viewModel.allRecipes.collectAsState()

    Box(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        if (recipes.isEmpty()) {
            // Muestra indicador de carga o texto si no hay recetas
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            // Text("No hay recetas disponibles.", modifier = Modifier.align(Alignment.Center))
        } else {
            // Muestra la lista de recetas (muy básico)
            LazyColumn {
                items(recipes) { recipe ->
                    Text("Receta: ${recipe.titulo}") // Aquí iría tu RecipeCard Composable
                    // Divider() // Para separar items
                }
            }
        }
        // Aquí podrías añadir botones flotantes, filtros, etc.
    }
}