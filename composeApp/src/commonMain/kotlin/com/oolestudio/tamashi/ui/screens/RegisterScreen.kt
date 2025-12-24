package com.oolestudio.tamashi.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.oolestudio.tamashi.data.rememberGoogleAuthHandler
import com.oolestudio.tamashi.ui.components.EmailTextField
import com.oolestudio.tamashi.ui.components.PasswordTextField
import com.oolestudio.tamashi.ui.components.UsernameTextField
import com.oolestudio.tamashi.viewmodel.AuthViewModel

/**
 * Pantalla de Registro de nuevos usuarios.
 */
@Composable
fun RegisterScreen(viewModel: AuthViewModel, onNavigateToLogin: () -> Unit) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var username by remember { mutableStateOf("") }

    val registrationError by viewModel.registrationError.collectAsState()

    val googleSignInLauncher = rememberGoogleAuthHandler { token ->
        viewModel.signInWithGoogle(token)
    }

    // Limpia errores previos cuando el usuario modifica algún campo.
    LaunchedEffect(email, password, username) {
        viewModel.clearRegistrationError()
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Registro", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(16.dp))

        UsernameTextField(
            value = username,
            onValueChange = { username = it },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))

        EmailTextField(
            value = email,
            onValueChange = { email = it },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))

        PasswordTextField(
            value = password,
            onValueChange = { password = it },
            supportingText = { Text("Mín. 6 caracteres, 1 mayúscula, 1 minúscula, 1 número.") },
            isError = registrationError != null,
            modifier = Modifier.fillMaxWidth()
        )
        
        registrationError?.let {
            Text(it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = { viewModel.register(email, password, username) },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Registrarse")
        }
        Spacer(modifier = Modifier.height(8.dp))
        TextButton(onClick = onNavigateToLogin) {
            Text("¿Ya tienes cuenta? Inicia Sesión")
        }

        Divider(modifier = Modifier.padding(vertical = 16.dp))

        GoogleSignInButton(
            onClick = { googleSignInLauncher() }
        )
    }
}