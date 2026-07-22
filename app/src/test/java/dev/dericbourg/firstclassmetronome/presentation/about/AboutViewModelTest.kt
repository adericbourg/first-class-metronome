package dev.dericbourg.firstclassmetronome.presentation.about

import dev.dericbourg.firstclassmetronome.device.AppInfoProvider
import org.junit.Assert.assertEquals
import org.junit.Test

class AboutViewModelTest {

    private class FakeAppInfoProvider(
        override val versionName: String = "1.3.0",
        override val versionCode: Long = 13L,
        override val androidVersionRelease: String = "14",
        override val androidApiLevel: Int = 34,
        override val deviceModel: String = "Pixel 7"
    ) : AppInfoProvider

    @Test
    fun state_whenCreated_exposesFormattedVersionDisplay() {
        val viewModel = AboutViewModel(FakeAppInfoProvider())

        assertEquals("v1.3.0 (13)", viewModel.state.value.versionDisplay)
    }

    @Test
    fun state_whenCreated_exposesFormattedSystemInfo() {
        val viewModel = AboutViewModel(FakeAppInfoProvider())

        assertEquals("Android 14 (API 34) · Pixel 7", viewModel.state.value.systemInfo)
    }

    @Test
    fun state_whenCreated_exposesStaticAppMetadata() {
        val viewModel = AboutViewModel(FakeAppInfoProvider())

        assertEquals("First-class Metronome", viewModel.state.value.appName)
        assertEquals("alban@dericbourg.dev", viewModel.state.value.authorEmail)
        assertEquals("https://github.com/adericbourg/FirstClassMetronome", viewModel.state.value.sourceUrl)
    }
}
