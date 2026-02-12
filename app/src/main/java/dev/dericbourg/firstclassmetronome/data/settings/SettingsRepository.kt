package dev.dericbourg.firstclassmetronome.data.settings

import kotlinx.coroutines.flow.Flow

interface SettingsRepository {
    val settings: Flow<AppSettings>

    suspend fun setBpmIncrement(value: Int)
    suspend fun setHapticFeedbackEnabled(enabled: Boolean)
    suspend fun setHapticStrength(strength: HapticStrength)
    suspend fun setThemeMode(mode: ThemeMode)
    suspend fun setDynamicColorsEnabled(enabled: Boolean)
    suspend fun resetToDefaults()
}
