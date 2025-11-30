package com.example.gastroconectaaplicacion.ui.screens

import android.app.Application
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.gastroconectaaplicacion.R
import com.example.gastroconectaaplicacion.ui.viewmodel.AuthViewModel
import com.example.gastroconectaaplicacion.ui.viewmodel.RecipeViewModel
import com.example.gastroconectaaplicacion.ui.viewmodel.ViewModelFactory

@Composable
fun RecipeDetailScreen(
    navController: NavController,
    recipeId: Long,
    authViewModel: AuthViewModel,
    recipeViewModel: RecipeViewModel
) {
    val context = LocalContext.current
    // Ya no necesitamos crear factories aquí, recibimos los viewmodels

    // Usamos collectAsState normal
    val recipeState = recipeViewModel.getRecipeById(recipeId).collectAsState(initial = null)
    val recipe = recipeState.value

    val currentUser by authViewModel.currentUser.collectAsState()
    val userId = currentUser?.id

    val isLiked = remember(recipe, userId) {
        recipe?.likes?.contains(userId) == true
    }
    val isSaved = remember(currentUser, recipeId) {
        currentUser?.recetario?.contains(recipeId) == true
    }

    if (recipe == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp)
        ) {
            item {
                Text(recipe.titulo, style = MaterialTheme.typography.headlineMedium)
                Spacer(modifier = Modifier.height(8.dp))

                Text("Por: ${recipe.autorNombre}", style = MaterialTheme.typography.bodySmall)
                Spacer(modifier = Modifier.height(4.dp))
                Text("Tiempo: ${recipe.tiempoPreparacion}", style = MaterialTheme.typography.bodySmall)
                Spacer(modifier = Modifier.height(16.dp))

                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(recipe.foto)
                        .crossfade(true)
                        .build(),
                    contentDescription = recipe.titulo,
                    modifier = Modifier.fillMaxWidth().height(250.dp),
                    contentScale = ContentScale.Crop
                )
                Spacer(modifier = Modifier.height(16.dp))

                Text(recipe.descripcion)
                Spacer(modifier = Modifier.height(16.dp))

                Text("Etiquetas:", style = MaterialTheme.typography.titleMedium)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    recipe.etiquetasDieteticas.forEach { etiqueta ->
                        SuggestionChip(onClick = { }, label = { Text(etiqueta) })
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))

                // Botones con CORRECCIONES (?: 0L)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = {
                        if (userId != null) {
                            // CORRECCIÓN: recipe.id ?: 0L
                            recipeViewModel.toggleLike(recipe.id ?: 0L, userId)
                        }
                    }) {
                        Icon(
                            imageVector = if (isLiked) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                            contentDescription = "Like",
                            tint = if (isLiked) MaterialTheme.colorScheme.primary else LocalContentColor.current
                        )
                    }
                    Text("${recipe.likes.size}")

                    Spacer(modifier = Modifier.width(16.dp))

                    IconButton(onClick = {
                        if (currentUser != null) {
                            // CORRECCIÓN: recipe.id ?: 0L
                            authViewModel.toggleSave(recipe.id ?: 0L)
                        }
                    }) {
                        Icon(
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
                }
                Spacer(modifier = Modifier.height(16.dp))

                Text("Ingredientes:", style = MaterialTheme.typography.titleMedium)
                Column {
                    recipe.ingredientesSimples.forEach { ingTexto ->
                        Text("- $ingTexto")
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
                Text("(Sección de comentarios pendiente)")
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}