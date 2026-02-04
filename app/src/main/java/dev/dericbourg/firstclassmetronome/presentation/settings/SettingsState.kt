package dev.dericbourg.firstclassmetronome.presentation.settings

import dev.dericbourg.firstclassmetronome.data.settings.AppSettings
import dev.dericbourg.firstclassmetronome.data.settings.HapticStrength

data class SettingsState(
    val bpmIncrement: Int = AppSettings.DEFAULT_BPM_INCREMENT,
    val hapticFeedbackEnabled: Boolean = AppSettings.DEFAULT_HAPTIC_FEEDBACK_ENABLED,
    val hapticStrength: HapticStrength = AppSettings.DEFAULT_HAPTIC_STRENGTH,
    val isHapticSupported: Boolean = false,
    val hasAmplitudeControl: Boolean = false,
    val showResetDialog: Boolean = false
)
