package com.oolestudio.tamashi.data.tutorial

import kotlinx.coroutines.flow.StateFlow

interface TutorialRepository {
    val state: StateFlow<TutorialState?>
    fun loadTutorial(tutorialId: String, steps: List<TutorialStep>, startStepId: String? = steps.firstOrNull()?.id)
    fun next()
    fun dismiss()
    fun reset()
}
