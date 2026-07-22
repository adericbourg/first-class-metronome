package dev.dericbourg.firstclassmetronome.presentation.about

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dev.dericbourg.firstclassmetronome.presentation.theme.FirstClassMetronomeTheme
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@HiltAndroidTest
class AboutContentTest {

    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeTestRule = createComposeRule()

    private val defaultState = AboutState(
        versionDisplay = "v1.3.0 (13)",
        systemInfo = "Android 14 (API 34) · Pixel 7"
    )

    @Before
    fun setup() {
        hiltRule.inject()
    }

    @Test
    fun appName_isDisplayed() {
        composeTestRule.setContent {
            FirstClassMetronomeTheme {
                AboutContent(state = defaultState, onNavigateBack = {}, onOpenSource = {}, onSendEmail = {})
            }
        }

        composeTestRule.onNodeWithText("First-class Metronome").assertIsDisplayed()
    }

    @Test
    fun versionDisplay_isDisplayed() {
        composeTestRule.setContent {
            FirstClassMetronomeTheme {
                AboutContent(state = defaultState, onNavigateBack = {}, onOpenSource = {}, onSendEmail = {})
            }
        }

        composeTestRule.onNodeWithText("v1.3.0 (13)").assertIsDisplayed()
    }

    @Test
    fun systemInfo_isDisplayed() {
        composeTestRule.setContent {
            FirstClassMetronomeTheme {
                AboutContent(state = defaultState, onNavigateBack = {}, onOpenSource = {}, onSendEmail = {})
            }
        }

        composeTestRule.onNodeWithText("Android 14 (API 34) · Pixel 7").assertIsDisplayed()
    }

    @Test
    fun tappingSourceLink_callsOnOpenSource() {
        var openSourceCalled = false

        composeTestRule.setContent {
            FirstClassMetronomeTheme {
                AboutContent(
                    state = defaultState,
                    onNavigateBack = {},
                    onOpenSource = { openSourceCalled = true },
                    onSendEmail = {}
                )
            }
        }

        composeTestRule.onNodeWithContentDescription("Open source code on GitHub").performClick()

        assert(openSourceCalled) { "Expected onOpenSource to be called" }
    }

    @Test
    fun tappingEmail_callsOnSendEmail() {
        var sendEmailCalled = false

        composeTestRule.setContent {
            FirstClassMetronomeTheme {
                AboutContent(
                    state = defaultState,
                    onNavigateBack = {},
                    onOpenSource = {},
                    onSendEmail = { sendEmailCalled = true }
                )
            }
        }

        composeTestRule.onNodeWithContentDescription("Send email to ${defaultState.authorEmail}").performClick()

        assert(sendEmailCalled) { "Expected onSendEmail to be called" }
    }

    @Test
    fun backButton_callsOnNavigateBack() {
        var backCalled = false

        composeTestRule.setContent {
            FirstClassMetronomeTheme {
                AboutContent(
                    state = defaultState,
                    onNavigateBack = { backCalled = true },
                    onOpenSource = {},
                    onSendEmail = {}
                )
            }
        }

        composeTestRule.onNodeWithContentDescription("Go back").performClick()

        assert(backCalled) { "Expected onNavigateBack to be called" }
    }
}
