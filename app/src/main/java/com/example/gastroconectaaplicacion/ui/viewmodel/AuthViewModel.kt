package com.example.gastroconectaaplicacion.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gastroconectaaplicacion.data.model.User
import com.example.gastroconectaaplicacion.data.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

// 1. Pide el Repositorio en el constructor
class AuthViewModel(private val userRepository: UserRepository) : ViewModel() {

    // 2. Estados que la UI (Compose) observará (Cumple Req 3: responder a cambios de estado)

    // Estado de la UI (Cargando, Error, Éxito, Inactivo)
    // Es privado para que solo el ViewModel pueda modificarlo
    private val _uiState = MutableStateFlow<AuthUiState>(AuthUiState.Idle)
    // Es público para que la UI pueda leerlo
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    // Estado del usuario actual (null si no está logueado)
    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser.asStateFlow()

    // 3. Funciones de Lógica (Login/Registro)
    fun login(email: String, pass: String) {
        // Inicia una corutina en el hilo del ViewModel
        viewModelScope.launch {
            _uiState.value = AuthUiState.Loading // Estado: Cargando
            try {
                // Llama al repositorio (que llama al DAO)
                val user = userRepository.loginUser(email, pass)
                if (user != null) {
                    _currentUser.value = user
                    _uiState.value = AuthUiState.Success
                } else {
                    _uiState.value = AuthUiState.Error("Email o contraseña incorrectos")
                }
            } catch (e: Exception) {
                _uiState.value = AuthUiState.Error(e.message ?: "Error desconocido")
            }
        }
    }

    fun register(nombre: String, email: String, pass: String) {
        viewModelScope.launch {
            _uiState.value = AuthUiState.Loading
            try {
                // (Validación simple)
                if (nombre.isBlank() || email.isBlank() || pass.isBlank()) {
                    _uiState.value = AuthUiState.Error("Todos los campos son obligatorios")
                    return@launch
                }

                val newUser = User(nombre = nombre, email = email, password_hash = pass)
                userRepository.registerUser(newUser)

                // Hacemos auto-login después de registrar
                login(email, pass)
            } catch (e: Exception) {
                // Esto fallará si el email ya existe (gracias al OnConflict.ABORT del DAO)
                _uiState.value = AuthUiState.Error("Este email ya está en uso")
            }
        }
    }

    // Función para resetear el estado (ej. al salir de la pantalla de error)
    fun resetState() {
        _uiState.value = AuthUiState.Idle
    }
}

// 4. Clase sellada (sealed class) para definir los estados de la UI
// Esto es mucho más limpio que manejar un "isLoading", "isError", etc.
sealed class AuthUiState {
    object Idle : AuthUiState() // Inactivo
    object Loading : AuthUiState() // Cargando
    object Success : AuthUiState() // Éxito
    data class Error(val message: String) : AuthUiState() // Error con mensaje
}