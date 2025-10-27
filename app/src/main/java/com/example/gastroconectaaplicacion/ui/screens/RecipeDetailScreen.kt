package com.example.gastroconectaaplicacion.ui.screens // Verifica

import android.app.Application
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle // Importante para observar Flow en detalle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.gastroconectaaplicacion.ui.viewmodel.RecipeViewModel // Verifica
import com.example.gastroconectaaplicacion.ui.viewmodel.ViewModelFactory // Verifica
// Faltaría importar Coil u otra librería para cargar imágenes desde URL

@Composable
fun RecipeDetailScreen(navController: NavController, recipeId: Long) {
    val context = LocalContext.current
    val application = context.applicationContext as Application
    val factory = ViewModelFactory(application)
    val recipeViewModel: RecipeViewModel = viewModel(factory = factory)

    // Necesitamos una función en RecipeViewModel para obtener por ID
    // val recipe by recipeViewModel.getRecipeById(recipeId).collectAsStateWithLifecycle(initialValue = null)

    // --- Versión Placeholder mientras no tengas getRecipeById ---
    val recipes by recipeViewModel.allRecipes.collectAsState()
    val recipe = recipes.find { it.id == recipeId }
    // --- Fin Placeholder ---


    if (recipe == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = androidx.compose.ui.Alignment.Center) {
            CircularProgressIndicator() // Muestra carga o no encontrado
        }
    } else {
        LazyColumn(modifier = Modifier.fillMaxSize().padding(16.dp)) {
            item {
                Text(recipe.titulo, style = MaterialTheme.typography.headlineMedium)
                Spacer(modifier = Modifier.height(8.dp))
                Text("Por: Autor ${recipe.autorId}", style = MaterialTheme.typography.bodySmall) // Falta buscar el nombre del autor
                Spacer(modifier = Modifier.height(4.dp))
                Text("Tiempo: ${recipe.tiempoPreparacion}", style = MaterialTheme.typography.bodySmall)
                Spacer(modifier = Modifier.height(16.dp))

                // Aquí iría la imagen cargada desde recipe.fotoUrl con Coil/Glide
                Box(modifier = Modifier.fillMaxWidth().height(200.dp).padding(vertical=8.dp)) {
                    Text("(Imagen de ${recipe.titulo})", modifier = Modifier.align(androidx.compose.ui.Alignment.Center))
                }


                Text(recipe.descripcion)
                Spacer(modifier = Modifier.height(16.dp))

                Text("Etiquetas:", style = MaterialTheme.typography.titleMedium)
                Row {
                    recipe.etiquetasDieteticas.forEach { etiqueta ->
                        // Podrías usar Chip o Badge de Material3
                        Text("[$etiqueta]", modifier = Modifier.padding(end = 4.dp))
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))

                // Botones Like / Guardar (Sin lógica aún)
                Row {
                    Button(onClick = { /* TODO: Implementar Like */ }) { Text("Like (?)") }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(onClick = { /* TODO: Implementar Guardar */ }) { Text("Guardar (?)") }
                }
                Spacer(modifier = Modifier.height(16.dp))

                Text("Ingredientes:", style = MaterialTheme.typography.titleMedium)
                recipe.ingredientes.forEach { ing ->
                    Text("- ${ing.cantidad} ${ing.nombre}")
                }
                Spacer(modifier = Modifier.height(16.dp))

                Text("Pasos:", style = MaterialTheme.typography.titleMedium)
                recipe.pasos.forEachIndexed { index, paso ->
                    Text("${index + 1}. $paso")
                }
                Spacer(modifier = Modifier.height(16.dp))

                Text("Comentarios:", style = MaterialTheme.typography.titleMedium)
                // Aquí iría la sección de comentarios (Formulario y lista)
                Text("(Sección de comentarios pendiente)")
            }
        }
    }
}