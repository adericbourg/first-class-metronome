package dev.dericbourg.firstclassmetronome.presentation.about

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.dericbourg.firstclassmetronome.device.AppInfoProvider
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class AboutViewModel @Inject constructor(
    appInfoProvider: AppInfoProvider
) : ViewModel() {

    private val _state = MutableStateFlow(
        AboutState(
            versionDisplay = "v${appInfoProvider.versionName} (${appInfoProvider.versionCode})",
            systemInfo = "Android ${appInfoProvider.androidVersionRelease} " +
                    "(API ${appInfoProvider.androidApiLevel}) · ${appInfoProvider.deviceModel}"
        )
    )
    val state: StateFlow<AboutState> = _state.asStateFlow()
}
