package dev.dericbourg.firstclassmetronome.presentation

import dev.dericbourg.firstclassmetronome.data.settings.AppSettings
import dev.dericbourg.firstclassmetronome.data.settings.SettingsRepository
import dev.dericbourg.firstclassmetronome.data.settings.ThemeMode
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
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class MainViewModelTest {

    private val testDispatcher = UnconfinedTestDispatcher()
    private lateinit var settingsRepository: SettingsRepository
    private lateinit var settingsFlow: MutableStateFlow<AppSettings>
    private lateinit var viewModel: MainViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)

        settingsFlow = MutableStateFlow(AppSettings())
        settingsRepository = mockk(relaxed = true) {
            every { settings } returns settingsFlow
        }

        viewModel = MainViewModel(settingsRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun initialState_hasDefaultValues() {
        assertEquals(AppSettings.DEFAULT_THEME_MODE, viewModel.state.value.themeMode)
        assertTrue(viewModel.state.value.dynamicColorsEnabled)
    }

    @Test
    fun settingsFlow_updatesState() {
        settingsFlow.value = AppSettings(
            themeMode = ThemeMode.DARK,
            dynamicColorsEnabled = false
        )

        assertEquals(ThemeMode.DARK, viewModel.state.value.themeMode)
        assertEquals(false, viewModel.state.value.dynamicColorsEnabled)
    }
}
