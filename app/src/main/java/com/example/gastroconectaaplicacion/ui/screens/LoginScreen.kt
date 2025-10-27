package com.example.gastroconectaaplicacion.ui.screens // Asegúrate que el package sea correcto

import android.app.Application
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.gastroconectaaplicacion.ui.navigation.AppScreens // Importa tus rutas
import com.example.gastroconectaaplicacion.ui.viewmodel.AuthViewModel // Importa el ViewModel
import com.example.gastroconectaaplicacion.ui.viewmodel.AuthUiState // Importa el estado
import com.example.gastroconectaaplicacion.ui.viewmodel.ViewModelFactory // Importa la Factory

@Composable
fun LoginScreen(navController: NavController) {
    // 1. Obtener la instancia del ViewModel usando nuestra Factory
    val context = LocalContext.current
    val application = context.applicationContext as Application
    val viewModel: AuthViewModel = viewModel(factory = ViewModelFactory(application))

    // 2. Observar el estado de la UI del ViewModel
    val uiState by viewModel.uiState.collectAsState()

    // 3. Estados locales para los campos del formulario
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    // --- Diseño de la Pantalla ---
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Iniciar Sesión", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(32.dp))

        // --- Campo Email ---
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))

        // --- Campo Contraseña ---
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Contraseña") },
            visualTransformation = PasswordVisualTransformation(), // Oculta la contraseña
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(32.dp))

        // --- Botón de Login ---
        Button(
            onClick = { viewModel.login(email, password) },
            enabled = uiState != AuthUiState.Loading, // Deshabilita si está cargando
            modifier = Modifier.fillMaxWidth()
        ) {
            // Muestra texto o indicador de carga según el estado
            if (uiState == AuthUiState.Loading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = MaterialTheme.colorScheme.onPrimary
                )
            } else {
                Text("Ingresar")
            }
        }
        Spacer(modifier = Modifier.height(16.dp))

        // --- Botón para ir a Registro ---
        TextButton(onClick = { navController.navigate(AppScreens.RegisterScreen.route) }) {
            Text("¿No tienes cuenta? Regístrate aquí")
        }

        // --- Manejo de Estado (Errores/Éxito) ---
        when (val state = uiState) {
            is AuthUiState.Error -> {
                // Muestra un diálogo de error
                AlertDialog(
                    onDismissRequest = { viewModel.resetState() }, // Resetea el estado al cerrar
                    title = { Text("Error") },
                    text = { Text(state.message) },
                    confirmButton = {
                        Button(onClick = { viewModel.resetState() }) {
                            Text("OK")
                        }
                    }
                )
            }
            AuthUiState.Success -> {
                // Si el login es exitoso, navega a la pantalla Home
                LaunchedEffect(Unit) { // <-- DESCOMENTADO
                    navController.navigate(AppScreens.HomeScreen.route) {
                        popUpTo(AppScreens.LoginScreen.route) { inclusive = true }
                    }
                    viewModel.resetState()
                } // <-- DESCOMENTADO
            }
            else -> {} // No hacer nada en Idle o Loading
        }
    }
}