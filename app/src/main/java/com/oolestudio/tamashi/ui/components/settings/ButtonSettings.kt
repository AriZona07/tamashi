package com.oolestudio.tamashi.ui.components.settings

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

/**
 * Componente reutilizable para un botón de configuración.
 *
 * @param text Texto a mostrar en el botón.
 * @param onClick Acción a ejecutar al hacer clic.
 * @param modifier Modificador para aplicar al botón.
 */
@Composable
fun SettingsButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        modifier = modifier.fillMaxWidth()
    ) {
        Text(text)
    }
}

