package com.example.gastroconectaaplicacion.ui.screens

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Base64
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.launch // Importante
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt // Icono de cámara
import androidx.compose.material.icons.filled.Image // Icono de galería
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap // Para mostrar el Bitmap de la cámara
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

    // Imagen (URI para galería, Bitmap para cámara)
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    var cameraBitmap by remember { mutableStateOf<Bitmap?>(null) } // Nuevo estado para la foto de cámara
    var fotoBase64 by remember { mutableStateOf("") }

    // Datos
    var ingredientesText by remember { mutableStateOf("") }
    var pasosText by remember { mutableStateOf("") }
    var etiquetasSeleccionadas by remember { mutableStateOf<List<String>>(emptyList()) }

    var error by remember { mutableStateOf<String?>(null) }

    // 1. Selector de GALERÍA
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        imageUri = uri
        cameraBitmap = null // Limpiamos la cámara si elige galería
        if (uri != null) {
            fotoBase64 = uriToBase64(context, uri) ?: ""
        }
    }

    // 2. Selector de CÁMARA
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { bitmap ->
        if (bitmap != null) {
            cameraBitmap = bitmap
            imageUri = null // Limpiamos galería si toma foto
            fotoBase64 = bitmapToBase64(bitmap) // Convertimos el bitmap directo
        }
    }

    // 3. Permiso de CÁMARA
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            cameraLauncher.launch()
        } else {
            // Opcional: Mostrar aviso de que se necesita permiso
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

        // --- BOTONES PARA FOTO (Fila) ---
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            // Botón Galería
            Button(
                onClick = { galleryLauncher.launch("image/*") },
                modifier = Modifier.weight(1f)
            ) {
                Icon(Icons.Filled.Image, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text("Galería")
            }

            // Botón Cámara
            Button(
                onClick = { permissionLauncher.launch(android.Manifest.permission.CAMERA) },
                modifier = Modifier.weight(1f)
            ) {
                Icon(Icons.Filled.CameraAlt, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text("Cámara")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // --- PREVISUALIZACIÓN DE IMAGEN ---
        if (imageUri != null) {
            // Si viene de galería
            Image(
                painter = rememberAsyncImagePainter(imageUri),
                contentDescription = null,
                modifier = Modifier.height(200.dp).fillMaxWidth()
            )
        } else if (cameraBitmap != null) {
            // Si viene de cámara
            Image(
                bitmap = cameraBitmap!!.asImageBitmap(),
                contentDescription = null,
                modifier = Modifier.height(200.dp).fillMaxWidth()
            )
        } else {
            // Placeholder vacío
            Box(
                modifier = Modifier
                    .height(200.dp)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Text("Sin imagen seleccionada", color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

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

        // --- ETIQUETAS ---
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

        // --- BOTÓN PUBLICAR ---
        Button(
            onClick = {
                val user = currentUser ?: return@Button

                if (titulo.isBlank() || descripcion.isBlank()) {
                    error = "El título y la descripción son obligatorios"
                    return@Button
                }

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

// --- HELPERS PARA IMÁGENES ---

fun uriToBase64(context: Context, uri: Uri): String? {
    return try {
        val inputStream: InputStream? = context.contentResolver.openInputStream(uri)
        val originalBitmap = BitmapFactory.decodeStream(inputStream)
        // Redimensionamos antes de convertir
        val resizedBitmap = resizeBitmap(originalBitmap, 1024)
        bitmapToBase64(resizedBitmap)
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}

fun bitmapToBase64(bitmap: Bitmap): String {
    // Redimensionamos por seguridad (por si viene directo de cámara muy grande)
    val resizedBitmap = resizeBitmap(bitmap, 1024)

    val outputStream = ByteArrayOutputStream()
    // Calidad 60 es suficiente para web/móvil
    resizedBitmap.compress(Bitmap.CompressFormat.JPEG, 60, outputStream)
    val byteArray = outputStream.toByteArray()
    return "data:image/jpeg;base64," + Base64.encodeToString(byteArray, Base64.NO_WRAP)
}

// Función mágica para reducir el tamaño en píxeles
fun resizeBitmap(source: Bitmap, maxLength: Int): Bitmap {
    try {
        if (source.height <= maxLength && source.width <= maxLength) {
            return source // Ya es pequeña, no hacer nada
        }

        val aspectRatio = source.width.toDouble() / source.height.toDouble()
        val targetWidth: Int
        val targetHeight: Int

        if (source.height >= source.width) {
            // Es vertical
            targetHeight = maxLength
            targetWidth = (maxLength * aspectRatio).toInt()
        } else {
            // Es horizontal
            targetWidth = maxLength
            targetHeight = (maxLength / aspectRatio).toInt()
        }

        return Bitmap.createScaledBitmap(source, targetWidth, targetHeight, true)
    } catch (e: Exception) {
        return source // Si falla, devolvemos la original
    }
}