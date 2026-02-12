package dev.dericbourg.firstclassmetronome.data.settings

data class AppSettings(
    val bpmIncrement: Int = DEFAULT_BPM_INCREMENT,
    val hapticFeedbackEnabled: Boolean = DEFAULT_HAPTIC_FEEDBACK_ENABLED,
    val hapticStrength: HapticStrength = DEFAULT_HAPTIC_STRENGTH,
    val themeMode: ThemeMode = DEFAULT_THEME_MODE,
    val dynamicColorsEnabled: Boolean = DEFAULT_DYNAMIC_COLORS_ENABLED
) {
    companion object {
        const val DEFAULT_BPM_INCREMENT = 5
        const val DEFAULT_HAPTIC_FEEDBACK_ENABLED = false
        val DEFAULT_HAPTIC_STRENGTH = HapticStrength.MEDIUM
        val DEFAULT_THEME_MODE = ThemeMode.SYSTEM_DEFAULT
        const val DEFAULT_DYNAMIC_COLORS_ENABLED = true
        const val MIN_BPM_INCREMENT = 1
        const val MAX_BPM_INCREMENT = 20
    }
}
