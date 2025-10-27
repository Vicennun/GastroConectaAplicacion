package com.example.gastroconectaaplicacion.ui.screens // Verifica este paquete

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
import com.example.gastroconectaaplicacion.ui.navigation.AppScreens // Verifica este import
import com.example.gastroconectaaplicacion.ui.viewmodel.AuthViewModel // Verifica este import
import com.example.gastroconectaaplicacion.ui.viewmodel.AuthUiState // Verifica este import
import com.example.gastroconectaaplicacion.ui.viewmodel.ViewModelFactory // Verifica este import

@Composable
fun RegisterScreen(navController: NavController) {
    // Obtener ViewModel
    val context = LocalContext.current
    val application = context.applicationContext as Application
    val viewModel: AuthViewModel = viewModel(factory = ViewModelFactory(application))

    // Observar estado
    val uiState by viewModel.uiState.collectAsState()

    // Estados locales para el formulario
    var nombre by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var passwordsMatch by remember { mutableStateOf(true) }

    // Función para validar contraseñas al cambiar
    LaunchedEffect(password, confirmPassword) {
        passwordsMatch = password == confirmPassword
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Crear Cuenta", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(32.dp))

        // Campo Nombre
        OutlinedTextField(
            value = nombre,
            onValueChange = { nombre = it },
            label = { Text("Nombre Completo") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))

        // Campo Email
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))

        // Campo Contraseña
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Contraseña") },
            visualTransformation = PasswordVisualTransformation(),
            isError = !passwordsMatch && confirmPassword.isNotEmpty(), // Muestra error si no coinciden
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))

        // Campo Confirmar Contraseña
        OutlinedTextField(
            value = confirmPassword,
            onValueChange = { confirmPassword = it },
            label = { Text("Confirmar Contraseña") },
            visualTransformation = PasswordVisualTransformation(),
            isError = !passwordsMatch && confirmPassword.isNotEmpty(),
            modifier = Modifier.fillMaxWidth()
        )
        if (!passwordsMatch && confirmPassword.isNotEmpty()) {
            Text("Las contraseñas no coinciden", color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
        }
        Spacer(modifier = Modifier.height(32.dp))

        // Botón Registrarse
        Button(
            onClick = { viewModel.register(nombre, email, password) },
            enabled = uiState != AuthUiState.Loading && passwordsMatch && password.isNotEmpty(), // Valida antes de enviar
            modifier = Modifier.fillMaxWidth()
        ) {
            if (uiState == AuthUiState.Loading) {
                CircularProgressIndicator(modifier = Modifier.size(24.dp), color = MaterialTheme.colorScheme.onPrimary)
            } else {
                Text("Registrarse")
            }
        }
        Spacer(modifier = Modifier.height(16.dp))

        // Botón para ir a Login
        TextButton(onClick = { navController.navigate(AppScreens.LoginScreen.route) }) {
            Text("¿Ya tienes cuenta? Inicia sesión aquí")
        }

        // Manejo de Estado (Errores/Éxito)
        when (val state = uiState) {
            is AuthUiState.Error -> {
                AlertDialog(
                    onDismissRequest = { viewModel.resetState() },
                    title = { Text("Error de Registro") },
                    text = { Text(state.message) },
                    confirmButton = {
                        Button(onClick = { viewModel.resetState() }) { Text("OK") }
                    }
                )
            }
            AuthUiState.Success -> {
                // Éxito implica auto-login, navegamos a Home
                LaunchedEffect(Unit) { // <-- DESCOMENTADO
                    navController.navigate(AppScreens.HomeScreen.route) { // Agrega navegación correcta a Home
                        popUpTo(AppScreens.LoginScreen.route) { inclusive = true }
                    }
                    viewModel.resetState()
                } // <-- DESCOMENTADO
            }
            else -> {}
        }
    }
}