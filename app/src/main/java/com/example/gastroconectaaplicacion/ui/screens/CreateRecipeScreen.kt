package com.example.gastroconectaaplicacion.ui.screens

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Base64
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.launch
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit // Icono opcional
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
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

    var titulo by remember { mutableStateOf("") }
    var descripcion by remember { mutableStateOf("") }
    var tiempo by remember { mutableStateOf("") }

    // FOTO
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    var imageBitmap by remember { mutableStateOf<Bitmap?>(null) } // Para mostrar foto de cámara
    var fotoBase64 by remember { mutableStateOf("") }

    var ingredientesText by remember { mutableStateOf("") }
    var pasosText by remember { mutableStateOf("") }
    var etiquetasSeleccionadas by remember { mutableStateOf<List<String>>(emptyList()) }
    var error by remember { mutableStateOf<String?>(null) }

    // 1. LANZADOR GALERÍA
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            imageUri = uri
            imageBitmap = null // Limpiamos bitmap si usa URI
            fotoBase64 = uriToBase64(context, uri) ?: ""
        }
    }

    // 2. LANZADOR CÁMARA
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { bitmap: Bitmap? ->
        if (bitmap != null) {
            imageBitmap = bitmap
            imageUri = null
            fotoBase64 = bitmapToBase64(bitmap) ?: ""
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

        // --- BOTONES DE FOTO ---
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
            Button(onClick = { galleryLauncher.launch("image/*") }) {
                Text("Abrir Galería")
            }
            Button(onClick = { cameraLauncher.launch() }) { // Lanza la cámara directa
                Text("Tomar Foto")
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // PREVISUALIZACIÓN
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .padding(4.dp),
            contentAlignment = Alignment.Center
        ) {
            if (imageBitmap != null) {
                Image(
                    bitmap = imageBitmap!!.asImageBitmap(),
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize()
                )
            } else if (imageUri != null) {
                Image(
                    painter = rememberAsyncImagePainter(imageUri),
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                Text("Sin imagen seleccionada", color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }

        // --- CAMPOS ---
        OutlinedTextField(value = titulo, onValueChange = { titulo = it }, label = { Text("Título") }, modifier = Modifier.fillMaxWidth())
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(value = descripcion, onValueChange = { descripcion = it }, label = { Text("Descripción") }, modifier = Modifier.fillMaxWidth())
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(value = tiempo, onValueChange = { tiempo = it }, label = { Text("Tiempo") }, modifier = Modifier.fillMaxWidth())
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(value = ingredientesText, onValueChange = { ingredientesText = it }, label = { Text("Ingredientes (Uno por línea)") }, modifier = Modifier.fillMaxWidth().height(100.dp))
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(value = pasosText, onValueChange = { pasosText = it }, label = { Text("Pasos (Uno por línea)") }, modifier = Modifier.fillMaxWidth().height(100.dp))

        Spacer(modifier = Modifier.height(16.dp))

        // ETIQUETAS CLICKEABLES
        Text("Etiquetas Dietéticas:", style = MaterialTheme.typography.titleMedium)
        Column {
            OPCIONES_DIETETICAS.chunked(2).forEach { rowOptions ->
                Row(modifier = Modifier.fillMaxWidth()) {
                    rowOptions.forEach { option ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .weight(1f)
                                .clickable {
                                    etiquetasSeleccionadas = if (etiquetasSeleccionadas.contains(option)) {
                                        etiquetasSeleccionadas - option
                                    } else {
                                        etiquetasSeleccionadas + option
                                    }
                                }
                                .padding(8.dp)
                        ) {
                            Checkbox(
                                checked = etiquetasSeleccionadas.contains(option),
                                onCheckedChange = null
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(option, style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                    if (rowOptions.size < 2) Spacer(modifier = Modifier.weight(1f))
                }
            }
        }

        if (error != null) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(error!!, color = MaterialTheme.colorScheme.error)
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                val user = currentUser ?: return@Button
                if (titulo.isBlank() || descripcion.isBlank()) {
                    error = "Título y descripción obligatorios"
                    return@Button
                }

                val listaIngredientes = ingredientesText.lines().filter { it.isNotBlank() }.map { it.trim() }
                val listaPasos = pasosText.lines().filter { it.isNotBlank() }.map { it.trim() }
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

                recipeViewModel.addRecipe(receta) { exito ->
                    if (exito) navController.popBackStack()
                    else error = "Error al subir. Intenta de nuevo."
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Publicar")
        }
    }
}

// --- UTILS ---

fun uriToBase64(context: Context, uri: Uri): String? {
    return try {
        val inputStream = context.contentResolver.openInputStream(uri) ?: return null
        val bitmap = BitmapFactory.decodeStream(inputStream)
        inputStream.close()
        bitmapToBase64(bitmap)
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}

fun bitmapToBase64(bitmap: Bitmap): String? {
    return try {
        val outputStream = ByteArrayOutputStream()
        // Comprimir a JPEG 50%
        bitmap.compress(Bitmap.CompressFormat.JPEG, 50, outputStream)
        val byteArray = outputStream.toByteArray()
        "data:image/jpeg;base64," + Base64.encodeToString(byteArray, Base64.NO_WRAP)
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}