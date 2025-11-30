package com.example.gastroconectaaplicacion.ui.components

import android.graphics.BitmapFactory
import android.util.Base64
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.gastroconectaaplicacion.data.model.Recipe

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecipeCard(
    recipe: Recipe,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Column {
            // LÓGICA INTELIGENTE DE IMAGEN
            Box(modifier = Modifier.fillMaxWidth().height(180.dp)) {
                if (recipe.foto.startsWith("data:image")) {
                    // CASO 1: Es Base64 (Foto subida desde celular)
                    val bitmap = remember(recipe.foto) {
                        try {
                            val cleanBase64 = recipe.foto.substringAfter(",")
                            val decodedBytes = Base64.decode(cleanBase64, Base64.DEFAULT)
                            BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
                        } catch (e: Exception) { null }
                    }

                    if (bitmap != null) {
                        Image(
                            bitmap = bitmap.asImageBitmap(),
                            contentDescription = recipe.titulo,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    }
                } else {
                    // CASO 2: Es URL (Foto de internet)
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(recipe.foto)
                            .crossfade(true)
                            .build(),
                        contentDescription = recipe.titulo,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                }
            }

            Column(Modifier.padding(16.dp)) {
                Text(recipe.titulo, style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(4.dp))
                Text("Tiempo: ${recipe.tiempoPreparacion}", style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}