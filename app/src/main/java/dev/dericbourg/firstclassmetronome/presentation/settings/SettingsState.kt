package dev.dericbourg.firstclassmetronome.presentation.settings

import dev.dericbourg.firstclassmetronome.data.settings.AppSettings
import dev.dericbourg.firstclassmetronome.data.settings.HapticStrength
import dev.dericbourg.firstclassmetronome.data.settings.ThemeMode

data class SettingsState(
    val bpmIncrement: Int = AppSettings.DEFAULT_BPM_INCREMENT,
    val hapticFeedbackEnabled: Boolean = AppSettings.DEFAULT_HAPTIC_FEEDBACK_ENABLED,
    val hapticStrength: HapticStrength = AppSettings.DEFAULT_HAPTIC_STRENGTH,
    val isHapticSupported: Boolean = false,
    val hasAmplitudeControl: Boolean = false,
    val themeMode: ThemeMode = AppSettings.DEFAULT_THEME_MODE,
    val dynamicColorsEnabled: Boolean = AppSettings.DEFAULT_DYNAMIC_COLORS_ENABLED,
    val isDynamicColorsSupported: Boolean = true,
    val showResetDialog: Boolean = false
)
