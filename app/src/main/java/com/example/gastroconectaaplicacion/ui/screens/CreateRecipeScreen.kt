package com.example.gastroconectaaplicacion.ui.screens

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Base64
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
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
import coil.compose.rememberAsyncImagePainter // Necesitas agregar Coil a build.gradle si no lo tienes, o usar lógica nativa
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
    recipeViewModel: RecipeViewModel // Inyectamos este también
) {
    val currentUser by authViewModel.currentUser.collectAsState()
    val context = LocalContext.current

    // Estados del formulario
    var titulo by remember { mutableStateOf("") }
    var descripcion by remember { mutableStateOf("") }
    var tiempo by remember { mutableStateOf("") }

    // IMAGEN
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    var fotoBase64 by remember { mutableStateOf("") }

    var ingredientesText by remember { mutableStateOf("") }
    var pasosText by remember { mutableStateOf("") }
    var etiquetasSeleccionadas by remember { mutableStateOf<List<String>>(emptyList()) }

    // Selector de imágenes
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
            // Muestra previa (requiere librería Coil: io.coil-kt:coil-compose:2.4.0)
            // Si no tienes Coil, puedes quitar este bloque Image
            Image(
                painter = rememberAsyncImagePainter(imageUri),
                contentDescription = null,
                modifier = Modifier.height(200.dp).fillMaxWidth()
            )
        }

        OutlinedTextField(value = titulo, onValueChange = { titulo = it }, label = { Text("Título") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = descripcion, onValueChange = { descripcion = it }, label = { Text("Descripción") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = tiempo, onValueChange = { tiempo = it }, label = { Text("Tiempo") }, modifier = Modifier.fillMaxWidth())

        OutlinedTextField(
            value = ingredientesText,
            onValueChange = { ingredientesText = it },
            label = { Text("Ingredientes (Nombre - Cantidad por línea)") },
            modifier = Modifier.fillMaxWidth().height(100.dp)
        )

        OutlinedTextField(
            value = pasosText,
            onValueChange = { pasosText = it },
            label = { Text("Pasos (uno por línea)") },
            modifier = Modifier.fillMaxWidth().height(100.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = {
                val user = currentUser ?: return@Button

                // 1. Convertir el texto de ingredientes a Lista de Strings
                // El backend espera: ["Harina - 1 taza", "Huevos - 2"]
                val listaIngredientes = ingredientesText.lines()
                    .filter { it.isNotBlank() }
                    .map { it.trim() }

                val listaPasos = pasosText.lines()
                    .filter { it.isNotBlank() }
                    .map { it.trim() }

                // Usamos la foto Base64 si existe, sino un placeholder
                val fotoFinal = if (fotoBase64.isNotBlank()) fotoBase64 else "https://via.placeholder.com/300"

                val receta = Recipe(
                    titulo = titulo,
                    descripcion = descripcion,
                    tiempoPreparacion = tiempo,
                    autorId = user.id,

                    // CORRECCIÓN 1: user.name
                    autorNombre = user.name,

                    // CORRECCIÓN 2: fotoFinal en el campo 'foto'
                    foto = fotoFinal,

                    // CORRECCIÓN 3: Asignar a 'ingredientesSimples'
                    ingredientesSimples = listaIngredientes,

                    pasos = listaPasos,
                    etiquetasDieteticas = etiquetasSeleccionadas
                    // El resto de campos (ratings, comentarios) tienen valores por defecto en el modelo
                )

                recipeViewModel.addRecipe(receta)
                navController.popBackStack()
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
        // Comprimimos a JPEG calidad 50 para que no sea gigante
        bitmap.compress(Bitmap.CompressFormat.JPEG, 50, outputStream)
        val byteArray = outputStream.toByteArray()
        "data:image/jpeg;base64," + Base64.encodeToString(byteArray, Base64.NO_WRAP)
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}