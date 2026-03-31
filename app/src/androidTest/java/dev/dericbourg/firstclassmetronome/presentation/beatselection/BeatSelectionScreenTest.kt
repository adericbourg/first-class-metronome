package dev.dericbourg.firstclassmetronome.presentation.beatselection

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dev.dericbourg.firstclassmetronome.presentation.taptempo.TapTempoState
import dev.dericbourg.firstclassmetronome.presentation.theme.FirstClassMetronomeTheme
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@HiltAndroidTest
class BeatSelectionScreenTest {

    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeTestRule = createComposeRule()

    @Before
    fun setup() {
        hiltRule.inject()
    }

    @Test
    fun title_isDisplayed() {
        composeTestRule.setContent {
            FirstClassMetronomeTheme {
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
                    onNavigateToSettings = {}
                )
            }
        }

        composeTestRule.onNodeWithText("First Class Metronome").assertIsDisplayed()
    }

    @Test
    fun defaultBpm_hasSelectedContentDescription() {
        composeTestRule.setContent {
            FirstClassMetronomeTheme {
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
                    onNavigateToSettings = {}
                )
            }
        }

        composeTestRule.onNodeWithContentDescription("60 BPM, selected").assertIsDisplayed()
    }

    @Test
    fun unselectedBpm_hasSelectContentDescription() {
        composeTestRule.setContent {
            FirstClassMetronomeTheme {
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
                    onNavigateToSettings = {}
                )
            }
        }

        composeTestRule.onNodeWithContentDescription("Select 120 BPM").assertIsDisplayed()
    }

    @Test
    fun clickingBpm_callsOnBpmSelected() {
        var selectedBpm = 0

        composeTestRule.setContent {
            FirstClassMetronomeTheme {
                BeatSelectionContent(
                    state = BeatSelectionState(),
                    tapTempoState = TapTempoState(),
                    onBpmSelected = { selectedBpm = it },
                    onDecreaseBpm = {},
                    onIncreaseBpm = {},
                    onPlayToggle = {},
                    onTapTempo = {},
                    onTap = {},
                    onApplyTappedBpm = {},
                    onCancelTapTempo = {},
                    onNavigateToWorkLog = {},
                    onNavigateToSettings = {}
                )
            }
        }

        composeTestRule.onNodeWithContentDescription("Select 120 BPM").performClick()

        assert(selectedBpm == 120) { "Expected 120 but got $selectedBpm" }
    }

    @Test
    fun currentTempoDisplay_showsSelectedBpm() {
        composeTestRule.setContent {
            FirstClassMetronomeTheme {
                BeatSelectionContent(
                    state = BeatSelectionState(selectedBpm = 90),
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
                    onNavigateToSettings = {}
                )
            }
        }

        composeTestRule.onNodeWithText("90 BPM").assertIsDisplayed()
    }

    @Test
    fun buttonBar_decreaseTempoButton_hasContentDescription() {
        composeTestRule.setContent {
            FirstClassMetronomeTheme {
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
                    onNavigateToSettings = {}
                )
            }
        }

        composeTestRule.onNodeWithContentDescription("Decrease tempo by 5").assertIsDisplayed()
    }

    @Test
    fun buttonBar_increaseTempoButton_hasContentDescription() {
        composeTestRule.setContent {
            FirstClassMetronomeTheme {
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
                    onNavigateToSettings = {}
                )
            }
        }

        composeTestRule.onNodeWithContentDescription("Increase tempo by 5").assertIsDisplayed()
    }

    @Test
    fun buttonBar_tapTempoButton_hasContentDescription() {
        composeTestRule.setContent {
            FirstClassMetronomeTheme {
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
                    onNavigateToSettings = {}
                )
            }
        }

        composeTestRule.onNodeWithContentDescription("Tap tempo").assertIsDisplayed()
    }

    @Test
    fun buttonBar_whenNotPlaying_showsStartMetronome() {
        composeTestRule.setContent {
            FirstClassMetronomeTheme {
                BeatSelectionContent(
                    state = BeatSelectionState(isPlaying = false),
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
                    onNavigateToSettings = {}
                )
            }
        }

        composeTestRule.onNodeWithContentDescription("Start metronome").assertIsDisplayed()
    }

    @Test
    fun buttonBar_whenPlaying_showsStopMetronome() {
        composeTestRule.setContent {
            FirstClassMetronomeTheme {
                BeatSelectionContent(
                    state = BeatSelectionState(isPlaying = true),
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
                    onNavigateToSettings = {}
                )
            }
        }

        composeTestRule.onNodeWithContentDescription("Stop metronome").assertIsDisplayed()
    }

    @Test
    fun playButton_callsOnPlayToggle() {
        var playToggleCalled = false

        composeTestRule.setContent {
            FirstClassMetronomeTheme {
                BeatSelectionContent(
                    state = BeatSelectionState(),
                    tapTempoState = TapTempoState(),
                    onBpmSelected = {},
                    onDecreaseBpm = {},
                    onIncreaseBpm = {},
                    onPlayToggle = { playToggleCalled = true },
                    onTapTempo = {},
                    onTap = {},
                    onApplyTappedBpm = {},
                    onCancelTapTempo = {},
                    onNavigateToWorkLog = {},
                    onNavigateToSettings = {}
                )
            }
        }

        composeTestRule.onNodeWithContentDescription("Start metronome").performClick()

        assert(playToggleCalled) { "Expected onPlayToggle to be called" }
    }

    @Test
    fun allBpmValues_areDisplayed() {
        composeTestRule.setContent {
            FirstClassMetronomeTheme {
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
                    onNavigateToSettings = {}
                )
            }
        }

        BeatSelectionState.BPM_VALUES.forEach { bpm ->
            composeTestRule.onNodeWithText(bpm.toString()).assertIsDisplayed()
        }
    }
}
