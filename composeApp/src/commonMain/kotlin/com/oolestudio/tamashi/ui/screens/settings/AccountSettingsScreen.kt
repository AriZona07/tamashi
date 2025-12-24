package com.oolestudio.tamashi.ui.screens.settings

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.oolestudio.tamashi.data.UserState
import com.oolestudio.tamashi.ui.components.PasswordTextField
import com.oolestudio.tamashi.ui.components.UsernameTextField
import com.oolestudio.tamashi.viewmodel.AuthViewModel
import kotlinx.coroutines.delay

/**
 * Pantalla de Configuración de la Cuenta.
 * Permite al usuario cambiar su nombre de usuario, contraseña y eliminar su cuenta.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccountSettingsScreen(viewModel: AuthViewModel, onBack: () -> Unit, modifier: Modifier = Modifier) {
    // Estados locales para los campos de entrada.
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    
    // Estados para controlar la visibilidad de los diálogos.
    var showDeleteDialog by remember { mutableStateOf(false) }
    // showReauthDialog almacena la acción a ejecutar después de re-autenticarse exitosamente.
    var showReauthDialog by remember { mutableStateOf<(() -> Unit)?>(null) }

    // Observamos el estado de autenticación y mensajes del ViewModel.
    val authState by viewModel.authState.collectAsState()
    val updateError by viewModel.updateError.collectAsState()
    val successMessage by viewModel.updateSuccessMessage.collectAsState()
    
    // Verificamos si el usuario inició sesión con contraseña (para mostrar/ocultar opciones de cambio de pass).
    val hasPasswordProvider = "password" in (authState as? UserState.LoggedIn)?.providerIds.orEmpty()

    // Limpiamos errores al escribir.
    LaunchedEffect(password) {
        viewModel.clearUpdateError()
    }

    // --- MANEJO DE DIÁLOGOS ---
    
    // Diálogo de éxito (ej. "Contraseña actualizada").
    successMessage?.let { message ->
        SuccessDialog(
            message = message,
            onDismiss = { viewModel.clearUpdateSuccessMessage() }
        )
    }

    // Diálogo de error.
    updateError?.let { error ->
        ErrorDialog(
            error = error,
            onDismiss = { viewModel.clearUpdateError() }
        )
    }

    // Diálogo de confirmación de eliminación de cuenta.
    if (showDeleteDialog) {
        DeleteConfirmationDialog(
            onConfirm = { viewModel.deleteUser() },
            onDismiss = { showDeleteDialog = false }
        )
    }

    // Diálogo de re-autenticación (pide la contraseña actual antes de acciones sensibles).
    showReauthDialog?.let { onReauthSuccess ->
        ReauthenticationDialog(
            viewModel = viewModel,
            onDismiss = { showReauthDialog = null },
            onSuccess = {
                onReauthSuccess() // Ejecuta la acción pendiente (ej. cambiar contraseña)
                showReauthDialog = null
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Configuración de la cuenta") },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "Volver") } }
            )
        },
        modifier = modifier
    ) { innerPadding ->
        Column(modifier = Modifier.fillMaxSize().padding(innerPadding).padding(16.dp)) {
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                
                // Sección: Cambiar nombre de usuario
                Text("Cambiar nombre de usuario", style = MaterialTheme.typography.titleMedium)
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    UsernameTextField(value = username, onValueChange = { username = it }, modifier = Modifier.weight(1f))
                    Button(onClick = { viewModel.updateUsername(username) }) { Text("Guardar") }
                }

                // Sección: Opciones específicas para usuarios con contraseña
                if (hasPasswordProvider) {
                    Text("Cambiar correo electrónico", style = MaterialTheme.typography.titleMedium)
                    OutlinedTextField(
                        value = "",
                        onValueChange = {},
                        label = { Text("Próximamente") },
                        enabled = false,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Text("Cambiar contraseña", style = MaterialTheme.typography.titleMedium)
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            PasswordTextField(
                                value = password,
                                onValueChange = { password = it },
                                label = "Nueva contraseña",
                                supportingText = { Text("Mín. 6 caracteres, 1 mayúscula, 1 minúscula, 1 número.") },
                                isError = updateError != null,
                                modifier = Modifier.weight(1f)
                            )
                            // Al hacer clic, primero pedimos re-autenticación.
                            Button(onClick = { showReauthDialog = { viewModel.updateUserPassword(password) } }) { Text("Guardar") }
                        }
                    }
                }
                
                // Sección: Teléfono (Placeholder)
                Text("Número de teléfono", style = MaterialTheme.typography.titleMedium)
                OutlinedTextField(value = "", onValueChange = {}, label = { Text("Próximamente") }, enabled = false, modifier = Modifier.fillMaxWidth())
            }

            // Botón de zona de peligro: Eliminar cuenta
            Button(
                onClick = { showDeleteDialog = true },
                colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Eliminar Cuenta")
            }
        }
    }
}

@Composable
private fun ErrorDialog(error: String, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Error") },
        text = { Text(error) },
        confirmButton = {
            Button(onClick = onDismiss) {
                Text("Aceptar")
            }
        }
    )
}

@Composable
private fun SuccessDialog(message: String, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Éxito") },
        text = { Text(message) },
        confirmButton = {
            Button(onClick = onDismiss) {
                Text("Aceptar")
            }
        }
    )
}

@Composable
private fun ReauthenticationDialog(
    viewModel: AuthViewModel,
    onDismiss: () -> Unit,
    onSuccess: () -> Unit
) {
    var password by remember { mutableStateOf("") }
    var error by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Verifica tu identidad") },
        text = {
            Column {
                Text("Por seguridad, ingresa tu contraseña actual para continuar.")
                Spacer(modifier = Modifier.height(8.dp))
                PasswordTextField(
                    value = password,
                    onValueChange = { password = it; error = null },
                    label = "Contraseña actual",
                    isError = error != null
                )
                error?.let { Text(it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall) }
            }
        },
        confirmButton = {
            Button(onClick = {
                viewModel.reauthenticate(password,
                    onSuccess = onSuccess,
                    onError = { error = it }
                )
            }) {
                Text("Confirmar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancelar") }
        }
    )
}

@Composable
private fun DeleteConfirmationDialog(onConfirm: () -> Unit, onDismiss: () -> Unit) {
    var countdown by remember { mutableStateOf(3) }
    var buttonEnabled by remember { mutableStateOf(false) }

    // Cuenta regresiva de seguridad para evitar eliminaciones accidentales.
    LaunchedEffect(Unit) {
        while (countdown > 0) { delay(1000); countdown-- }
        buttonEnabled = true
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("¿Eliminar Cuenta?") },
        text = { Text("Esta acción es irreversible. Para continuar, espera a que el botón se active.") },
        confirmButton = {
            Button(
                onClick = onConfirm,
                enabled = buttonEnabled,
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (buttonEnabled) Color.Red else MaterialTheme.colorScheme.surfaceVariant,
                    contentColor = if (buttonEnabled) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                )
            ) {
                Text(if (buttonEnabled) "Eliminar" else "Espera... ($countdown)")
            }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancelar") } }
    )
}