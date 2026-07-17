package dev.dericbourg.firstclassmetronome.presentation.beatselection

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dev.dericbourg.firstclassmetronome.domain.model.BeatOutput
import dev.dericbourg.firstclassmetronome.domain.model.ClickSound
import dev.dericbourg.firstclassmetronome.presentation.theme.FirstClassMetronomeTheme
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@HiltAndroidTest
class BeatIndicatorRowTest {

    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeTestRule = createComposeRule()

    private val click = BeatOutput.Sound(ClickSound.CLICK)

    @Before
    fun setup() {
        hiltRule.inject()
    }

    @Test
    fun rendersOneSquarePerBeat_withLabelledContentDescriptions() {
        composeTestRule.setContent {
            FirstClassMetronomeTheme {
                BeatIndicatorRow(
                    beatPattern = listOf(click, BeatOutput.NoSound, BeatOutput.HapticOnly),
                    currentBeat = BeatSelectionState.NO_BEAT
                )
            }
        }

        composeTestRule.onNodeWithContentDescription("Beat 1: Click").assertIsDisplayed()
        composeTestRule.onNodeWithContentDescription("Beat 2: No sound").assertIsDisplayed()
        composeTestRule.onNodeWithContentDescription("Beat 3: Haptic feedback only").assertIsDisplayed()
    }

    @Test
    fun currentBeat_isMarkedInContentDescription() {
        composeTestRule.setContent {
            FirstClassMetronomeTheme {
                BeatIndicatorRow(
                    beatPattern = listOf(click, click),
                    currentBeat = 1
                )
            }
        }

        composeTestRule.onNodeWithContentDescription("Beat 2: Click, current").assertIsDisplayed()
        composeTestRule.onNodeWithContentDescription("Beat 1: Click").assertIsDisplayed()
    }
}
