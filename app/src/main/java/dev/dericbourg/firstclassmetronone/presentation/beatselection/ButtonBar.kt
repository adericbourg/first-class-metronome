package dev.dericbourg.firstclassmetronone.presentation.beatselection

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.TouchApp
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp

private val MIN_TOUCH_TARGET = 48.dp
private val BUTTON_SPACING = 8.dp

@Composable
fun ButtonBar(
    currentBpm: Int,
    isPlaying: Boolean,
    onPlayToggle: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Bottom
    ) {
        AdjustmentButtons()
        ActionButtons(
            currentBpm = currentBpm,
            isPlaying = isPlaying,
            onPlayToggle = onPlayToggle
        )
    }
}

@Composable
private fun AdjustmentButtons(modifier: Modifier = Modifier) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(BUTTON_SPACING)
    ) {
        OutlinedButton(
            onClick = { },
            modifier = Modifier
                .sizeIn(minWidth = MIN_TOUCH_TARGET, minHeight = MIN_TOUCH_TARGET)
                .semantics { contentDescription = "Decrease tempo by 5" }
        ) {
            Text(text = "-5")
        }
        OutlinedButton(
            onClick = { },
            modifier = Modifier
                .sizeIn(minWidth = MIN_TOUCH_TARGET, minHeight = MIN_TOUCH_TARGET)
                .semantics { contentDescription = "Increase tempo by 5" }
        ) {
            Text(text = "+5")
        }
    }
}

@Composable
private fun ActionButtons(
    currentBpm: Int,
    isPlaying: Boolean,
    onPlayToggle: () -> Unit,
    modifier: Modifier = Modifier
) {
    val playButtonDescription = if (isPlaying) "Stop metronome" else "Start metronome"
    val playButtonIcon = if (isPlaying) Icons.Default.Stop else Icons.Default.PlayArrow

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.End
    ) {
        Text(
            text = "$currentBpm BPM",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(bottom = 4.dp)
        )
        Row(
            horizontalArrangement = Arrangement.spacedBy(BUTTON_SPACING)
        ) {
            IconButton(
                onClick = { },
                modifier = Modifier
                    .sizeIn(minWidth = MIN_TOUCH_TARGET, minHeight = MIN_TOUCH_TARGET)
                    .semantics { contentDescription = "Tap tempo" }
            ) {
                Icon(
                    imageVector = Icons.Default.TouchApp,
                    contentDescription = null
                )
            }
            IconButton(
                onClick = onPlayToggle,
                modifier = Modifier
                    .sizeIn(minWidth = MIN_TOUCH_TARGET, minHeight = MIN_TOUCH_TARGET)
                    .semantics { contentDescription = playButtonDescription }
            ) {
                Icon(
                    imageVector = playButtonIcon,
                    contentDescription = null
                )
            }
        }
    }
}
