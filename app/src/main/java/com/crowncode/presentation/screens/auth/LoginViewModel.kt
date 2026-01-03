package com.crowncode.presentation.screens.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow<AuthUiState>(AuthUiState.Idle)
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    private val _email = MutableStateFlow("")
    val email: StateFlow<String> = _email.asStateFlow()

    private val _password = MutableStateFlow("")
    val password: StateFlow<String> = _password.asStateFlow()

    private val _isGoogleLoading = MutableStateFlow(false)
    val isGoogleLoading: StateFlow<Boolean> = _isGoogleLoading.asStateFlow()

    private val _isAppleLoading = MutableStateFlow(false)
    val isAppleLoading: StateFlow<Boolean> = _isAppleLoading.asStateFlow()

    fun onEmailChange(value: String) {
        _email.value = value
    }

    fun onPasswordChange(value: String) {
        _password.value = value
    }

    fun loginWithEmail() {
        if (_email.value.isBlank()) {
            _uiState.value = AuthUiState.Error("Lütfen email adresinizi girin")
            return
        }
        if (_password.value.isBlank()) {
            _uiState.value = AuthUiState.Error("Lütfen şifrenizi girin")
            return
        }

        viewModelScope.launch {
            _uiState.value = AuthUiState.Loading
            delay(1500) // Simulate network call
            _uiState.value = AuthUiState.Success
        }
    }

    fun loginWithGoogle() {
        viewModelScope.launch {
            _isGoogleLoading.value = true
            delay(1500) // Simulate Google auth
            _isGoogleLoading.value = false
            _uiState.value = AuthUiState.Success
        }
    }

    fun loginWithApple() {
        viewModelScope.launch {
            _isAppleLoading.value = true
            delay(1500) // Simulate Apple auth
            _isAppleLoading.value = false
            _uiState.value = AuthUiState.Success
        }
    }

    fun resetState() {
        _uiState.value = AuthUiState.Idle
    }

    fun clearError() {
        if (_uiState.value is AuthUiState.Error) {
            _uiState.value = AuthUiState.Idle
        }
    }
}
