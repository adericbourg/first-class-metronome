package dev.dericbourg.firstclassmetronone.presentation.settings

import dev.dericbourg.firstclassmetronone.data.settings.AppSettings

data class SettingsState(
    val bpmIncrement: Int = AppSettings.DEFAULT_BPM_INCREMENT,
    val hapticFeedbackEnabled: Boolean = AppSettings.DEFAULT_HAPTIC_FEEDBACK_ENABLED,
    val isHapticSupported: Boolean = false,
    val showResetDialog: Boolean = false
)
