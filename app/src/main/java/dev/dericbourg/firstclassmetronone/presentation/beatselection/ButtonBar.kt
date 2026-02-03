package dev.dericbourg.firstclassmetronone.presentation.beatselection

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.TouchApp
import androidx.compose.material.icons.filled.Vibration
import androidx.compose.material.icons.outlined.Tune
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.dp

private val MIN_TOUCH_TARGET = 48.dp
private val BUTTON_SPACING = 8.dp

@Composable
fun ButtonBar(
    currentBpm: Int,
    isOnGrid: Boolean,
    isPlaying: Boolean,
    canDecreaseBpm: Boolean,
    canIncreaseBpm: Boolean,
    bpmIncrement: Int,
    isHapticEnabled: Boolean,
    onDecreaseBpm: () -> Unit,
    onIncreaseBpm: () -> Unit,
    onPlayToggle: () -> Unit,
    onTapTempo: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Bottom
    ) {
        AdjustmentButtons(
            bpmIncrement = bpmIncrement,
            canDecrease = canDecreaseBpm,
            canIncrease = canIncreaseBpm,
            onDecrease = onDecreaseBpm,
            onIncrease = onIncreaseBpm
        )
        ActionButtons(
            currentBpm = currentBpm,
            isOnGrid = isOnGrid,
            isPlaying = isPlaying,
            isHapticEnabled = isHapticEnabled,
            onPlayToggle = onPlayToggle,
            onTapTempo = onTapTempo
        )
    }
}

@Composable
private fun AdjustmentButtons(
    bpmIncrement: Int,
    canDecrease: Boolean,
    canIncrease: Boolean,
    onDecrease: () -> Unit,
    onIncrease: () -> Unit,
    modifier: Modifier = Modifier
) {
    val decreaseDescription = if (canDecrease) {
        "Decrease tempo by $bpmIncrement"
    } else {
        "Decrease tempo by $bpmIncrement, minimum reached"
    }
    val increaseDescription = if (canIncrease) {
        "Increase tempo by $bpmIncrement"
    } else {
        "Increase tempo by $bpmIncrement, maximum reached"
    }

    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(BUTTON_SPACING)
    ) {
        OutlinedButton(
            onClick = onDecrease,
            enabled = canDecrease,
            modifier = Modifier
                .sizeIn(minWidth = MIN_TOUCH_TARGET, minHeight = MIN_TOUCH_TARGET)
                .semantics { contentDescription = decreaseDescription }
        ) {
            Text(text = "-$bpmIncrement")
        }
        OutlinedButton(
            onClick = onIncrease,
            enabled = canIncrease,
            modifier = Modifier
                .sizeIn(minWidth = MIN_TOUCH_TARGET, minHeight = MIN_TOUCH_TARGET)
                .semantics { contentDescription = increaseDescription }
        ) {
            Text(text = "+$bpmIncrement")
        }
    }
}

@Composable
private fun ActionButtons(
    currentBpm: Int,
    isOnGrid: Boolean,
    isPlaying: Boolean,
    isHapticEnabled: Boolean,
    onPlayToggle: () -> Unit,
    onTapTempo: () -> Unit,
    modifier: Modifier = Modifier
) {
    val playButtonDescription = if (isPlaying) "Stop metronome" else "Start metronome"
    val playButtonIcon = if (isPlaying) Icons.Default.Stop else Icons.Default.PlayArrow

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.End
    ) {
        TempoDisplay(
            bpm = currentBpm,
            isOnGrid = isOnGrid,
            isHapticEnabled = isHapticEnabled,
            isPlaying = isPlaying,
            modifier = Modifier.padding(bottom = 4.dp)
        )
        Row(
            horizontalArrangement = Arrangement.spacedBy(BUTTON_SPACING),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onTapTempo,
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

@Composable
private fun TempoDisplay(
    bpm: Int,
    isOnGrid: Boolean,
    isHapticEnabled: Boolean,
    isPlaying: Boolean,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (!isOnGrid) {
            Icon(
                imageVector = Icons.Outlined.Tune,
                contentDescription = "Custom tempo",
                modifier = Modifier.size(16.dp),
                tint = MaterialTheme.colorScheme.secondary
            )
            Spacer(modifier = Modifier.width(4.dp))
        }
        Text(
            text = "$bpm BPM",
            style = MaterialTheme.typography.titleMedium,
            fontStyle = if (isOnGrid) FontStyle.Normal else FontStyle.Italic,
            color = if (isOnGrid) {
                MaterialTheme.colorScheme.onSurface
            } else {
                MaterialTheme.colorScheme.secondary
            }
        )
        if (isHapticEnabled) {
            Spacer(modifier = Modifier.width(6.dp))
            HapticIndicator(isPlaying = isPlaying)
        }
    }
}

@Composable
private fun HapticIndicator(
    isPlaying: Boolean,
    modifier: Modifier = Modifier
) {
    val backgroundColor = if (isPlaying) {
        MaterialTheme.colorScheme.secondaryContainer
    } else {
        MaterialTheme.colorScheme.surfaceVariant
    }
    val iconColor = if (isPlaying) {
        MaterialTheme.colorScheme.onSecondaryContainer
    } else {
        MaterialTheme.colorScheme.onSurfaceVariant
    }

    Box(
        modifier = modifier
            .clip(CircleShape)
            .background(backgroundColor)
            .padding(4.dp)
            .semantics { contentDescription = "Haptic feedback enabled" },
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.Default.Vibration,
            contentDescription = null,
            modifier = Modifier.size(12.dp),
            tint = iconColor
        )
    }
}
