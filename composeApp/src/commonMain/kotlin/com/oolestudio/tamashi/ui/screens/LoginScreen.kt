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
import com.oolestudio.tamashi.viewmodel.AuthViewModel

/**
 * Pantalla de Inicio de Sesión.
 */
@Composable
fun LoginScreen(viewModel: AuthViewModel, onNavigateToRegister: () -> Unit) {
    // Estados locales para los campos de texto.
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    // Observamos si hay errores de login desde el ViewModel.
    val loginError by viewModel.loginError.collectAsState()

    // Inicializamos el manejador de autenticación con Google.
    // La lambda se ejecutará cuando Google devuelva el token exitosamente.
    val googleSignInLauncher = rememberGoogleAuthHandler { token ->
        viewModel.signInWithGoogle(token)
    }

    // Efecto secundario: Limpia los errores de login cuando el usuario empieza a escribir de nuevo.
    LaunchedEffect(email, password) {
        viewModel.clearLoginError()
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Iniciar Sesión", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(16.dp))

        // Componentes personalizados para entrada de texto.
        EmailTextField(
            value = email,
            onValueChange = { email = it },
            modifier = Modifier.fillMaxWidth(),
            isError = loginError != null
        )
        Spacer(modifier = Modifier.height(8.dp))

        PasswordTextField(
            value = password,
            onValueChange = { password = it },
            modifier = Modifier.fillMaxWidth(),
            isError = loginError != null
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = { viewModel.login(email, password) },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Login")
        }

        // Muestra el mensaje de error si existe.
        loginError?.let {
            Spacer(modifier = Modifier.height(8.dp))
            Text(it, color = MaterialTheme.colorScheme.error)
        }

        Spacer(modifier = Modifier.height(8.dp))
        TextButton(onClick = onNavigateToRegister) {
            Text("¿No tienes cuenta? Regístrate")
        }

        Divider(modifier = Modifier.padding(vertical = 16.dp))

        // Botón para iniciar sesión con Google.
        GoogleSignInButton(
            onClick = { googleSignInLauncher() }
        )
    }
}

@Composable
fun GoogleSignInButton(onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Text("G", style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.width(12.dp))
        Text("Iniciar Sesión con Google")
    }
}