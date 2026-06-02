package com.example.planify.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.planify.viewmodel.LoginViewModel

/**
 * Pantalla de inicio de sesión de Planify.
 * Valida credenciales predefinidas (admin / 1234) sin base de datos.
 */
@Composable
fun LoginScreen(
    viewModel: LoginViewModel,
    onLoginSuccess: () -> Unit
) {
    val isLoggedIn   by viewModel.isLoggedIn.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()

    // Navega automáticamente cuando el login es exitoso
    LaunchedEffect(isLoggedIn) {
        if (isLoggedIn) onLoginSuccess()
    }

    Box(
        modifier = androidx.compose.ui.Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center
    ) {
        LoginContent(
            errorMessage = errorMessage,
            onLogin      = { user, pass -> viewModel.login(user, pass) },
            onClearError = { viewModel.clearError() }
        )
    }
}

/**
 * Contenido del formulario de login separado para evitar el límite del compilador.
 */
@Composable
private fun LoginContent(
    errorMessage : String?,
    onLogin      : (String, String) -> Unit,
    onClearError : () -> Unit
) {
    val username        = rememberSaveable { mutableStateOf("") }
    val password        = rememberSaveable { mutableStateOf("") }
    val passwordVisible = rememberSaveable { mutableStateOf(false) }
    val focusManager    = LocalFocusManager.current

    Column(
        modifier = androidx.compose.ui.Modifier
            .fillMaxWidth()
            .padding(horizontal = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Spacer(modifier = androidx.compose.ui.Modifier.height(16.dp))

        // ── Logo ──────────────────────────────────────────────────────────
        Surface(
            shape    = RoundedCornerShape(24.dp),
            color    = MaterialTheme.colorScheme.primaryContainer,
            modifier = androidx.compose.ui.Modifier.size(88.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Text(text = "📋", fontSize = 40.sp)
            }
        }

        Spacer(modifier = androidx.compose.ui.Modifier.height(8.dp))

        Text(
            text       = "Planify",
            style      = MaterialTheme.typography.headlineLarge,
            color      = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.Bold
        )

        Text(
            text      = "Tu agenda personal",
            style     = MaterialTheme.typography.bodyMedium,
            color     = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = androidx.compose.ui.Modifier.height(8.dp))

        // ── Tarjeta de formulario ──────────────────────────────────────────
        Card(
            modifier  = androidx.compose.ui.Modifier.fillMaxWidth(),
            shape     = RoundedCornerShape(20.dp),
            colors    = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(
                modifier            = androidx.compose.ui.Modifier.padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text  = "Iniciar sesión",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )

                // ── Campo Usuario ──────────────────────────────────────────
                OutlinedTextField(
                    value         = username.value,
                    onValueChange = { username.value = it; onClearError() },
                    label         = { Text("Usuario") },
                    leadingIcon   = { Icon(Icons.Default.Person, contentDescription = null) },
                    singleLine    = true,
                    modifier      = androidx.compose.ui.Modifier.fillMaxWidth(),
                    shape         = RoundedCornerShape(12.dp),
                    isError       = errorMessage != null,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                    keyboardActions = KeyboardActions(
                        onNext = { focusManager.moveFocus(FocusDirection.Down) }
                    )
                )

                // ── Campo Contraseña ───────────────────────────────────────
                OutlinedTextField(
                    value         = password.value,
                    onValueChange = { password.value = it; onClearError() },
                    label         = { Text("Contraseña") },
                    leadingIcon   = { Icon(Icons.Default.Lock, contentDescription = null) },
                    trailingIcon  = {
                        IconButton(onClick = { passwordVisible.value = !passwordVisible.value }) {
                            Icon(
                                imageVector = if (passwordVisible.value)
                                    Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                contentDescription = null
                            )
                        }
                    },
                    visualTransformation = if (passwordVisible.value)
                        VisualTransformation.None else PasswordVisualTransformation(),
                    singleLine    = true,
                    modifier      = androidx.compose.ui.Modifier.fillMaxWidth(),
                    shape         = RoundedCornerShape(12.dp),
                    isError       = errorMessage != null,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Password,
                        imeAction    = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = {
                            focusManager.clearFocus()
                            onLogin(username.value, password.value)
                        }
                    )
                )

                // ── Mensaje de error ───────────────────────────────────────
                if (errorMessage != null) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = MaterialTheme.colorScheme.errorContainer
                    ) {
                        Text(
                            text     = errorMessage,
                            color    = MaterialTheme.colorScheme.onErrorContainer,
                            style    = MaterialTheme.typography.bodySmall,
                            modifier = androidx.compose.ui.Modifier.padding(
                                horizontal = 12.dp, vertical = 8.dp
                            )
                        )
                    }
                }

                // ── Botón de Login ─────────────────────────────────────────
                Button(
                    onClick  = {
                        focusManager.clearFocus()
                        onLogin(username.value, password.value)
                    },
                    modifier = androidx.compose.ui.Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape    = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text       = "Ingresar",
                        style      = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }

        // ── Hint de credenciales (demo) ────────────────────────────────────
        Text(
            text      = "Demo: admin / 1234",
            style     = MaterialTheme.typography.bodySmall,
            color     = MaterialTheme.colorScheme.outline,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = androidx.compose.ui.Modifier.height(16.dp))
    }
}
