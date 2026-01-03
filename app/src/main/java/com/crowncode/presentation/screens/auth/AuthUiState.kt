package com.crowncode.presentation.screens.auth

sealed interface AuthUiState {
    data object Idle : AuthUiState
    data object Loading : AuthUiState
    data class Error(val message: String) : AuthUiState
    data object Success : AuthUiState
}

data class FormValidationState(
    val nameError: String? = null,
    val emailError: String? = null,
    val passwordError: String? = null,
    val isValid: Boolean = false
)
