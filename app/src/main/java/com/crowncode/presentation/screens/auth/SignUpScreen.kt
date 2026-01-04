package com.crowncode.presentation.screens.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.crowncode.presentation.components.GradientButton
import com.crowncode.presentation.components.PasswordTextField
import com.crowncode.presentation.components.SocialLoginButton
import com.crowncode.presentation.components.SocialProvider
import com.crowncode.presentation.theme.Background
import com.crowncode.presentation.theme.Border
import com.crowncode.presentation.theme.Primary
import com.crowncode.presentation.theme.SurfaceAlt
import com.crowncode.presentation.theme.TextMuted
import com.crowncode.presentation.theme.TextPrimary
import com.crowncode.presentation.theme.TextSecondary
import com.crowncode.presentation.theme.Warning

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignUpScreen(
    onNavigateBack: () -> Unit,
    onSignUpSuccess: () -> Unit,
    onNavigateToLogin: () -> Unit,
    viewModel: SignUpViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val name by viewModel.name.collectAsStateWithLifecycle()
    val email by viewModel.email.collectAsStateWithLifecycle()
    val password by viewModel.password.collectAsStateWithLifecycle()
    val termsAccepted by viewModel.termsAccepted.collectAsStateWithLifecycle()
    val isGoogleLoading by viewModel.isGoogleLoading.collectAsStateWithLifecycle()
    val isAppleLoading by viewModel.isAppleLoading.collectAsStateWithLifecycle()
    val keyboardController = LocalSoftwareKeyboardController.current

    LaunchedEffect(uiState) {
        if (uiState is AuthUiState.Success) {
            onSignUpSuccess()
        }
    }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .background(Background),
        containerColor = Background,
        topBar = {
            TopAppBar(
                title = { },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Geri",
                            tint = TextPrimary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent
                )
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .imePadding(),
            contentAlignment = Alignment.TopCenter
        ) {
            Column(
                modifier = Modifier
                    .widthIn(max = 400.dp)
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 24.dp)
                    .navigationBarsPadding(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(8.dp))

                // Header
                Text(
                    text = "Hesap Oluştur",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "CrownCode'a bugün katılın",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(28.dp))

                // Social Buttons
                SocialLoginButton(
                    provider = SocialProvider.GOOGLE,
                    text = "Google ile kayıt ol",
                    onClick = { viewModel.signUpWithGoogle() },
                    isLoading = isGoogleLoading
                )

                Spacer(modifier = Modifier.height(12.dp))

                SocialLoginButton(
                    provider = SocialProvider.APPLE,
                    text = "Apple ile kayıt ol",
                    onClick = { viewModel.signUpWithApple() },
                    isLoading = isAppleLoading
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Divider
                DividerWithText(text = "veya")

                Spacer(modifier = Modifier.height(24.dp))

                // Name Field
                OutlinedTextField(
                    value = name,
                    onValueChange = {
                        viewModel.onNameChange(it)
                        viewModel.clearError()
                    },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Ad Soyad") },
                    placeholder = { Text("Adınızı girin", color = TextMuted) },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = null,
                            tint = TextMuted,
                            modifier = Modifier.size(20.dp)
                        )
                    },
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Primary,
                        unfocusedBorderColor = Border,
                        cursorColor = Primary,
                        focusedContainerColor = SurfaceAlt,
                        unfocusedContainerColor = SurfaceAlt
                    ),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Text,
                        imeAction = ImeAction.Next
                    ),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Email Field
                OutlinedTextField(
                    value = email,
                    onValueChange = {
                        viewModel.onEmailChange(it)
                        viewModel.clearError()
                    },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Email") },
                    placeholder = { Text("ornek@email.com", color = TextMuted) },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Email,
                            contentDescription = null,
                            tint = TextMuted,
                            modifier = Modifier.size(20.dp)
                        )
                    },
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Primary,
                        unfocusedBorderColor = Border,
                        cursorColor = Primary,
                        focusedContainerColor = SurfaceAlt,
                        unfocusedContainerColor = SurfaceAlt
                    ),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Email,
                        imeAction = ImeAction.Next
                    ),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Password Field
                PasswordTextField(
                    value = password,
                    onValueChange = {
                        viewModel.onPasswordChange(it)
                        viewModel.clearError()
                    },
                    modifier = Modifier.fillMaxWidth(),
                    label = "Şifre",
                    placeholder = "En az 6 karakter",
                    imeAction = ImeAction.Done,
                    onImeAction = { keyboardController?.hide() }
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Terms Checkbox
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null,
                            onClick = { viewModel.onTermsAcceptedChange(!termsAccepted) }
                        ),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = termsAccepted,
                        onCheckedChange = { viewModel.onTermsAcceptedChange(it) },
                        colors = CheckboxDefaults.colors(
                            checkedColor = Primary,
                            uncheckedColor = TextMuted,
                            checkmarkColor = TextPrimary
                        )
                    )
                    Text(
                        text = "Kullanım Koşulları ve Gizlilik Politikasını kabul ediyorum",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary,
                        modifier = Modifier.weight(1f)
                    )
                }

                // Error Message
                if (uiState is AuthUiState.Error) {
                    Text(
                        text = (uiState as AuthUiState.Error).message,
                        style = MaterialTheme.typography.bodySmall,
                        color = Warning,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp)
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Sign Up Button
                GradientButton(
                    text = "Hesap Oluştur",
                    onClick = {
                        keyboardController?.hide()
                        viewModel.signUpWithEmail()
                    },
                    modifier = Modifier.fillMaxWidth(),
                    isLoading = uiState is AuthUiState.Loading
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Login Link
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Zaten hesabınız var mı? ",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSecondary
                    )
                    Text(
                        text = "Giriş Yap",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = Primary,
                        modifier = Modifier.clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null,
                            onClick = onNavigateToLogin
                        )
                    )
                }

                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}

@Composable
private fun DividerWithText(text: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .weight(1f)
                .height(1.dp)
                .background(Border)
        )
        Text(
            text = text,
            style = MaterialTheme.typography.bodySmall,
            color = TextMuted,
            modifier = Modifier.padding(horizontal = 16.dp)
        )
        Box(
            modifier = Modifier
                .weight(1f)
                .height(1.dp)
                .background(Border)
        )
    }
}
