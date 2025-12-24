package com.oolestudio.tamashi.data

import androidx.compose.runtime.Composable

// Usamos el mecanismo 'expect/actual' de Kotlin Multiplatform.
// Esto nos permite definir una interfaz común aquí (en commonMain) y proveer
// implementaciones específicas para cada plataforma (Android, iOS) en sus respectivas carpetas.

// Esta clase es un marcador de posición. La lógica principal se manejará a través de la función composable.
expect class GoogleAuthHandler

// Esta función composable es el punto de entrada principal para la UI.
// 'rememberGoogleAuthHandler' devolverá una función que, al ser invocada, iniciará el flujo de inicio de sesión de Google.
// El parámetro 'onTokenReceived' es un callback que se ejecutará cuando obtengamos el token de Google exitosamente.
@Composable
expect fun rememberGoogleAuthHandler(onTokenReceived: (String) -> Unit): () -> Unit