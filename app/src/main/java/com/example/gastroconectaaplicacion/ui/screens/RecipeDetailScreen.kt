package com.example.gastroconectaaplicacion.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.gastroconectaaplicacion.R
import com.example.gastroconectaaplicacion.data.model.Comentario
import com.example.gastroconectaaplicacion.data.model.Rating
import com.example.gastroconectaaplicacion.ui.components.RatingBar
import com.example.gastroconectaaplicacion.ui.navigation.AppScreens
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

    // Estados
    val recipeState = recipeViewModel.getRecipeById(recipeId).collectAsState(initial = null)
    val recipe = recipeState.value
    val currentUser by authViewModel.currentUser.collectAsState()

    // Estado local para comentario
    var commentText by remember { mutableStateOf("") }

    if (recipe == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    } else {
        val isLiked = recipe.likes.contains(currentUser?.id)
        val isSaved = currentUser?.recetario?.contains(recipeId) == true
        val isMyRecipe = currentUser?.id == recipe.autorId

        // Calcular Rating
        val avgRating = remember(recipe.ratings) {
            if (recipe.ratings.isEmpty()) 0.0 else recipe.ratings.map { it.score }.average()
        }
        // CORRECCIÓN: Verificación segura de currentUser?.id
        val myRating = if (currentUser != null) {
            recipe.ratings.find { it.userId == currentUser!!.id }?.score ?: 0
        } else { 0 }

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp)
        ) {
            item {
                Text(recipe.titulo, style = MaterialTheme.typography.headlineMedium)
                Spacer(modifier = Modifier.height(8.dp))

                // --- 3. LINK AL PERFIL PÚBLICO ---
                Row(modifier = Modifier.clickable {
                    // CORRECCIÓN LÍNEA 93: autorId es Long, pero por seguridad forzamos un valor no nulo
                    navController.navigate(AppScreens.PublicProfileScreen.createRoute(recipe.autorId))
                }) {
                    Text("Por: ", style = MaterialTheme.typography.bodyMedium)
                    Text(recipe.autorNombre, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.height(4.dp))
                Text("Tiempo: ${recipe.tiempoPreparacion}", style = MaterialTheme.typography.bodySmall)

                // --- 2. SECCIÓN RATING ---
                Spacer(modifier = Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Calificación: ${String.format("%.1f", avgRating)} ", style = MaterialTheme.typography.titleMedium)
                    Text("(${recipe.ratings.size} votos)", style = MaterialTheme.typography.bodySmall)
                }
                if (currentUser != null && !isMyRecipe) {
                    RatingBar(
                        currentRating = myRating,
                        onRatingChanged = { score ->
                            // CORRECCIÓN: Aseguramos IDs no nulos
                            recipeViewModel.rateRecipe(recipe.id ?: 0L, Rating(currentUser!!.id ?: 0L, score))
                        }
                    )
                }
                // -------------------------

                Spacer(modifier = Modifier.height(16.dp))

                // Imagen Inteligente (Base64 o URL)
                Box(modifier = Modifier.fillMaxWidth().height(250.dp)) {
                    if (recipe.foto.startsWith("data:image")) {
                        val bitmap = remember(recipe.foto) {
                            try {
                                val cleanBase64 = recipe.foto.substringAfter(",")
                                val decodedBytes = android.util.Base64.decode(cleanBase64, android.util.Base64.DEFAULT)
                                android.graphics.BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
                            } catch (e: Exception) { null }
                        }
                        if (bitmap != null) {
                            Image(
                                bitmap = bitmap.asImageBitmap(),
                                contentDescription = null,
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        }
                    } else {
                        AsyncImage(
                            model = ImageRequest.Builder(LocalContext.current).data(recipe.foto).crossfade(true).build(),
                            contentDescription = null,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
                Text(recipe.descripcion)
                Spacer(modifier = Modifier.height(16.dp))

                // Botones Like / Guardar
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = {
                        // CORRECCIÓN LÍNEA 136: forzar unblocking de currentUser?.id
                        if (currentUser != null && !isMyRecipe) {
                            recipeViewModel.toggleLike(recipe.id ?: 0L, currentUser!!.id ?: 0L)
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
                        if (currentUser != null && !isMyRecipe) authViewModel.toggleSave(recipe.id ?: 0L)
                    }) {
                        Icon(
                            painter = painterResource(if (isSaved) R.drawable.ic_bookmark_filled else R.drawable.ic_bookmark_border),
                            contentDescription = "Guardar",
                            tint = if (isSaved) MaterialTheme.colorScheme.primary else LocalContentColor.current
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
                Text("Ingredientes:", style = MaterialTheme.typography.titleMedium)
                recipe.ingredientesSimples.forEach { Text("- $it") }

                Spacer(modifier = Modifier.height(16.dp))
                Text("Pasos:", style = MaterialTheme.typography.titleMedium)
                recipe.pasos.forEachIndexed { index, paso -> Text("${index + 1}. $paso") }

                Spacer(modifier = Modifier.height(24.dp))
                HorizontalDivider() // Cambio a HorizontalDivider si usas Material3 nuevo, sino Divider()
                Spacer(modifier = Modifier.height(16.dp))

                // --- 1. SECCIÓN COMENTARIOS ---
                Text("Comentarios (${recipe.comentarios.size})", style = MaterialTheme.typography.titleMedium)

                if (currentUser != null) {
                    Row(modifier = Modifier.padding(top = 8.dp), verticalAlignment = Alignment.CenterVertically) {
                        OutlinedTextField(
                            value = commentText,
                            onValueChange = { commentText = it },
                            placeholder = { Text("Escribe un comentario...") },
                            modifier = Modifier.weight(1f)
                        )
                        IconButton(onClick = {
                            if (commentText.isNotBlank()) {
                                // CORRECCIÓN LÍNEA 182: currentUser!!.id
                                val nuevoComentario = Comentario(currentUser!!.id ?: 0L, currentUser!!.name, commentText)
                                recipeViewModel.addComment(recipe.id ?: 0L, nuevoComentario)
                                commentText = "" // Limpiar input
                            }
                        }) {
                            Icon(Icons.Filled.Send, contentDescription = "Enviar")
                        }
                    }
                } else {
                    Text("Inicia sesión para comentar", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.secondary)
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Lista de comentarios
                if (recipe.comentarios.isEmpty()) {
                    Text("No hay comentarios.", style = MaterialTheme.typography.bodySmall)
                } else {
                    recipe.comentarios.forEach { comment ->
                        Card(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                            Column(modifier = Modifier.padding(8.dp)) {
                                Text(comment.autorNombre, style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                                Text(comment.texto, style = MaterialTheme.typography.bodyMedium)
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}