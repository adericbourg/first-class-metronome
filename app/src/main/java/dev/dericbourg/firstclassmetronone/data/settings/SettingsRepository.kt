package dev.dericbourg.firstclassmetronone.data.settings

import kotlinx.coroutines.flow.Flow

interface SettingsRepository {
    val settings: Flow<AppSettings>

    suspend fun setBpmIncrement(value: Int)
    suspend fun setHapticFeedbackEnabled(enabled: Boolean)
    suspend fun resetToDefaults()
}
