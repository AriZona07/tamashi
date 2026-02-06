package com.oolestudio.tamashi.ui.tutorial

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import com.oolestudio.tamashi.viewmodel.tutorial.TutorialViewModel
import com.oolestudio.tamashi.util.tutorial.TutorialLayoutUtils

@Composable
fun TutorialOverlay(
    viewModel: TutorialViewModel,
    modifier: Modifier = Modifier
) {
    val ui = viewModel.uiState.collectAsState().value
    AnimatedVisibility(visible = ui.visible, enter = fadeIn(), exit = fadeOut()) {
        Row(horizontalArrangement = Arrangement.spacedBy(TutorialLayoutUtils.spacing), modifier = modifier) {
            ui.step?.let { step ->
                TamashiAvatar(tamashiName = step.tamashiName, assetOverride = step.assetName)
                SpeechBubble(text = step.text, modifier = Modifier.width(TutorialLayoutUtils.bubbleMaxWidth))
                Spacer(modifier = Modifier.width(TutorialLayoutUtils.spacing))
                Button(onClick = { if (step.nextStepId != null) viewModel.next() else viewModel.dismiss() }) {
                    Text(if (step.nextStepId != null) "Siguiente" else "Cerrar")
                }
            }
        }
    }
}
