package com.example.planify.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * ViewModel para la pantalla de Login.
 * Valida credenciales predefinidas sin base de datos externa.
 */
class LoginViewModel : ViewModel() {

    // Credenciales predefinidas
    private val validUsername = "admin"
    private val validPassword = "1234"

    // ── Estado de autenticación ────────────────────────────────────────────
    private val _isLoggedIn = MutableStateFlow(false)
    val isLoggedIn: StateFlow<Boolean> = _isLoggedIn.asStateFlow()

    // ── Mensaje de error (null = sin error) ───────────────────────────────
    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    // ── Estado de carga ────────────────────────────────────────────────────
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    /**
     * Valida las credenciales ingresadas por el usuario.
     */
    fun login(username: String, password: String) {
        _errorMessage.value = null

        when {
            username.isBlank() && password.isBlank() ->
                _errorMessage.value = "Por favor ingresa tu usuario y contraseña."
            username.isBlank() ->
                _errorMessage.value = "El campo de usuario no puede estar vacío."
            password.isBlank() ->
                _errorMessage.value = "El campo de contraseña no puede estar vacío."
            username != validUsername ->
                _errorMessage.value = "Usuario incorrecto. Intenta de nuevo."
            password != validPassword ->
                _errorMessage.value = "Contraseña incorrecta. Intenta de nuevo."
            else -> {
                _isLoggedIn.value = true
                _errorMessage.value = null
            }
        }
    }

    /**
     * Cierra la sesión del usuario y limpia el estado.
     */
    fun logout() {
        _isLoggedIn.value = false
        _errorMessage.value = null
    }

    /**
     * Limpia el mensaje de error (útil al modificar los campos).
     */
    fun clearError() {
        _errorMessage.value = null
    }
}
