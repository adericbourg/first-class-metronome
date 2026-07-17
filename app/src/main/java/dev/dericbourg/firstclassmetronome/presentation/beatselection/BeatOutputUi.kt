package dev.dericbourg.firstclassmetronome.presentation.beatselection

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Vibration
import androidx.compose.material.icons.outlined.Circle
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import dev.dericbourg.firstclassmetronome.domain.model.BeatOutput
import dev.dericbourg.firstclassmetronome.domain.model.ClickSound

/** Output options offered for each beat, in dropdown order. */
val BEAT_OUTPUT_OPTIONS: List<BeatOutput> = listOf(
    BeatOutput.NoSound,
    BeatOutput.HapticOnly,
    BeatOutput.Sound(ClickSound.CLICK)
)

/** Human-readable label for a beat output. */
fun beatOutputLabel(output: BeatOutput): String = when (output) {
    BeatOutput.NoSound -> "No sound"
    BeatOutput.HapticOnly -> "Haptic feedback only"
    is BeatOutput.Sound -> when (output.sound) {
        ClickSound.CLICK -> "Click"
    }
}

/**
 * A distinct glyph per output so the indicator row conveys meaning without relying on color
 * alone (WCAG 2.2). Filled note = click, vibration = haptic, hollow circle = silent.
 */
fun beatOutputIcon(output: BeatOutput): ImageVector = when (output) {
    BeatOutput.NoSound -> Icons.Outlined.Circle
    BeatOutput.HapticOnly -> Icons.Filled.Vibration
    is BeatOutput.Sound -> Icons.Filled.MusicNote
}

/** Fill color per output, drawn from theme roles so it adapts to light/dark and dynamic color. */
@Composable
fun beatOutputColor(output: BeatOutput): Color = when (output) {
    BeatOutput.NoSound -> MaterialTheme.colorScheme.surfaceVariant
    BeatOutput.HapticOnly -> MaterialTheme.colorScheme.tertiaryContainer
    is BeatOutput.Sound -> MaterialTheme.colorScheme.primaryContainer
}

/** Foreground color matching [beatOutputColor]. */
@Composable
fun beatOutputContentColor(output: BeatOutput): Color = when (output) {
    BeatOutput.NoSound -> MaterialTheme.colorScheme.onSurfaceVariant
    BeatOutput.HapticOnly -> MaterialTheme.colorScheme.onTertiaryContainer
    is BeatOutput.Sound -> MaterialTheme.colorScheme.onPrimaryContainer
}
