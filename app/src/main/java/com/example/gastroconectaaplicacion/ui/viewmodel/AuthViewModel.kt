package com.example.gastroconectaaplicacion.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gastroconectaaplicacion.data.local.SessionManager
import com.example.gastroconectaaplicacion.data.model.User
import com.example.gastroconectaaplicacion.data.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AuthViewModel(
    private val userRepository: UserRepository,
    private val sessionManager: SessionManager
) : ViewModel() {

    // Estado de la UI
    private val _uiState = MutableStateFlow<AuthUiState>(AuthUiState.Idle)
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    // Usuario actual
    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser.asStateFlow()

    // 1. Al iniciar el ViewModel, verificamos sesión guardada
    init {
        checkSession()
    }

    private fun checkSession() {
        val savedUser = sessionManager.getUser()
        if (savedUser != null) {
            _currentUser.value = savedUser
            _uiState.value = AuthUiState.Success
        }
    }

    // 2. Login con guardado automático
    fun login(email: String, pass: String) {
        viewModelScope.launch {
            _uiState.value = AuthUiState.Loading
            val user = userRepository.loginUser(email, pass)
            if (user != null) {
                _currentUser.value = user
                // Guardamos la sesión automáticamente al loguearse con éxito
                sessionManager.saveUser(user)
                _uiState.value = AuthUiState.Success
            } else {
                _uiState.value = AuthUiState.Error("Credenciales incorrectas o error de conexión")
            }
        }
    }

    fun register(nombre: String, email: String, pass: String) {
        viewModelScope.launch {
            _uiState.value = AuthUiState.Loading
            val newUser = User(name = nombre, email = email, password = pass)
            val result = userRepository.registerUser(newUser)
            if (result != null) {
                _currentUser.value = result
                // Opcional: Guardar sesión también al registrarse
                sessionManager.saveUser(result)
                _uiState.value = AuthUiState.Success
            } else {
                _uiState.value = AuthUiState.Error("Error al registrar.")
            }
        }
    }

    fun logout() {
        sessionManager.clearSession() // Limpiamos persistencia
        _currentUser.value = null
        _uiState.value = AuthUiState.Idle
    }

    private val _publicProfileUser = MutableStateFlow<User?>(null)
    val publicProfileUser: StateFlow<User?> = _publicProfileUser.asStateFlow()

    fun loadPublicProfile(userId: Long) {
        viewModelScope.launch {
            val user = userRepository.getUserById(userId)
            _publicProfileUser.value = user
        }
    }

    fun toggleSave(recipeId: Long) {
        val userId = _currentUser.value?.id ?: return
        viewModelScope.launch {
            val updatedUser = userRepository.toggleSaveRecipe(userId, recipeId)
            if (updatedUser != null) _currentUser.value = updatedUser
        }
    }

    fun toggleFollow(targetId: Long) {
        val userId = _currentUser.value?.id ?: return
        viewModelScope  .launch {
            val updatedUser = userRepository.toggleFollowUser(userId, targetId)
            if (updatedUser != null) _currentUser.value = updatedUser
        }
    }

    fun resetState() {
        _uiState.value = AuthUiState.Idle
    }
}

// --- ¡ESTO ES LO QUE FALTABA! ---
// Esta clase debe estar fuera de AuthViewModel, pero en el mismo archivo
sealed class AuthUiState {
    object Idle : AuthUiState()
    object Loading : AuthUiState()
    object Success : AuthUiState()
    data class Error(val message: String) : AuthUiState()
}