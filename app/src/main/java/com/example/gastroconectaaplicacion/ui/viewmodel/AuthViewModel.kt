package com.example.gastroconectaaplicacion.ui.viewmodel // Verifica

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gastroconectaaplicacion.data.model.User
import com.example.gastroconectaaplicacion.data.repository.UserRepository
import kotlinx.coroutines.flow.* // Asegúrate de importar flow
import kotlinx.coroutines.launch

class AuthViewModel(private val userRepository: UserRepository) : ViewModel() {

    private val _uiState = MutableStateFlow<AuthUiState>(AuthUiState.Idle)
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    private val _currentUser = MutableStateFlow<User?>(null) // Mantenlo Mutable para poder actualizarlo
    val currentUser: StateFlow<User?> = _currentUser.asStateFlow()

    // --- NUEVO: Cargar usuario al inicio (simula sesión persistente) ---
    // init {
    //     viewModelScope.launch {
    //         // Aquí podrías leer un ID de usuario guardado en SharedPreferences
    //         // y luego cargarlo con userRepository.getUserById(savedId)
    //         // Por ahora, lo dejamos así, el usuario se carga al hacer login.
    //     }
    // }

    fun login(email: String, pass: String) {
        viewModelScope.launch {
            _uiState.value = AuthUiState.Loading
            try {
                val user = userRepository.loginUser(email, pass)
                if (user != null) {
                    _currentUser.value = user // Actualiza el usuario actual
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
                if (nombre.isBlank() || email.isBlank() || pass.isBlank()) {
                    _uiState.value = AuthUiState.Error("Todos los campos son obligatorios")
                    return@launch
                }
                // Simplificado: No hashear contraseña
                val newUser = User(nombre = nombre, email = email, password_hash = pass)
                userRepository.registerUser(newUser)
                login(email, pass) // Auto-login
            } catch (e: Exception) {
                _uiState.value = AuthUiState.Error("Este email ya está en uso")
            }
        }
    }

    // --- NUEVA FUNCIÓN: Logout ---
    fun logout() {
        _currentUser.value = null // Borra el usuario actual
        // Aquí podrías borrar también el ID guardado en SharedPreferences
        _uiState.value = AuthUiState.Idle // Vuelve al estado inicial
    }

    // --- NUEVAS FUNCIONES: Acciones del usuario ---
    fun toggleSave(recipeId: Long) {
        val userId = _currentUser.value?.id ?: return // Solo si hay usuario logueado
        viewModelScope.launch {
            try {
                userRepository.toggleSaveRecipe(userId, recipeId)
                // Actualiza el currentUser local para reflejar el cambio inmediatamente en la UI
                _currentUser.value = userRepository.getUserById(userId)
            } catch (e: Exception) { /* Manejar error si es necesario */ }
        }
    }

    fun toggleFollow(targetUserId: Long) {
        val userId = _currentUser.value?.id ?: return
        if (userId == targetUserId) return // No te sigas a ti mismo
        viewModelScope.launch {
            try {
                userRepository.toggleFollowUser(userId, targetUserId)
                // Actualiza el currentUser local
                _currentUser.value = userRepository.getUserById(userId)
            } catch (e: Exception) { /* Manejar error */ }
        }
    }

    fun resetState() {
        _uiState.value = AuthUiState.Idle
    }
}

sealed class AuthUiState {
    object Idle : AuthUiState()
    object Loading : AuthUiState()
    object Success : AuthUiState()
    data class Error(val message: String) : AuthUiState()
}