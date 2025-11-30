package com.example.gastroconectaaplicacion.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gastroconectaaplicacion.data.model.User
import com.example.gastroconectaaplicacion.data.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AuthViewModel(private val userRepository: UserRepository) : ViewModel() {

    // Estado de la UI (Carga, Éxito, Error)
    private val _uiState = MutableStateFlow<AuthUiState>(AuthUiState.Idle)
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    // Usuario actual (Sesión)
    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser.asStateFlow()

    fun login(email: String, pass: String) {
        viewModelScope.launch {
            _uiState.value = AuthUiState.Loading
            val user = userRepository.loginUser(email, pass)
            if (user != null) {
                _currentUser.value = user
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
                // Si registra bien, hacemos login automático
                _currentUser.value = result
                _uiState.value = AuthUiState.Success
            } else {
                _uiState.value = AuthUiState.Error("Error al registrar. El email podría estar usado.")
            }
        }
    }

    fun logout() {
        _currentUser.value = null
        _uiState.value = AuthUiState.Idle
    }

    fun toggleSave(recipeId: Long) {
        val userId = _currentUser.value?.id ?: return
        viewModelScope.launch {
            val updatedUser = userRepository.toggleSaveRecipe(userId, recipeId)
            if (updatedUser != null) {
                _currentUser.value = updatedUser
            }
        }
    }

    fun toggleFollow(targetId: Long) {
        val userId = _currentUser.value?.id ?: return
        viewModelScope.launch {
            val updatedUser = userRepository.toggleFollowUser(userId, targetId)
            if (updatedUser != null) {
                _currentUser.value = updatedUser
            }
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