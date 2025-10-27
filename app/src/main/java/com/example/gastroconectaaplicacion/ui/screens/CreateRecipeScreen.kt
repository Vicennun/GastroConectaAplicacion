package com.example.gastroconectaaplicacion.ui.screens // Verifica

import android.app.Application
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.gastroconectaaplicacion.data.model.Recipe
import com.example.gastroconectaaplicacion.data.model.Ingredients // Importa Ingredients
import com.example.gastroconectaaplicacion.ui.navigation.AppScreens // Verifica
import com.example.gastroconectaaplicacion.ui.viewmodel.AuthViewModel // Necesitamos el usuario actual
import com.example.gastroconectaaplicacion.ui.viewmodel.RecipeViewModel // Necesitamos añadir receta
import com.example.gastroconectaaplicacion.ui.viewmodel.ViewModelFactory // Verifica

// Lista fija como en tu app React
val OPCIONES_DIETETICAS = listOf(
    "Sin Gluten", "Vegano", "Vegetariano", "Sin Lácteos", "Bajo en Azúcar", "Apto para Diabéticos"
)

@Composable
fun CreateRecipeScreen(navController: NavController) {
    val context = LocalContext.current
    val application = context.applicationContext as Application
    val factory = ViewModelFactory(application)
    val authViewModel: AuthViewModel = viewModel(factory = factory)
    val recipeViewModel: RecipeViewModel = viewModel(factory = factory)

    val currentUser by authViewModel.currentUser.collectAsState()

    // Estados para el formulario
    var titulo by remember { mutableStateOf("") }
    var descripcion by remember { mutableStateOf("") }
    var tiempo by remember { mutableStateOf("") }
    var fotoUrl by remember { mutableStateOf("") }
    var ingredientesText by remember { mutableStateOf("") } // Texto plano para el TextField
    var pasosText by remember { mutableStateOf("") }       // Texto plano para el TextField
    var etiquetasSeleccionadas by remember { mutableStateOf<List<String>>(emptyList()) }
    var error by remember { mutableStateOf<String?>(null) }
    var exito by remember { mutableStateOf(false) }

    // Función para manejar el envío
    fun handleSubmit() {
        error = null
        exito = false
        val user = currentUser ?: return // No debería pasar si la pantalla está protegida

        if (titulo.isBlank() || descripcion.isBlank() || ingredientesText.isBlank() || pasosText.isBlank()) {
            error = "Título, Descripción, Ingredientes y Pasos son obligatorios."
            return
        }

        // Procesar ingredientes (simplificado, asume formato Nombre,Cantidad por línea)
        val ingredientesList = ingredientesText.lines()
            .filter { it.contains(',') }
            .mapNotNull { line ->
                val parts = line.split(',', limit = 2)
                if (parts.size == 2) Ingredients(nombre = parts[0].trim(), cantidad = parts[1].trim()) else null
            }

        // Procesar pasos
        val pasosList = pasosText.lines().filter { it.isNotBlank() }.map { it.trim() }

        val nuevaReceta = Recipe(
            titulo = titulo,
            descripcion = descripcion,
            tiempoPreparacion = tiempo.ifBlank { "N/A" },
            fotoUrl = fotoUrl.ifBlank { "https://via.placeholder.com/300x200.png?text=Sin+Foto" },
            autorId = user.id,
            ingredientes = ingredientesList,
            pasos = pasosList,
            etiquetasDieteticas = etiquetasSeleccionadas
            // Likes y Comentarios se inicializan vacíos por defecto en Room o el ViewModel
        )

        recipeViewModel.addRecipe(nuevaReceta)
        exito = true // Mostrar mensaje de éxito (simplificado)
        // Podrías navegar después de un delay
        // navController.popBackStack() // Volver atrás
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()) // Para que quepa en pantallas pequeñas
    ) {
        Text("Crear Nueva Receta", style = MaterialTheme.typography.headlineMedium, modifier = Modifier.align(Alignment.CenterHorizontally))
        Spacer(modifier = Modifier.height(16.dp))

        if (error != null) {
            AlertDialog(
                onDismissRequest = { error = null },
                title = { Text("Error") },
                text = { Text(error ?: "") },
                confirmButton = { Button(onClick = { error = null }) { Text("OK") } }
            )
        }
        if (exito) {
            AlertDialog(
                onDismissRequest = {
                    exito = false
                    navController.popBackStack() // Vuelve atrás al cerrar el diálogo
                },
                title = { Text("Éxito") },
                text = { Text("Receta creada correctamente.") },
                confirmButton = { Button(onClick = {
                    exito = false
                    navController.popBackStack()
                }) { Text("OK") } }
            )
        }

        OutlinedTextField(value = titulo, onValueChange = { titulo = it }, label = { Text("Título") }, modifier = Modifier.fillMaxWidth())
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(value = descripcion, onValueChange = { descripcion = it }, label = { Text("Descripción") }, modifier = Modifier.fillMaxWidth())
        Spacer(modifier = Modifier.height(8.dp))
        Row {
            OutlinedTextField(value = tiempo, onValueChange = { tiempo = it }, label = { Text("Tiempo (ej. 30 min)") }, modifier = Modifier.weight(1f))
            Spacer(modifier = Modifier.width(8.dp))
            OutlinedTextField(value = fotoUrl, onValueChange = { fotoUrl = it }, label = { Text("URL Foto") }, modifier = Modifier.weight(1f))
        }
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(value = ingredientesText, onValueChange = { ingredientesText = it }, label = { Text("Ingredientes (Nombre,Cantidad por línea)") }, modifier = Modifier.fillMaxWidth().height(100.dp))
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(value = pasosText, onValueChange = { pasosText = it }, label = { Text("Pasos (uno por línea)") }, modifier = Modifier.fillMaxWidth().height(100.dp))
        Spacer(modifier = Modifier.height(16.dp))

        Text("Etiquetas Dietéticas:")
        // Checkboxes para etiquetas (simplificado)
        Column {
            OPCIONES_DIETETICAS.chunked(2).forEach { rowOptions -> // Muestra 2 por fila
                Row {
                    rowOptions.forEach { option ->
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                            Checkbox(
                                checked = etiquetasSeleccionadas.contains(option),
                                onCheckedChange = { isChecked ->
                                    etiquetasSeleccionadas = if (isChecked) {
                                        etiquetasSeleccionadas + option
                                    } else {
                                        etiquetasSeleccionadas - option
                                    }
                                }
                            )
                            Text(option)
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = ::handleSubmit, modifier = Modifier.fillMaxWidth()) {
            Text("Publicar Receta")
        }
    }
}