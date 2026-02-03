package dev.dericbourg.firstclassmetronone.presentation.beatselection

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp

private val GRID_SPACING = 8.dp
private val MIN_BUTTON_HEIGHT = 48.dp
private val SELECTED_BORDER_WIDTH = 3.dp
private val UNSELECTED_BORDER_WIDTH = 1.dp

@Composable
fun BpmGrid(
    bpmValues: List<Int>,
    selectedBpm: Int,
    onBpmSelected: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val rows = bpmValues.chunked(BeatSelectionState.GRID_COLUMNS)

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(GRID_SPACING)
    ) {
        rows.forEach { rowBpmValues ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(GRID_SPACING)
            ) {
                rowBpmValues.forEach { bpm ->
                    BpmButton(
                        bpm = bpm,
                        isSelected = bpm == selectedBpm,
                        onClick = { onBpmSelected(bpm) },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

@Composable
private fun BpmButton(
    bpm: Int,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val containerColor = if (isSelected) {
        MaterialTheme.colorScheme.primary
    } else {
        MaterialTheme.colorScheme.surface
    }

    val contentColor = if (isSelected) {
        MaterialTheme.colorScheme.onPrimary
    } else {
        MaterialTheme.colorScheme.onSurface
    }

    val borderColor = if (isSelected) {
        MaterialTheme.colorScheme.primary
    } else {
        MaterialTheme.colorScheme.outline
    }

    val borderWidth = if (isSelected) SELECTED_BORDER_WIDTH else UNSELECTED_BORDER_WIDTH

    val description = if (isSelected) {
        "$bpm BPM, selected"
    } else {
        "Select $bpm BPM"
    }

    Button(
        onClick = onClick,
        modifier = modifier
            .heightIn(min = MIN_BUTTON_HEIGHT)
            .semantics {
                contentDescription = description
                if (isSelected) {
                    liveRegion = LiveRegionMode.Polite
                }
            },
        colors = ButtonDefaults.buttonColors(
            containerColor = containerColor,
            contentColor = contentColor
        ),
        border = BorderStroke(borderWidth, borderColor)
    ) {
        Text(text = bpm.toString())
    }
}
