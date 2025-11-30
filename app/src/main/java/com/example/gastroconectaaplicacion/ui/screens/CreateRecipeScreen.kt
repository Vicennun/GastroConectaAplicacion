package com.example.gastroconectaaplicacion.ui.screens

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Base64
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable // Importante para el fix de etiquetas
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.gastroconectaaplicacion.data.model.Recipe
import com.example.gastroconectaaplicacion.ui.viewmodel.AuthViewModel
import com.example.gastroconectaaplicacion.ui.viewmodel.RecipeViewModel
import java.io.ByteArrayOutputStream
import java.io.InputStream

val OPCIONES_DIETETICAS = listOf(
    "Sin Gluten", "Vegano", "Vegetariano", "Sin Lácteos", "Bajo en Azúcar", "Apto para Diabéticos"
)

@Composable
fun CreateRecipeScreen(
    navController: NavController,
    authViewModel: AuthViewModel,
    recipeViewModel: RecipeViewModel
) {
    val currentUser by authViewModel.currentUser.collectAsState()
    val context = LocalContext.current

    // --- Estados del formulario ---
    var titulo by remember { mutableStateOf("") }
    var descripcion by remember { mutableStateOf("") }
    var tiempo by remember { mutableStateOf("") }

    // Imagen
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    var fotoBase64 by remember { mutableStateOf("") }

    // Datos
    var ingredientesText by remember { mutableStateOf("") }
    var pasosText by remember { mutableStateOf("") }
    var etiquetasSeleccionadas by remember { mutableStateOf<List<String>>(emptyList()) }

    // Manejo de errores
    var error by remember { mutableStateOf<String?>(null) }

    // Selector de imágenes de la galería
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        imageUri = uri
        if (uri != null) {
            fotoBase64 = uriToBase64(context, uri) ?: ""
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text("Nueva Receta", style = MaterialTheme.typography.headlineMedium)

        Spacer(modifier = Modifier.height(16.dp))

        // --- SELECCIONAR FOTO ---
        Button(onClick = { launcher.launch("image/*") }) {
            Text(if (imageUri == null) "Seleccionar Foto" else "Cambiar Foto")
        }

        if (imageUri != null) {
            Image(
                painter = rememberAsyncImagePainter(imageUri),
                contentDescription = null,
                modifier = Modifier.height(200.dp).fillMaxWidth()
            )
        }

        // --- CAMPOS DE TEXTO ---
        OutlinedTextField(value = titulo, onValueChange = { titulo = it }, label = { Text("Título") }, modifier = Modifier.fillMaxWidth())
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(value = descripcion, onValueChange = { descripcion = it }, label = { Text("Descripción") }, modifier = Modifier.fillMaxWidth())
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(value = tiempo, onValueChange = { tiempo = it }, label = { Text("Tiempo (ej. 30 min)") }, modifier = Modifier.fillMaxWidth())
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = ingredientesText,
            onValueChange = { ingredientesText = it },
            label = { Text("Ingredientes (Uno por línea)") },
            modifier = Modifier.fillMaxWidth().height(100.dp)
        )
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = pasosText,
            onValueChange = { pasosText = it },
            label = { Text("Pasos (Uno por línea)") },
            modifier = Modifier.fillMaxWidth().height(100.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // --- ETIQUETAS (SECCIÓN CORREGIDA) ---
        Text("Etiquetas Dietéticas:", style = MaterialTheme.typography.titleMedium)

        Column {
            // Divide la lista en pares para mostrar 2 por fila
            OPCIONES_DIETETICAS.chunked(2).forEach { rowOptions ->
                Row(modifier = Modifier.fillMaxWidth()) {
                    rowOptions.forEach { option ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .weight(1f)
                                .clickable {
                                    // Lógica de selección al tocar la fila completa
                                    etiquetasSeleccionadas = if (etiquetasSeleccionadas.contains(option)) {
                                        etiquetasSeleccionadas - option
                                    } else {
                                        etiquetasSeleccionadas + option
                                    }
                                }
                                .padding(8.dp) // Área táctil más grande
                        ) {
                            Checkbox(
                                checked = etiquetasSeleccionadas.contains(option),
                                onCheckedChange = null // Desactivamos el clic propio del checkbox para evitar conflictos
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(option, style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                    // Si la fila tiene un número impar, rellenamos el espacio vacío
                    if (rowOptions.size < 2) {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }
        }

        // --- MENSAJE DE ERROR ---
        if (error != null) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(error!!, color = MaterialTheme.colorScheme.error)
        }

        Spacer(modifier = Modifier.height(16.dp))

        // --- BOTÓN PUBLICAR ---
        Button(
            onClick = {
                val user = currentUser ?: return@Button

                // Validaciones básicas
                if (titulo.isBlank() || descripcion.isBlank()) {
                    error = "El título y la descripción son obligatorios"
                    return@Button
                }

                // Convertir textos a listas
                val listaIngredientes = ingredientesText.lines().filter { it.isNotBlank() }
                val listaPasos = pasosText.lines().filter { it.isNotBlank() }
                val fotoFinal = if (fotoBase64.isNotBlank()) fotoBase64 else "https://via.placeholder.com/300"

                val receta = Recipe(
                    titulo = titulo,
                    descripcion = descripcion,
                    tiempoPreparacion = tiempo,
                    autorId = user.id,
                    autorNombre = user.name,
                    foto = fotoFinal,
                    ingredientesSimples = listaIngredientes,
                    pasos = listaPasos,
                    etiquetasDieteticas = etiquetasSeleccionadas
                )

                // Enviamos y esperamos el resultado
                recipeViewModel.addRecipe(receta) { exito ->
                    if (exito) {
                        navController.popBackStack()
                    } else {
                        error = "Error al subir la receta. Intenta de nuevo."
                    }
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Publicar")
        }
    }
}

// --- FUNCIÓN MÁGICA: Convierte URI a String Base64 ---
fun uriToBase64(context: Context, uri: Uri): String? {
    return try {
        val inputStream: InputStream? = context.contentResolver.openInputStream(uri)
        val bitmap = BitmapFactory.decodeStream(inputStream)
        val outputStream = ByteArrayOutputStream()
        // Comprimimos a JPEG calidad 50 para que no sea gigante y pase rápido por la red
        bitmap.compress(Bitmap.CompressFormat.JPEG, 50, outputStream)
        val byteArray = outputStream.toByteArray()
        "data:image/jpeg;base64," + Base64.encodeToString(byteArray, Base64.NO_WRAP)
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}