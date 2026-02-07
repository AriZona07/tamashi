package com.oolestudio.tamashi.ui.components

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration

/**
 * Componente reutilizable para mostrar un enlace de crédito.
 * @param text Texto a mostrar.
 * @param url URL a la que redirigir.
 * @param modifier Modificador.
 */
@Composable
fun CreditLink(
    text: String,
    url: String,
    modifier: Modifier = Modifier
) {
    val annotatedString = buildAnnotatedString {
        append(text)
        addLink(
            url = LinkAnnotation.Url(url),
            start = 0,
            end = text.length
        )
        addStyle(
            style = SpanStyle(
                textDecoration = TextDecoration.Underline,
                color = MaterialTheme.colorScheme.primary
            ),
            start = 0,
            end = text.length
        )
    }

    Text(
        text = annotatedString,
        style = MaterialTheme.typography.bodyLarge,
        textAlign = TextAlign.Center,
        modifier = modifier
    )
}
