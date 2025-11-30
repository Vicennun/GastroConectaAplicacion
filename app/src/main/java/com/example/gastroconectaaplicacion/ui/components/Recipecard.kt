package com.example.gastroconectaaplicacion.ui.components // Verifica

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage // Necesitarás añadir la dependencia de Coil
import coil.request.ImageRequest
import com.example.gastroconectaaplicacion.data.model.Recipe // Verifica

@OptIn(ExperimentalMaterial3Api::class) // Para Card
@Composable
fun RecipeCard(
    recipe: Recipe,
    onClick: () -> Unit // Acción al hacer clic
) {
    Card(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Column {
            // Imagen (usando Coil - necesita dependencia)
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(recipe.foto)
                    .crossfade(true)
                    // .placeholder(R.drawable.placeholder) // Opcional: imagen de carga
                    // .error(R.drawable.error) // Opcional: imagen de error
                    .build(),
                contentDescription = recipe.titulo,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp),
                contentScale = ContentScale.Crop // Escala la imagen para llenar el espacio
            )

            // Contenido de texto
            Column(Modifier.padding(16.dp)) {
                Text(recipe.titulo, style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(4.dp))
                Text("Tiempo: ${recipe.tiempoPreparacion}", style = MaterialTheme.typography.bodySmall)
                // Podrías añadir el nombre del autor aquí si lo buscas
                // Text("Por: ${authorName}", style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}

// --- Añade la dependencia de Coil ---
// 1. Abre gradle/libs.versions.toml
// 2. En [versions], añade: coil = "2.6.0" (o la última versión)
// 3. En [libraries], añade: coil-compose = { group = "io.coil-kt", name = "coil-compose", version.ref = "coil" }
// 4. Abre app/build.gradle.kts
// 5. En dependencies, añade: implementation(libs.coil.compose)
// 6. Sync Gradle