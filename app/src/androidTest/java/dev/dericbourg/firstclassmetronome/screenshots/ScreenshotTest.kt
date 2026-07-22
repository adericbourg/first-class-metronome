package dev.dericbourg.firstclassmetronome.screenshots

import androidx.compose.ui.test.junit4.createComposeRule
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dev.dericbourg.firstclassmetronome.presentation.beatselection.BeatSelectionContent
import dev.dericbourg.firstclassmetronome.presentation.beatselection.BeatSelectionState
import dev.dericbourg.firstclassmetronome.presentation.settings.SettingsContent
import dev.dericbourg.firstclassmetronome.presentation.settings.SettingsState
import dev.dericbourg.firstclassmetronome.presentation.taptempo.TapTempoState
import dev.dericbourg.firstclassmetronome.presentation.theme.FirstClassMetronomeTheme
import dev.dericbourg.firstclassmetronome.presentation.worklog.WorkLogContent
import dev.dericbourg.firstclassmetronome.presentation.worklog.WorkLogState
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import tools.fastlane.screengrab.Screengrab
import tools.fastlane.screengrab.locale.LocaleTestRule

@HiltAndroidTest
class ScreenshotTest {

    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeTestRule = createComposeRule()

    @get:Rule(order = 2)
    val localeTestRule = LocaleTestRule()

    @Before
    fun setup() {
        hiltRule.inject()
    }

    @Test
    fun screenshot_ofBeatSelectionDefault_takesScreenshot() {
        composeTestRule.setContent {
            FirstClassMetronomeTheme(dynamicColor = false) {
                BeatSelectionContent(
                    state = BeatSelectionState(),
                    tapTempoState = TapTempoState(),
                    onBpmSelected = {},
                    onDecreaseBpm = {},
                    onIncreaseBpm = {},
                    onPlayToggle = {},
                    onTapTempo = {},
                    onTap = {},
                    onApplyTappedBpm = {},
                    onCancelTapTempo = {},
                    onNavigateToWorkLog = {},
                    onNavigateToSettings = {},
                    onNavigateToAbout = {}
                )
            }
        }
        composeTestRule.waitForIdle()
        Screengrab.screenshot("1_beat_selection")
    }

    @Test
    fun screenshot_ofBeatSelectionPlaying_takesScreenshot() {
        composeTestRule.setContent {
            FirstClassMetronomeTheme(dynamicColor = false) {
                BeatSelectionContent(
                    state = BeatSelectionState(
                        selectedBpm = 120,
                        isPlaying = true
                    ),
                    tapTempoState = TapTempoState(),
                    onBpmSelected = {},
                    onDecreaseBpm = {},
                    onIncreaseBpm = {},
                    onPlayToggle = {},
                    onTapTempo = {},
                    onTap = {},
                    onApplyTappedBpm = {},
                    onCancelTapTempo = {},
                    onNavigateToWorkLog = {},
                    onNavigateToSettings = {},
                    onNavigateToAbout = {}
                )
            }
        }
        composeTestRule.waitForIdle()
        Screengrab.screenshot("2_beat_selection_playing")
    }

    @Test
    fun screenshot_ofSettings_takesScreenshot() {
        composeTestRule.setContent {
            FirstClassMetronomeTheme(dynamicColor = false) {
                SettingsContent(
                    state = SettingsState(),
                    onNavigateBack = {},
                    onBpmIncrementChange = {},
                    onHapticFeedbackChange = {},
                    onHapticStrengthChange = {},
                    onThemeModeChange = {},
                    onDynamicColorsChange = {},
                    onResetClick = {},
                    onConfirmReset = {},
                    onDismissDialog = {}
                )
            }
        }
        composeTestRule.waitForIdle()
        Screengrab.screenshot("3_settings")
    }

    @Test
    fun screenshot_ofWorkLogEmpty_takesScreenshot() {
        composeTestRule.setContent {
            FirstClassMetronomeTheme(dynamicColor = false) {
                WorkLogContent(
                    state = WorkLogState(),
                    onNavigateBack = {},
                    onClearClick = {},
                    onFirstConfirm = {},
                    onSecondConfirm = {},
                    onDismissDialog = {}
                )
            }
        }
        composeTestRule.waitForIdle()
        Screengrab.screenshot("4_work_log")
    }
}
