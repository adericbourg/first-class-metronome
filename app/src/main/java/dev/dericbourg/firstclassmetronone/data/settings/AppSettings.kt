package dev.dericbourg.firstclassmetronone.data.settings

data class AppSettings(
    val bpmIncrement: Int = DEFAULT_BPM_INCREMENT,
    val hapticFeedbackEnabled: Boolean = DEFAULT_HAPTIC_FEEDBACK_ENABLED
) {
    companion object {
        const val DEFAULT_BPM_INCREMENT = 5
        const val DEFAULT_HAPTIC_FEEDBACK_ENABLED = false
        const val MIN_BPM_INCREMENT = 1
        const val MAX_BPM_INCREMENT = 20
    }
}
