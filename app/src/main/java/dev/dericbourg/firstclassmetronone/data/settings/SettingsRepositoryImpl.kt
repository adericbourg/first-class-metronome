package dev.dericbourg.firstclassmetronone.data.settings

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SettingsRepositoryImpl @Inject constructor(
    private val dataStore: DataStore<Preferences>
) : SettingsRepository {

    override val settings: Flow<AppSettings> = dataStore.data.map { preferences ->
        val hapticStrength = preferences[KEY_HAPTIC_STRENGTH]?.let { name ->
            runCatching { HapticStrength.valueOf(name) }.getOrNull()
        } ?: AppSettings.DEFAULT_HAPTIC_STRENGTH

        AppSettings(
            bpmIncrement = preferences[KEY_BPM_INCREMENT] ?: AppSettings.DEFAULT_BPM_INCREMENT,
            hapticFeedbackEnabled = preferences[KEY_HAPTIC_FEEDBACK_ENABLED]
                ?: AppSettings.DEFAULT_HAPTIC_FEEDBACK_ENABLED,
            hapticStrength = hapticStrength
        )
    }

    override suspend fun setBpmIncrement(value: Int) {
        val clampedValue = value.coerceIn(AppSettings.MIN_BPM_INCREMENT, AppSettings.MAX_BPM_INCREMENT)
        dataStore.edit { preferences ->
            preferences[KEY_BPM_INCREMENT] = clampedValue
        }
    }

    override suspend fun setHapticFeedbackEnabled(enabled: Boolean) {
        dataStore.edit { preferences ->
            preferences[KEY_HAPTIC_FEEDBACK_ENABLED] = enabled
        }
    }

    override suspend fun setHapticStrength(strength: HapticStrength) {
        dataStore.edit { preferences ->
            preferences[KEY_HAPTIC_STRENGTH] = strength.name
        }
    }

    override suspend fun resetToDefaults() {
        dataStore.edit { preferences ->
            preferences.remove(KEY_BPM_INCREMENT)
            preferences.remove(KEY_HAPTIC_FEEDBACK_ENABLED)
            preferences.remove(KEY_HAPTIC_STRENGTH)
        }
    }

    companion object {
        private val KEY_BPM_INCREMENT = intPreferencesKey("bpm_increment")
        private val KEY_HAPTIC_FEEDBACK_ENABLED = booleanPreferencesKey("haptic_feedback_enabled")
        private val KEY_HAPTIC_STRENGTH = stringPreferencesKey("haptic_strength")
    }
}
