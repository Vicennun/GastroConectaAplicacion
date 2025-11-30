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
import com.example.gastroconectaaplicacion.ui.navigation.AppScreens // Verifica
import com.example.gastroconectaaplicacion.ui.viewmodel.AuthViewModel // Verifica
import com.example.gastroconectaaplicacion.ui.viewmodel.ViewModelFactory // Verifica

@Composable
fun ProfileScreen(
    navController: NavController,
    authViewModel: AuthViewModel // <--- CAMBIO
) {

    val currentUser by authViewModel.currentUser.collectAsState()

    // Comprobar si el usuario está logueado, si no, redirigir (mejorado)
    LaunchedEffect(currentUser) {
        if (currentUser == null) {
            navController.navigate(AppScreens.LoginScreen.route) {
                popUpTo(0) { inclusive = true } // Limpia toda la pila para que no pueda volver atrás
            }
        }
    }

    // Solo muestra el contenido si currentUser no es null
    currentUser?.let { user ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Mi Perfil", style = MaterialTheme.typography.headlineMedium)
            Spacer(modifier = Modifier.height(16.dp))
            Text("Nombre: ${user.name}")
            Text("Email: ${user.email}")
            Spacer(modifier = Modifier.height(32.dp))

            // TODO: Implementar    Tabs con Pager (Compose) para:
            // - Mis Recetas (filtrar recipes por user.id)
            // - Recetas Guardadas (filtrar recipes por user.recetario)
            // - Siguiendo (buscar usuarios por user.siguiendo)
            // - Seguidores (buscar usuarios por user.seguidores)
            Text("--- Pestañas Pendientes ---")
            Spacer(modifier = Modifier.height(32.dp))

            // Botón Logout Funcional
            Button(onClick = {
                authViewModel.logout()
                // Navegar a Login ya se maneja con LaunchedEffect
            }) {
                Text("Cerrar Sesión")
            }
        }
    } ?: run {
        // Muestra carga mientras LaunchedEffect redirige (o si hubo error)
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    }
}