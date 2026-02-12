package dev.dericbourg.firstclassmetronome.presentation.settings

import android.os.Build
import android.os.Vibrator
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.dericbourg.firstclassmetronome.data.settings.AppSettings
import dev.dericbourg.firstclassmetronome.data.settings.HapticStrength
import dev.dericbourg.firstclassmetronome.data.settings.SettingsRepository
import dev.dericbourg.firstclassmetronome.data.settings.ThemeMode
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val settingsRepository: SettingsRepository,
    private val vibrator: Vibrator
) : ViewModel() {

    private val _state = MutableStateFlow(SettingsState())
    val state: StateFlow<SettingsState> = _state.asStateFlow()

    init {
        val isHapticSupported = vibrator.hasVibrator()
        val hasAmplitudeControl = vibrator.hasAmplitudeControl()
        val isDynamicColorsSupported = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S
        _state.update {
            it.copy(
                isHapticSupported = isHapticSupported,
                hasAmplitudeControl = hasAmplitudeControl,
                isDynamicColorsSupported = isDynamicColorsSupported
            )
        }

        viewModelScope.launch {
            settingsRepository.settings.collect { settings ->
                _state.update {
                    it.copy(
                        bpmIncrement = settings.bpmIncrement,
                        hapticFeedbackEnabled = settings.hapticFeedbackEnabled,
                        hapticStrength = settings.hapticStrength,
                        themeMode = settings.themeMode,
                        dynamicColorsEnabled = settings.dynamicColorsEnabled
                    )
                }
            }
        }
    }

    fun setBpmIncrement(value: Int) {
        val clampedValue = value.coerceIn(AppSettings.MIN_BPM_INCREMENT, AppSettings.MAX_BPM_INCREMENT)
        viewModelScope.launch {
            settingsRepository.setBpmIncrement(clampedValue)
        }
    }

    fun setHapticFeedback(enabled: Boolean) {
        viewModelScope.launch {
            settingsRepository.setHapticFeedbackEnabled(enabled)
        }
    }

    fun setHapticStrength(strength: HapticStrength) {
        viewModelScope.launch {
            settingsRepository.setHapticStrength(strength)
        }
    }

    fun setThemeMode(mode: ThemeMode) {
        viewModelScope.launch {
            settingsRepository.setThemeMode(mode)
        }
    }

    fun setDynamicColorsEnabled(enabled: Boolean) {
        viewModelScope.launch {
            settingsRepository.setDynamicColorsEnabled(enabled)
        }
    }

    fun showResetDialog() {
        _state.update { it.copy(showResetDialog = true) }
    }

    fun confirmReset() {
        viewModelScope.launch {
            settingsRepository.resetToDefaults()
        }
        _state.update { it.copy(showResetDialog = false) }
    }

    fun dismissDialog() {
        _state.update { it.copy(showResetDialog = false) }
    }
}
