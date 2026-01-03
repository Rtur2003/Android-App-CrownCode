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
class SignUpViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow<AuthUiState>(AuthUiState.Idle)
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    private val _name = MutableStateFlow("")
    val name: StateFlow<String> = _name.asStateFlow()

    private val _email = MutableStateFlow("")
    val email: StateFlow<String> = _email.asStateFlow()

    private val _password = MutableStateFlow("")
    val password: StateFlow<String> = _password.asStateFlow()

    private val _termsAccepted = MutableStateFlow(false)
    val termsAccepted: StateFlow<Boolean> = _termsAccepted.asStateFlow()

    private val _isGoogleLoading = MutableStateFlow(false)
    val isGoogleLoading: StateFlow<Boolean> = _isGoogleLoading.asStateFlow()

    private val _isAppleLoading = MutableStateFlow(false)
    val isAppleLoading: StateFlow<Boolean> = _isAppleLoading.asStateFlow()

    fun onNameChange(value: String) {
        _name.value = value
    }

    fun onEmailChange(value: String) {
        _email.value = value
    }

    fun onPasswordChange(value: String) {
        _password.value = value
    }

    fun onTermsAcceptedChange(value: Boolean) {
        _termsAccepted.value = value
    }

    fun signUpWithEmail() {
        if (_name.value.isBlank()) {
            _uiState.value = AuthUiState.Error("Lütfen adınızı girin")
            return
        }
        if (_email.value.isBlank()) {
            _uiState.value = AuthUiState.Error("Lütfen email adresinizi girin")
            return
        }
        if (_password.value.length < 6) {
            _uiState.value = AuthUiState.Error("Şifre en az 6 karakter olmalıdır")
            return
        }
        if (!_termsAccepted.value) {
            _uiState.value = AuthUiState.Error("Kullanım koşullarını kabul etmelisiniz")
            return
        }

        viewModelScope.launch {
            _uiState.value = AuthUiState.Loading
            delay(1500) // Simulate network call
            _uiState.value = AuthUiState.Success
        }
    }

    fun signUpWithGoogle() {
        viewModelScope.launch {
            _isGoogleLoading.value = true
            delay(1500) // Simulate Google auth
            _isGoogleLoading.value = false
            _uiState.value = AuthUiState.Success
        }
    }

    fun signUpWithApple() {
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
