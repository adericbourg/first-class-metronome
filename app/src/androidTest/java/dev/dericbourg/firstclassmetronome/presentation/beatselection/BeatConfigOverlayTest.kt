package dev.dericbourg.firstclassmetronome.presentation.beatselection

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dev.dericbourg.firstclassmetronome.domain.model.BeatOutput
import dev.dericbourg.firstclassmetronome.domain.model.ClickSound
import dev.dericbourg.firstclassmetronome.presentation.theme.FirstClassMetronomeTheme
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@HiltAndroidTest
class BeatConfigOverlayTest {

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
    fun showsBeatCount_andOneDropdownPerBeat() {
        composeTestRule.setContent {
            FirstClassMetronomeTheme {
                BeatConfigOverlay(
                    beatPattern = listOf(click, BeatOutput.NoSound),
                    onSetBeatCount = {},
                    onSetBeatOutput = { _, _ -> },
                    onClose = {}
                )
            }
        }

        composeTestRule.onNodeWithContentDescription("2 beats per measure").assertIsDisplayed()
        composeTestRule.onNodeWithContentDescription("Beat 1 sound: Click").assertIsDisplayed()
        composeTestRule.onNodeWithContentDescription("Beat 2 sound: No sound").assertIsDisplayed()
    }

    @Test
    fun addBeat_invokesCallbackWithIncrementedCount() {
        var requestedCount = -1
        composeTestRule.setContent {
            FirstClassMetronomeTheme {
                BeatConfigOverlay(
                    beatPattern = listOf(click),
                    onSetBeatCount = { requestedCount = it },
                    onSetBeatOutput = { _, _ -> },
                    onClose = {}
                )
            }
        }

        composeTestRule.onNodeWithContentDescription("Add a beat").performClick()

        assertEquals(2, requestedCount)
    }

    @Test
    fun done_invokesClose() {
        var closed = false
        composeTestRule.setContent {
            FirstClassMetronomeTheme {
                BeatConfigOverlay(
                    beatPattern = listOf(click),
                    onSetBeatCount = {},
                    onSetBeatOutput = { _, _ -> },
                    onClose = { closed = true }
                )
            }
        }

        composeTestRule.onNodeWithText("Done").performClick()

        assertEquals(true, closed)
    }
}
