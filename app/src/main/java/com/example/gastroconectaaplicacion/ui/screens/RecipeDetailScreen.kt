package com.example.gastroconectaaplicacion.ui.screens

import androidx.compose.ui.res.painterResource
import com.example.gastroconectaaplicacion.R
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items // Importante para la lista de comentarios
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Send // Icono para enviar
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.material3.LocalContentColor
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.gastroconectaaplicacion.data.model.Comentario // Importa tu modelo
import com.example.gastroconectaaplicacion.ui.viewmodel.AuthViewModel
import com.example.gastroconectaaplicacion.ui.viewmodel.RecipeViewModel

@Composable
fun RecipeDetailScreen(
    navController: NavController,
    recipeId: Long,
    authViewModel: AuthViewModel,
    recipeViewModel: RecipeViewModel
) {
    // --- OBTENER RECETA POR ID ---
    val recipeState = recipeViewModel.getRecipeById(recipeId).collectAsState(initial = null)
    val recipe = recipeState.value

    val currentUser by authViewModel.currentUser.collectAsState()
    val userId = currentUser?.id

    // Estado local para el texto del nuevo comentario
    var commentText by remember { mutableStateOf("") }
    // Estado para saber si se está enviando (opcional, para feedback visual)
    var isSendingComment by remember { mutableStateOf(false) }

    // --- ESTADOS DERIVADOS ---
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
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp) // Espacio vertical entre items
        ) {
            // --- BLOQUE 1: INFORMACIÓN PRINCIPAL (Header, Foto, Descripción) ---
            item {
                Text(recipe.titulo, style = MaterialTheme.typography.headlineMedium)
                Spacer(modifier = Modifier.height(8.dp))
                Text("Por: ${recipe.autorNombre}", style = MaterialTheme.typography.bodySmall)
                Text("Tiempo: ${recipe.tiempoPreparacion}", style = MaterialTheme.typography.bodySmall)
                Spacer(modifier = Modifier.height(16.dp))

                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(recipe.foto)
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

                // Etiquetas
                Text("Etiquetas:", style = MaterialTheme.typography.titleMedium)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    recipe.etiquetasDieteticas.forEach { etiqueta ->
                        SuggestionChip(onClick = { }, label = { Text(etiqueta) })
                    }
                }

                // Botones de Acción (Like, Guardar)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = {
                        if (userId != null) recipeViewModel.toggleLike(recipe.id, userId)
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
                        if (currentUser != null) authViewModel.toggleSave(recipe.id)
                    }) {
                        Icon(
                            painter = if (isSaved) painterResource(id = R.drawable.ic_bookmark_filled) else painterResource(id = R.drawable.ic_bookmark_border),
                            contentDescription = "Guardar",
                            tint = if (isSaved) MaterialTheme.colorScheme.primary else LocalContentColor.current
                        )
                    }
                    Text(if (isSaved) "Guardado" else "Guardar")
                }
            }

            // --- BLOQUE 2: INGREDIENTES ---
            item {
                Text("Ingredientes:", style = MaterialTheme.typography.titleMedium)
                Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
                    Column(modifier = Modifier.padding(16.dp).fillMaxWidth()) {
                        recipe.ingredientesSimples.forEach { ingTexto ->
                            Text("• $ingTexto", modifier = Modifier.padding(vertical = 2.dp))
                        }
                    }
                }
            }

            // --- BLOQUE 3: PASOS ---
            item {
                Text("Pasos:", style = MaterialTheme.typography.titleMedium)
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    recipe.pasos.forEachIndexed { index, paso ->
                        Row(modifier = Modifier.fillMaxWidth()) {
                            Text(
                                text = "${index + 1}.",
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.width(24.dp)
                            )
                            Text(text = paso)
                        }
                    }
                }
            }

            // --- BLOQUE 4: SECCIÓN DE COMENTARIOS ---
            item {
                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                Text("Comentarios (${recipe.comentarios.size})", style = MaterialTheme.typography.titleLarge)
            }

            // A) Formulario para agregar comentario
            item {
                if (currentUser != null) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = commentText,
                            onValueChange = { commentText = it },
                            placeholder = { Text("Escribe un comentario...") },
                            modifier = Modifier.weight(1f),
                            maxLines = 3
                        )
                        IconButton(
                            onClick = {
                                if (commentText.isNotBlank()) {
                                    isSendingComment = true
                                    // Crear objeto Comentario
                                    val nuevoComentario = Comentario(
                                        autorId = currentUser!!.id,
                                        autorNombre = currentUser!!.name,
                                        texto = commentText
                                    )
                                    // Llamar al ViewModel
                                    recipeViewModel.addComment(recipe.id, nuevoComentario)

                                    // Limpiar UI
                                    commentText = ""
                                    isSendingComment = false
                                }
                            },
                            enabled = commentText.isNotBlank() && !isSendingComment
                        ) {
                            Icon(Icons.Default.Send, contentDescription = "Enviar")
                        }
                    }
                } else {
                    Text(
                        text = "Inicia sesión para dejar un comentario.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Gray
                    )
                }
            }

            // B) Lista de comentarios existentes
            if (recipe.comentarios.isEmpty()) {
                item {
                    Text("Sé el primero en comentar.", style = MaterialTheme.typography.bodyMedium, modifier = Modifier.padding(vertical = 8.dp))
                }
            } else {
                items(recipe.comentarios) { comentario ->
                    CommentItem(comentario)
                }
            }
        }
    }
}

// Componente individual para cada comentario
@Composable
fun CommentItem(comentario: Comentario) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(
                text = comentario.autorNombre,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = comentario.texto,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}