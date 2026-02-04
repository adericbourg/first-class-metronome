package dev.dericbourg.firstclassmetronome.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import dagger.hilt.android.AndroidEntryPoint
import dev.dericbourg.firstclassmetronome.presentation.beatselection.BeatSelectionScreen
import dev.dericbourg.firstclassmetronome.presentation.navigation.AppScreen
import dev.dericbourg.firstclassmetronome.presentation.settings.SettingsScreen
import dev.dericbourg.firstclassmetronome.presentation.theme.FirstClassMetronomeTheme
import dev.dericbourg.firstclassmetronome.presentation.worklog.WorkLogScreen

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            FirstClassMetronomeTheme {
                var currentScreen: AppScreen by rememberSaveable { mutableStateOf(AppScreen.BeatSelection) }

                Surface(
                    modifier = Modifier
                        .fillMaxSize()
                        .safeDrawingPadding(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    when (currentScreen) {
                        AppScreen.BeatSelection -> BeatSelectionScreen(
                            onNavigateToWorkLog = { currentScreen = AppScreen.WorkLog },
                            onNavigateToSettings = { currentScreen = AppScreen.Settings }
                        )
                        AppScreen.WorkLog -> WorkLogScreen(
                            onNavigateBack = { currentScreen = AppScreen.BeatSelection }
                        )
                        AppScreen.Settings -> SettingsScreen(
                            onNavigateBack = { currentScreen = AppScreen.BeatSelection }
                        )
                    }
                }
            }
        }
    }
}
