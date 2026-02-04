package dev.dericbourg.firstclassmetronome.presentation.taptempo

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.TouchApp
import androidx.compose.material3.ripple
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties

private val MIN_TAP_ZONE_WIDTH = 200.dp
private val MIN_TAP_ZONE_HEIGHT = 150.dp
private val MIN_BUTTON_SIZE = 48.dp

@Composable
fun TapTempoOverlay(
    state: TapTempoState,
    onTap: () -> Unit,
    onApply: () -> Unit,
    onCancel: () -> Unit,
    modifier: Modifier = Modifier
) {
    BackHandler(onBack = onCancel)

    Dialog(
        onDismissRequest = onCancel,
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = true,
            usePlatformDefaultWidth = false
        )
    ) {
        Surface(
            modifier = modifier
                .fillMaxSize()
                .padding(24.dp),
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 6.dp
        ) {
            TapTempoContent(
                state = state,
                onTap = onTap,
                onApply = onApply,
                onCancel = onCancel
            )
        }
    }
}

@Composable
private fun TapTempoContent(
    state: TapTempoState,
    onTap: () -> Unit,
    onApply: () -> Unit,
    onCancel: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier.fillMaxSize()) {
        // Background finger icon
        Icon(
            imageVector = Icons.Default.TouchApp,
            contentDescription = null,
            modifier = Modifier
                .size(200.dp)
                .align(Alignment.Center)
                .alpha(0.1f),
            tint = MaterialTheme.colorScheme.primary
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Tap Tempo",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(16.dp))

            TapZone(
                onTap = onTap,
                modifier = Modifier.weight(1f)
            )

            Spacer(modifier = Modifier.height(24.dp))

            BpmDisplay(
                bpm = state.calculatedBpm,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(24.dp))

            ActionButtons(
                canApply = state.canApply,
                bpm = state.calculatedBpm,
                onApply = onApply,
                onCancel = onCancel
            )
        }
    }
}

@Composable
private fun TapZone(
    onTap: () -> Unit,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .sizeIn(minWidth = MIN_TAP_ZONE_WIDTH, minHeight = MIN_TAP_ZONE_HEIGHT)
            .clickable(
                interactionSource = interactionSource,
                indication = ripple(),
                onClick = onTap
            )
            .semantics {
                contentDescription = "Tap tempo zone. Tap repeatedly to set tempo"
            },
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Default.TouchApp,
                    contentDescription = null,
                    modifier = Modifier.size(64.dp),
                    tint = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Tap here",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
        }
    }
}

@Composable
private fun BpmDisplay(
    bpm: Int?,
    modifier: Modifier = Modifier
) {
    val displayText = bpm?.let { "$it BPM" } ?: "---"
    val description = bpm?.let { "$it beats per minute" } ?: "No tempo detected"

    Text(
        text = displayText,
        style = MaterialTheme.typography.displayMedium,
        textAlign = TextAlign.Center,
        color = MaterialTheme.colorScheme.primary,
        modifier = modifier.semantics {
            contentDescription = description
        }
    )
}

@Composable
private fun ActionButtons(
    canApply: Boolean,
    bpm: Int?,
    onApply: () -> Unit,
    onCancel: () -> Unit,
    modifier: Modifier = Modifier
) {
    val applyDescription = if (canApply && bpm != null) {
        "Apply $bpm BPM"
    } else {
        "Apply tempo"
    }

    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterHorizontally)
    ) {
        OutlinedButton(
            onClick = onCancel,
            modifier = Modifier
                .weight(1f)
                .sizeIn(minHeight = MIN_BUTTON_SIZE)
                .semantics { contentDescription = "Cancel tap tempo" }
        ) {
            Text("Cancel")
        }

        Button(
            onClick = onApply,
            enabled = canApply,
            modifier = Modifier
                .weight(1f)
                .sizeIn(minHeight = MIN_BUTTON_SIZE)
                .semantics { contentDescription = applyDescription }
        ) {
            Text("Apply")
        }
    }
}
