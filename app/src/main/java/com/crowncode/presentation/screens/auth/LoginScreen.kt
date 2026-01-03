package com.crowncode.presentation.screens.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.crowncode.presentation.components.BackgroundWithGlow
import com.crowncode.presentation.components.CrownCodeLogo
import com.crowncode.presentation.components.GradientButton
import com.crowncode.presentation.components.LogoSize
import com.crowncode.presentation.components.PasswordTextField
import com.crowncode.presentation.components.SocialLoginButton
import com.crowncode.presentation.components.SocialProvider
import com.crowncode.presentation.theme.Border
import com.crowncode.presentation.theme.Primary
import com.crowncode.presentation.theme.SurfaceAlt
import com.crowncode.presentation.theme.TextMuted
import com.crowncode.presentation.theme.TextPrimary
import com.crowncode.presentation.theme.TextSecondary
import com.crowncode.presentation.theme.Warning

@Composable
fun LoginScreen(
    onNavigateBack: () -> Unit,
    onLoginSuccess: () -> Unit,
    onNavigateToSignUp: () -> Unit,
    viewModel: LoginViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val email by viewModel.email.collectAsStateWithLifecycle()
    val password by viewModel.password.collectAsStateWithLifecycle()
    val isGoogleLoading by viewModel.isGoogleLoading.collectAsStateWithLifecycle()
    val isAppleLoading by viewModel.isAppleLoading.collectAsStateWithLifecycle()
    val keyboardController = LocalSoftwareKeyboardController.current

    LaunchedEffect(uiState) {
        if (uiState is AuthUiState.Success) {
            onLoginSuccess()
        }
    }

    BackgroundWithGlow {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(WindowInsets.statusBars.asPaddingValues())
        ) {
            // Top Bar
            IconButton(
                onClick = onNavigateBack,
                modifier = Modifier.padding(8.dp)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Geri",
                    tint = TextPrimary
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
                    .padding(bottom = 32.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(16.dp))

                // Logo
                CrownCodeLogo(
                    size = LogoSize.Medium,
                    animated = false
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Title
                Text(
                    text = "Hoş Geldiniz",
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Devam etmek için giriş yapın",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary
                )

                Spacer(modifier = Modifier.height(32.dp))

                // Social Login Buttons
                SocialLoginButton(
                    provider = SocialProvider.GOOGLE,
                    text = "Google ile devam et",
                    onClick = { viewModel.loginWithGoogle() },
                    isLoading = isGoogleLoading
                )

                Spacer(modifier = Modifier.height(12.dp))

                SocialLoginButton(
                    provider = SocialProvider.APPLE,
                    text = "Apple ile devam et",
                    onClick = { viewModel.loginWithApple() },
                    isLoading = isAppleLoading
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Divider
                DividerWithText(text = "veya")

                Spacer(modifier = Modifier.height(24.dp))

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
                    shape = RoundedCornerShape(14.dp),
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
                    imeAction = ImeAction.Done,
                    onImeAction = {
                        keyboardController?.hide()
                        viewModel.loginWithEmail()
                    }
                )

                // Forgot Password
                TextButton(
                    onClick = { /* TODO: Forgot password */ },
                    modifier = Modifier.align(Alignment.End)
                ) {
                    Text(
                        text = "Şifremi Unuttum",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Primary
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Error Message
                if (uiState is AuthUiState.Error) {
                    Text(
                        text = (uiState as AuthUiState.Error).message,
                        style = MaterialTheme.typography.bodySmall,
                        color = Warning,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                }

                // Login Button
                GradientButton(
                    text = "Giriş Yap",
                    onClick = {
                        keyboardController?.hide()
                        viewModel.loginWithEmail()
                    },
                    modifier = Modifier.fillMaxWidth(),
                    isLoading = uiState is AuthUiState.Loading
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Sign Up Link
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Hesabınız yok mu? ",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSecondary
                    )
                    Text(
                        text = "Kayıt Ol",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = Primary,
                        modifier = Modifier.clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null,
                            onClick = onNavigateToSignUp
                        )
                    )
                }

                Spacer(modifier = Modifier.height(32.dp))

                // Demo Info Card
                DemoInfoCard()
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
                .background(
                    brush = Brush.horizontalGradient(
                        colors = listOf(Color.Transparent, Border)
                    )
                )
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
                .background(
                    brush = Brush.horizontalGradient(
                        colors = listOf(Border, Color.Transparent)
                    )
                )
        )
    }
}

@Composable
private fun DemoInfoCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Warning.copy(alpha = 0.08f))
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Warning,
                contentDescription = null,
                tint = Warning,
                modifier = Modifier.size(20.dp)
            )
            Column {
                Text(
                    text = "Demo Modu",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.SemiBold,
                    color = Warning
                )
                Text(
                    text = "Giriş simüle edilmektedir. Herhangi bir email/şifre ile giriş yapabilirsiniz.",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary,
                    lineHeight = 18.sp
                )
            }
        }
    }
}
