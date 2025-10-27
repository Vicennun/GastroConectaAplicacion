package com.example.gastroconectaaplicacion.ui.screens // Verifica

import android.app.Application
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.gastroconectaaplicacion.ui.viewmodel.AuthViewModel // Verifica
import com.example.gastroconectaaplicacion.ui.viewmodel.ViewModelFactory // Verifica

@Composable
fun ProfileScreen(navController: NavController) {
    val context = LocalContext.current
    val application = context.applicationContext as Application
    val factory = ViewModelFactory(application)
    val authViewModel: AuthViewModel = viewModel(factory = factory)

    val currentUser by authViewModel.currentUser.collectAsState()

    // Necesitaríamos RecipeViewModel para mostrar "Mis Recetas" y "Guardadas"
    // val recipeViewModel: RecipeViewModel = viewModel(factory = factory)
    // val recipes by recipeViewModel.allRecipes.collectAsState()
    // val misRecetas = recipes.filter { it.autorId == currentUser?.id }
    // val recetasGuardadas = ... (necesitaríamos el campo 'recetario' en User)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (currentUser == null) {
            Text("No estás logueado.") // O redirigir a Login
        } else {
            Text("Mi Perfil", style = MaterialTheme.typography.headlineMedium)
            Spacer(modifier = Modifier.height(16.dp))
            Text("Nombre: ${currentUser?.nombre}")
            Text("Email: ${currentUser?.email}")
            Spacer(modifier = Modifier.height(32.dp))

            // Aquí irían las Tabs (Mis Recetas, Guardadas, Siguiendo, Seguidores)
            // Implementar Tabs en Compose requiere más código (TabRow, Pager, etc.)
            Text("--- Pestañas Pendientes ---")
            Text("Mis Recetas: (Pendiente)")
            Text("Recetas Guardadas: (Pendiente)")
            Text("Siguiendo: (Pendiente)")
            Text("Seguidores: (Pendiente)")

            // Botón Logout (provisional)
            Spacer(modifier = Modifier.height(32.dp))
            Button(onClick = {
                // authViewModel.logout() // Necesitarías implementar logout en ViewModel
                navController.navigate("login_screen") { // Volver a login (simplificado)
                    popUpTo(0) // Limpia toda la pila
                }
            }) {
                Text("Cerrar Sesión (Temporal)")
            }
        }
    }
}