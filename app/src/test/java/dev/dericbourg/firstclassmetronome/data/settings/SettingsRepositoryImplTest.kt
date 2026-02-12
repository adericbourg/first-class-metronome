package dev.dericbourg.firstclassmetronome.data.settings

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder

@OptIn(ExperimentalCoroutinesApi::class)
class SettingsRepositoryImplTest {

    @get:Rule
    val tempFolder = TemporaryFolder()

    private val testDispatcher = UnconfinedTestDispatcher()
    private lateinit var dataStore: DataStore<Preferences>
    private lateinit var repository: SettingsRepositoryImpl

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)

        dataStore = PreferenceDataStoreFactory.create {
            tempFolder.newFile("test_settings.preferences_pb")
        }
        repository = SettingsRepositoryImpl(dataStore)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun settings_initiallyReturnsDefaults() = runTest {
        val settings = repository.settings.first()

        assertEquals(AppSettings.DEFAULT_BPM_INCREMENT, settings.bpmIncrement)
        assertEquals(AppSettings.DEFAULT_HAPTIC_FEEDBACK_ENABLED, settings.hapticFeedbackEnabled)
        assertEquals(AppSettings.DEFAULT_HAPTIC_STRENGTH, settings.hapticStrength)
        assertEquals(AppSettings.DEFAULT_THEME_MODE, settings.themeMode)
        assertEquals(AppSettings.DEFAULT_DYNAMIC_COLORS_ENABLED, settings.dynamicColorsEnabled)
    }

    @Test
    fun setBpmIncrement_updatesValue() = runTest {
        repository.setBpmIncrement(10)

        val settings = repository.settings.first()
        assertEquals(10, settings.bpmIncrement)
    }

    @Test
    fun setBpmIncrement_clampsToMinimum() = runTest {
        repository.setBpmIncrement(0)

        val settings = repository.settings.first()
        assertEquals(AppSettings.MIN_BPM_INCREMENT, settings.bpmIncrement)
    }

    @Test
    fun setBpmIncrement_clampsToMaximum() = runTest {
        repository.setBpmIncrement(100)

        val settings = repository.settings.first()
        assertEquals(AppSettings.MAX_BPM_INCREMENT, settings.bpmIncrement)
    }

    @Test
    fun setHapticFeedbackEnabled_updatesValue() = runTest {
        repository.setHapticFeedbackEnabled(true)

        val settings = repository.settings.first()
        assertTrue(settings.hapticFeedbackEnabled)
    }

    @Test
    fun setHapticFeedbackEnabled_canBeDisabled() = runTest {
        repository.setHapticFeedbackEnabled(true)
        repository.setHapticFeedbackEnabled(false)

        val settings = repository.settings.first()
        assertFalse(settings.hapticFeedbackEnabled)
    }

    @Test
    fun resetToDefaults_restoresDefaultValues() = runTest {
        repository.setBpmIncrement(15)
        repository.setHapticFeedbackEnabled(true)
        repository.setHapticStrength(HapticStrength.STRONG)
        repository.setThemeMode(ThemeMode.DARK)
        repository.setDynamicColorsEnabled(false)

        repository.resetToDefaults()

        val settings = repository.settings.first()
        assertEquals(AppSettings.DEFAULT_BPM_INCREMENT, settings.bpmIncrement)
        assertEquals(AppSettings.DEFAULT_HAPTIC_FEEDBACK_ENABLED, settings.hapticFeedbackEnabled)
        assertEquals(AppSettings.DEFAULT_HAPTIC_STRENGTH, settings.hapticStrength)
        assertEquals(AppSettings.DEFAULT_THEME_MODE, settings.themeMode)
        assertEquals(AppSettings.DEFAULT_DYNAMIC_COLORS_ENABLED, settings.dynamicColorsEnabled)
    }

    @Test
    fun setHapticStrength_updatesValue() = runTest {
        repository.setHapticStrength(HapticStrength.STRONG)

        val settings = repository.settings.first()
        assertEquals(HapticStrength.STRONG, settings.hapticStrength)
    }

    @Test
    fun setHapticStrength_canChangeToAnyLevel() = runTest {
        repository.setHapticStrength(HapticStrength.LIGHT)
        assertEquals(HapticStrength.LIGHT, repository.settings.first().hapticStrength)

        repository.setHapticStrength(HapticStrength.MEDIUM)
        assertEquals(HapticStrength.MEDIUM, repository.settings.first().hapticStrength)

        repository.setHapticStrength(HapticStrength.STRONG)
        assertEquals(HapticStrength.STRONG, repository.settings.first().hapticStrength)
    }

    @Test
    fun setThemeMode_updatesValue() = runTest {
        repository.setThemeMode(ThemeMode.DARK)

        val settings = repository.settings.first()
        assertEquals(ThemeMode.DARK, settings.themeMode)
    }

    @Test
    fun setThemeMode_canChangeToAnyMode() = runTest {
        repository.setThemeMode(ThemeMode.SYSTEM_DEFAULT)
        assertEquals(ThemeMode.SYSTEM_DEFAULT, repository.settings.first().themeMode)

        repository.setThemeMode(ThemeMode.LIGHT)
        assertEquals(ThemeMode.LIGHT, repository.settings.first().themeMode)

        repository.setThemeMode(ThemeMode.DARK)
        assertEquals(ThemeMode.DARK, repository.settings.first().themeMode)
    }

    @Test
    fun setDynamicColorsEnabled_updatesValue() = runTest {
        repository.setDynamicColorsEnabled(false)

        val settings = repository.settings.first()
        assertFalse(settings.dynamicColorsEnabled)
    }

    @Test
    fun setDynamicColorsEnabled_canBeToggled() = runTest {
        repository.setDynamicColorsEnabled(false)
        assertFalse(repository.settings.first().dynamicColorsEnabled)

        repository.setDynamicColorsEnabled(true)
        assertTrue(repository.settings.first().dynamicColorsEnabled)
    }
}
