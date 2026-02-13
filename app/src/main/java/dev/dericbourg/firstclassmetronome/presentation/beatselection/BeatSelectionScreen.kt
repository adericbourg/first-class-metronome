package dev.dericbourg.firstclassmetronome.presentation.beatselection

import android.app.Activity
import android.view.WindowManager
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import dev.dericbourg.firstclassmetronome.presentation.taptempo.TapTempoOverlay
import dev.dericbourg.firstclassmetronome.presentation.taptempo.TapTempoState

@Composable
fun BeatSelectionScreen(
    onNavigateToWorkLog: () -> Unit,
    onNavigateToSettings: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: BeatSelectionViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val tapTempoState by viewModel.tapTempoState.collectAsState()
    val lifecycleOwner = LocalLifecycleOwner.current

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_PAUSE) {
                viewModel.stop()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    KeepScreenOn(enabled = state.isPlaying)

    BeatSelectionContent(
        state = state,
        tapTempoState = tapTempoState,
        onBpmSelected = viewModel::selectBpm,
        onDecreaseBpm = viewModel::decreaseBpm,
        onIncreaseBpm = viewModel::increaseBpm,
        onPlayToggle = viewModel::togglePlayback,
        onTapTempo = viewModel::openTapTempo,
        onTap = viewModel::recordTap,
        onApplyTappedBpm = viewModel::applyTappedBpm,
        onCancelTapTempo = viewModel::closeTapTempo,
        onNavigateToWorkLog = onNavigateToWorkLog,
        onNavigateToSettings = onNavigateToSettings,
        modifier = modifier
    )
}

@Composable
fun BeatSelectionContent(
    state: BeatSelectionState,
    tapTempoState: TapTempoState,
    onBpmSelected: (Int) -> Unit,
    onDecreaseBpm: () -> Unit,
    onIncreaseBpm: () -> Unit,
    onPlayToggle: () -> Unit,
    onTapTempo: () -> Unit,
    onTap: () -> Unit,
    onApplyTappedBpm: () -> Unit,
    onCancelTapTempo: () -> Unit,
    onNavigateToWorkLog: () -> Unit,
    onNavigateToSettings: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showMenu by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "First Class Metronome",
                style = MaterialTheme.typography.headlineMedium,
                textAlign = TextAlign.Center,
                modifier = Modifier.weight(1f)
            )
            Box {
                IconButton(
                    onClick = { showMenu = true }
                ) {
                    Icon(
                        imageVector = Icons.Default.MoreVert,
                        contentDescription = "Open menu"
                    )
                }
                DropdownMenu(
                    expanded = showMenu,
                    onDismissRequest = { showMenu = false }
                ) {
                    DropdownMenuItem(
                        text = { Text("Work log") },
                        onClick = {
                            showMenu = false
                            onNavigateToWorkLog()
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("Settings") },
                        onClick = {
                            showMenu = false
                            onNavigateToSettings()
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        BpmGrid(
            bpmValues = state.availableBpmValues,
            selectedBpm = state.selectedBpm,
            onBpmSelected = onBpmSelected
        )

        Spacer(modifier = Modifier.weight(1f))

        HorizontalDivider(
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        ButtonBar(
            currentBpm = state.selectedBpm,
            isOnGrid = state.isOnGrid,
            isPlaying = state.isPlaying,
            canDecreaseBpm = state.canDecreaseBpm,
            canIncreaseBpm = state.canIncreaseBpm,
            bpmIncrement = state.bpmIncrement,
            isHapticEnabled = state.isHapticEnabled,
            onDecreaseBpm = onDecreaseBpm,
            onIncreaseBpm = onIncreaseBpm,
            onPlayToggle = onPlayToggle,
            onTapTempo = onTapTempo
        )
    }

    if (tapTempoState.isVisible) {
        TapTempoOverlay(
            state = tapTempoState,
            onTap = onTap,
            onApply = onApplyTappedBpm,
            onCancel = onCancelTapTempo
        )
    }
}

@Composable
private fun KeepScreenOn(enabled: Boolean) {
    val context = LocalContext.current
    val activity = context as? Activity ?: return

    DisposableEffect(enabled) {
        if (enabled) {
            activity.window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        } else {
            activity.window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        }
        onDispose {
            activity.window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        }
    }
}
