package com.oolestudio.tamashi.data

import androidx.compose.runtime.Composable

/**
 * Implementación ACTUAL para iOS (simulador).
 * Como aún no hemos implementado el inicio de sesión con Google para iOS,
 * devolvemos una función que no hace nada para satisfacer al compilador.
 */
@Composable
actual fun rememberGoogleAuthHandler(onTokenReceived: (String) -> Unit): () -> Unit {
    return {
        // TODO: Implementar el inicio de sesión con Google para iOS.
        // Esto requerirá usar el SDK de Google para iOS y llamar a la UI nativa.
        println("Google Sign-In no está implementado en iOS todavía.")
    }
}

// El compilador también necesita una implementación para la clase `expect`.
// Aunque no la usemos, debe existir.
actual class GoogleAuthHandler {
    // No se usa en esta implementación.
}