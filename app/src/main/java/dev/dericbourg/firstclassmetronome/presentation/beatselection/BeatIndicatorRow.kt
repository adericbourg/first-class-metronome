package dev.dericbourg.firstclassmetronome.presentation.beatselection

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import dev.dericbourg.firstclassmetronome.domain.model.BeatOutput

private val SQUARE_SIZE = 40.dp
private val SQUARE_SPACING = 8.dp
private val SQUARE_SHAPE = RoundedCornerShape(8.dp)
private val CURRENT_BORDER_WIDTH = 3.dp
private val IDLE_BORDER_WIDTH = 1.dp

/**
 * Row of squares showing the measure: one per beat, colored and glyphed by its output, with the
 * current beat outlined. Color is never the sole signal — a glyph and a content description carry
 * the same meaning for accessibility (WCAG 2.2).
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun BeatIndicatorRow(
    beatPattern: List<BeatOutput>,
    currentBeat: Int,
    modifier: Modifier = Modifier
) {
    FlowRow(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(SQUARE_SPACING, Alignment.CenterHorizontally),
        verticalArrangement = Arrangement.spacedBy(SQUARE_SPACING)
    ) {
        beatPattern.forEachIndexed { index, output ->
            BeatSquare(
                beatNumber = index + 1,
                output = output,
                isCurrent = index == currentBeat
            )
        }
    }
}

@Composable
private fun BeatSquare(
    beatNumber: Int,
    output: BeatOutput,
    isCurrent: Boolean,
    modifier: Modifier = Modifier
) {
    val borderColor = if (isCurrent) {
        MaterialTheme.colorScheme.onSurface
    } else {
        MaterialTheme.colorScheme.outline
    }
    val borderWidth = if (isCurrent) CURRENT_BORDER_WIDTH else IDLE_BORDER_WIDTH

    val label = beatOutputLabel(output)
    val description = if (isCurrent) {
        "Beat $beatNumber: $label, current"
    } else {
        "Beat $beatNumber: $label"
    }

    Box(
        modifier = modifier
            .size(SQUARE_SIZE)
            .clip(SQUARE_SHAPE)
            .background(beatOutputColor(output))
            .border(BorderStroke(borderWidth, borderColor), SQUARE_SHAPE)
            .semantics { contentDescription = description },
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = beatOutputIcon(output),
            contentDescription = null,
            tint = beatOutputContentColor(output),
            modifier = Modifier.size(18.dp)
        )
    }
}
