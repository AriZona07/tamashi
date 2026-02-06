package com.oolestudio.tamashi.ui.tutorial

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * SpeechBubble: globo de texto reutilizable.
 * - Fondo lila, texto negro por defecto y tamaño ~12sp legible.
 * - Soporta "cola" opcional (triángulo) que apunta hacia abajo a la derecha.
 *   Ideal para ubicar encima del Tamashi sin desplazarlo.
 */
@Composable
fun SpeechBubble(
    text: String,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(12.dp),
    backgroundColor: Color = Color(0xFFCE93D8), // lila suave
    textColor: Color = Color.Black,
    fontSize: TextUnit = 12.sp,
    showTail: Boolean = false,
    tailSizeDp: Float = 12f,
    tailOffsetRightDp: Float = 16f // desplazamiento desde el borde derecho para apuntar al Tamashi
) {
    val tailSize = tailSizeDp.dp
    val tailOffsetRight = tailOffsetRightDp.dp

    val bubbleModifier = modifier
        .shadow(4.dp, RoundedCornerShape(16.dp))
        .background(backgroundColor, RoundedCornerShape(16.dp))
        .then(
            if (showTail) Modifier.drawBehind {
                // Dibuja un triángulo (cola) en la parte inferior apuntando hacia abajo a la derecha
                val tailWidth = tailSize.toPx()
                val tailHeight = tailSize.toPx()
                val right = size.width
                val baseCenterX = right - tailOffsetRight.toPx()
                val baseY = size.height

                val path = Path().apply {
                    // Triángulo isósceles: base en el borde inferior del globo, vértice hacia abajo
                    moveTo(baseCenterX - tailWidth / 2f, baseY) // base izquierda
                    lineTo(baseCenterX + tailWidth / 2f, baseY) // base derecha
                    lineTo(baseCenterX, baseY + tailHeight) // vértice hacia abajo
                    close()
                }
                drawIntoCanvas { canvas ->
                    canvas.drawPath(path, androidx.compose.ui.graphics.Paint().apply { color = backgroundColor })
                }
            } else Modifier
        )
        .padding(contentPadding)

    Text(
        text = text,
        color = textColor,
        fontSize = fontSize,
        style = MaterialTheme.typography.bodyMedium,
        textAlign = TextAlign.Start,
        modifier = bubbleModifier
    )
}
