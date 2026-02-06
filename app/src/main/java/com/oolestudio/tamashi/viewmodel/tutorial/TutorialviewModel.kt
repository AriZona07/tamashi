package com.oolestudio.tamashi.viewmodel.tutorial

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.oolestudio.tamashi.data.tutorial.TutorialRepository
import com.oolestudio.tamashi.data.tutorial.TutorialState
import com.oolestudio.tamashi.data.tutorial.TutorialStep
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

data class TutorialUiState(
    val visible: Boolean = false,
    val step: TutorialStep? = null
)

class TutorialViewModel(
    private val repo: TutorialRepository
) : ViewModel() {
    val uiState: StateFlow<TutorialUiState> =
        repo.state.map { s: TutorialState? ->
            val step = s?.currentStepId?.let { s.steps[it] }
            TutorialUiState(visible = s?.isVisible == true && step != null, step = step)
        }.stateIn(viewModelScope, SharingStarted.Lazily, TutorialUiState())

    fun next() = repo.next()
    fun dismiss() = repo.dismiss()
    fun reset() = repo.reset()
}
