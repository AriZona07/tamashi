package com.oolestudio.tamashi.data.tutorial

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class TutorialRepositoryImpl : TutorialRepository {
    private val _state = MutableStateFlow<TutorialState?>(null)
    override val state: StateFlow<TutorialState?> = _state

    override fun loadTutorial(tutorialId: String, steps: List<TutorialStep>, startStepId: String?) {
        val map = steps.associateBy { it.id }
        _state.value = TutorialState(
            tutorialId = tutorialId,
            steps = map,
            currentStepId = startStepId ?: steps.firstOrNull()?.id,
            isVisible = true
        )
    }

    override fun next() {
        val s = _state.value ?: return
        val current = s.currentStepId?.let { s.steps[it] } ?: return
        val nextId = current.nextStepId
        if (nextId == null) {
            dismiss()
        } else {
            _state.value = s.copy(currentStepId = nextId)
        }
    }

    override fun dismiss() {
        _state.value = _state.value?.copy(isVisible = false)
    }

    override fun reset() {
        val s = _state.value ?: return
        val start = s.steps.values.firstOrNull()?.id
        _state.value = s.copy(currentStepId = start, isVisible = true)
    }
}
