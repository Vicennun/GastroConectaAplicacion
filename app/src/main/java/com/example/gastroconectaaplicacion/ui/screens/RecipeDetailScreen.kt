package com.example.gastroconectaaplicacion.ui.screens // Verifica

import androidx.compose.ui.res.painterResource
import com.example.gastroconectaaplicacion.R
import android.app.Application
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn // Cambiado a LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage // Importa Coil si añadiste la dependencia
import coil.request.ImageRequest
import com.example.gastroconectaaplicacion.ui.viewmodel.AuthViewModel // Para obtener usuario y guardar
import com.example.gastroconectaaplicacion.ui.viewmodel.RecipeViewModel // Verifica
import com.example.gastroconectaaplicacion.ui.viewmodel.ViewModelFactory // Verifica

@Composable
fun RecipeDetailScreen(navController: NavController, recipeId: Long) {
    val context = LocalContext.current
    val application = context.applicationContext as Application
    val factory = ViewModelFactory(application)
    val recipeViewModel: RecipeViewModel = viewModel(factory = factory)
    val authViewModel: AuthViewModel = viewModel(factory = factory) // Para like/save

    // --- OBTENER RECETA POR ID ---
    // Observa el Flow que devuelve getRecipeById
    val recipeState = recipeViewModel.getRecipeById(recipeId).collectAsStateWithLifecycle(initialValue = null)
    val recipe = recipeState.value // El valor actual del Flow (Recipe? o null)

    val currentUser by authViewModel.currentUser.collectAsState()
    val userId = currentUser?.id

    // --- ESTADOS DERIVADOS para UI (Like y Guardado) ---
    val isLiked = remember(recipe, userId) { // Recalcula si receta o usuario cambian
        recipe?.likes?.contains(userId) == true
    }
    val isSaved = remember(currentUser, recipeId) { // Recalcula si usuario o ID cambian
        currentUser?.recetario?.contains(recipeId) == true
    }


    if (recipe == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    } else {
        LazyColumn( // Usamos LazyColumn por si el contenido es muy largo
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp) // Padding general
        ) {
            item {
                Text(recipe.titulo, style = MaterialTheme.typography.headlineMedium)
                Spacer(modifier = Modifier.height(8.dp))
                // TODO: Buscar nombre del autor con UserRepository
                Text("Por: Autor ID ${recipe.autorId}", style = MaterialTheme.typography.bodySmall)
                Spacer(modifier = Modifier.height(4.dp))
                Text("Tiempo: ${recipe.tiempoPreparacion}", style = MaterialTheme.typography.bodySmall)
                Spacer(modifier = Modifier.height(16.dp))

                // Imagen con Coil
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(recipe.fotoUrl)
                        .crossfade(true)
                        .build(),
                    contentDescription = recipe.titulo,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(250.dp),
                    contentScale = ContentScale.Crop
                )
                Spacer(modifier = Modifier.height(16.dp))

                Text(recipe.descripcion)
                Spacer(modifier = Modifier.height(16.dp))

                Text("Etiquetas:", style = MaterialTheme.typography.titleMedium)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(4.dp) // Espacio entre etiquetas
                ) {
                    recipe.etiquetasDieteticas.forEach { etiqueta ->
                        SuggestionChip(onClick = { /* Podrías filtrar por etiqueta */ }, label = { Text(etiqueta) })
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))

                // Botones Like / Guardar (con lógica básica)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Botón Like
                    IconButton(onClick = {
                        if (userId != null) { // Asegurarse que el usuario no es nulo
                            recipeViewModel.toggleLike(userId, recipe.id)
                        }
                    }) {
                        Icon(
                            imageVector = if (isLiked) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                            contentDescription = "Like",
                            tint = if (isLiked) MaterialTheme.colorScheme.primary else LocalContentColor.current
                        )
                    }
                    Text("${recipe.likes.size}") // Contador de Likes

                    Spacer(modifier = Modifier.width(16.dp))

                    // Botón Guardar (Corregido con XML Drawables)
                    IconButton(onClick = {
                        if (currentUser != null) {
                            authViewModel.toggleSave(recipe.id)
                        }
                    }) {
                        Icon(
                            // Usa painterResource para cargar tus XML de res/drawable
                            painter = if (isSaved) {
                                painterResource(id = R.drawable.ic_bookmark_filled)
                            } else {
                                painterResource(id = R.drawable.ic_bookmark_border)
                            },
                            contentDescription = "Guardar",
                            tint = if (isSaved) MaterialTheme.colorScheme.primary else LocalContentColor.current
                        )
                    }

                    Text(if (isSaved) "Guardado" else "Guardar")

                    // Podrías añadir botón Seguir Autor aquí
                }
                Spacer(modifier = Modifier.height(16.dp))

                Text("Ingredientes:", style = MaterialTheme.typography.titleMedium)
                Column { // Para que cada Text ocupe una línea
                    recipe.ingredientes.forEach { ing ->
                        Text("- ${ing.cantidad} ${ing.nombre}")
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))

                Text("Pasos:", style = MaterialTheme.typography.titleMedium)
                Column {
                    recipe.pasos.forEachIndexed { index, paso ->
                        Text("${index + 1}. $paso")
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))

                Text("Comentarios:", style = MaterialTheme.typography.titleMedium)
                // TODO: Implementar sección de comentarios (Formulario + Lista)
                Text("(Sección de comentarios pendiente)")
                Spacer(modifier = Modifier.height(16.dp)) // Espacio al final
            }
        }
    }
}