package dev.dericbourg.firstclassmetronone.presentation.beatselection

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import dev.dericbourg.firstclassmetronone.presentation.taptempo.TapTempoOverlay
import dev.dericbourg.firstclassmetronone.presentation.taptempo.TapTempoState

@Composable
fun BeatSelectionScreen(
    viewModel: BeatSelectionViewModel = hiltViewModel(),
    modifier: Modifier = Modifier
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
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "First Class Metronome",
            style = MaterialTheme.typography.headlineMedium,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        )

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
