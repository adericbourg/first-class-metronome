package dev.dericbourg.firstclassmetronone.presentation.settings

import android.os.Vibrator
import dev.dericbourg.firstclassmetronone.data.settings.AppSettings
import dev.dericbourg.firstclassmetronone.data.settings.SettingsRepository
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class SettingsViewModelTest {

    private val testDispatcher = UnconfinedTestDispatcher()
    private lateinit var settingsRepository: SettingsRepository
    private lateinit var vibrator: Vibrator
    private lateinit var settingsFlow: MutableStateFlow<AppSettings>
    private lateinit var viewModel: SettingsViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)

        settingsFlow = MutableStateFlow(AppSettings())
        settingsRepository = mockk(relaxed = true) {
            every { settings } returns settingsFlow
        }
        vibrator = mockk(relaxed = true) {
            every { hasVibrator() } returns true
        }

        viewModel = SettingsViewModel(settingsRepository, vibrator)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun initialState_hasDefaultValues() {
        assertEquals(AppSettings.DEFAULT_BPM_INCREMENT, viewModel.state.value.bpmIncrement)
        assertEquals(AppSettings.DEFAULT_HAPTIC_FEEDBACK_ENABLED, viewModel.state.value.hapticFeedbackEnabled)
    }

    @Test
    fun initialState_checksHapticSupport() {
        assertTrue(viewModel.state.value.isHapticSupported)
    }

    @Test
    fun initialState_whenNoVibrator_hapticNotSupported() {
        val noVibratorDevice = mockk<Vibrator> {
            every { hasVibrator() } returns false
        }
        val vm = SettingsViewModel(settingsRepository, noVibratorDevice)

        assertFalse(vm.state.value.isHapticSupported)
    }

    @Test
    fun settingsFlow_updatesState() {
        settingsFlow.value = AppSettings(bpmIncrement = 10, hapticFeedbackEnabled = true)

        assertEquals(10, viewModel.state.value.bpmIncrement)
        assertTrue(viewModel.state.value.hapticFeedbackEnabled)
    }

    @Test
    fun setBpmIncrement_callsRepository() {
        viewModel.setBpmIncrement(10)

        coVerify { settingsRepository.setBpmIncrement(10) }
    }

    @Test
    fun setBpmIncrement_clampsToMinimum() {
        viewModel.setBpmIncrement(0)

        coVerify { settingsRepository.setBpmIncrement(AppSettings.MIN_BPM_INCREMENT) }
    }

    @Test
    fun setBpmIncrement_clampsToMaximum() {
        viewModel.setBpmIncrement(100)

        coVerify { settingsRepository.setBpmIncrement(AppSettings.MAX_BPM_INCREMENT) }
    }

    @Test
    fun setHapticFeedback_callsRepository() {
        viewModel.setHapticFeedback(true)

        coVerify { settingsRepository.setHapticFeedbackEnabled(true) }
    }

    @Test
    fun showResetDialog_setsShowResetDialogTrue() {
        viewModel.showResetDialog()

        assertTrue(viewModel.state.value.showResetDialog)
    }

    @Test
    fun dismissDialog_setsShowResetDialogFalse() {
        viewModel.showResetDialog()

        viewModel.dismissDialog()

        assertFalse(viewModel.state.value.showResetDialog)
    }

    @Test
    fun confirmReset_callsRepositoryResetToDefaults() {
        viewModel.showResetDialog()

        viewModel.confirmReset()

        coVerify { settingsRepository.resetToDefaults() }
    }

    @Test
    fun confirmReset_dismissesDialog() {
        viewModel.showResetDialog()

        viewModel.confirmReset()

        assertFalse(viewModel.state.value.showResetDialog)
    }
}
