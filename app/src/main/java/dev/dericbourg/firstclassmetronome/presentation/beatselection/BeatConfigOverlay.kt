package dev.dericbourg.firstclassmetronome.presentation.beatselection

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import dev.dericbourg.firstclassmetronome.domain.model.BeatOutput

private val MIN_TOUCH_TARGET = 48.dp

@Composable
fun BeatConfigOverlay(
    beatPattern: List<BeatOutput>,
    onSetBeatCount: (Int) -> Unit,
    onSetBeatOutput: (Int, BeatOutput) -> Unit,
    onClose: () -> Unit,
    modifier: Modifier = Modifier
) {
    BackHandler(onBack = onClose)

    Dialog(
        onDismissRequest = onClose,
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
            BeatConfigContent(
                beatPattern = beatPattern,
                onSetBeatCount = onSetBeatCount,
                onSetBeatOutput = onSetBeatOutput,
                onClose = onClose
            )
        }
    }
}

@Composable
private fun BeatConfigContent(
    beatPattern: List<BeatOutput>,
    onSetBeatCount: (Int) -> Unit,
    onSetBeatOutput: (Int, BeatOutput) -> Unit,
    onClose: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Beats per measure",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onSurface
        )

        Spacer(modifier = Modifier.height(24.dp))

        BeatCountStepper(
            count = beatPattern.size,
            onSetBeatCount = onSetBeatCount
        )

        Spacer(modifier = Modifier.height(24.dp))

        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            beatPattern.forEachIndexed { index, output ->
                BeatOutputRow(
                    beatNumber = index + 1,
                    output = output,
                    onOutputSelected = { onSetBeatOutput(index, it) }
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = onClose,
            modifier = Modifier
                .fillMaxWidth()
                .sizeIn(minHeight = MIN_TOUCH_TARGET)
                .semantics { contentDescription = "Done configuring beats" }
        ) {
            Text("Done")
        }
    }
}

@Composable
private fun BeatCountStepper(
    count: Int,
    onSetBeatCount: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val canDecrease = count > BeatSelectionState.MIN_BEATS
    val canIncrease = count < BeatSelectionState.MAX_BEATS

    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        OutlinedButton(
            onClick = { onSetBeatCount(count - 1) },
            enabled = canDecrease,
            modifier = Modifier
                .sizeIn(minWidth = MIN_TOUCH_TARGET, minHeight = MIN_TOUCH_TARGET)
                .semantics { contentDescription = "Remove a beat" }
        ) {
            Icon(imageVector = Icons.Default.Remove, contentDescription = null)
        }

        Text(
            text = count.toString(),
            style = MaterialTheme.typography.headlineSmall,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .width(48.dp)
                .semantics { contentDescription = "$count beats per measure" }
        )

        OutlinedButton(
            onClick = { onSetBeatCount(count + 1) },
            enabled = canIncrease,
            modifier = Modifier
                .sizeIn(minWidth = MIN_TOUCH_TARGET, minHeight = MIN_TOUCH_TARGET)
                .semantics { contentDescription = "Add a beat" }
        ) {
            Icon(imageVector = Icons.Default.Add, contentDescription = null)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun BeatOutputRow(
    beatNumber: Int,
    output: BeatOutput,
    onOutputSelected: (BeatOutput) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }

    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = beatNumber.toString(),
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.width(24.dp)
        )

        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = it },
            modifier = Modifier.weight(1f)
        ) {
            OutlinedTextField(
                value = beatOutputLabel(output),
                onValueChange = {},
                readOnly = true,
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
                modifier = Modifier
                    .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable)
                    .fillMaxWidth()
                    .semantics { contentDescription = "Beat $beatNumber sound: ${beatOutputLabel(output)}" }
            )
            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                BEAT_OUTPUT_OPTIONS.forEach { option ->
                    DropdownMenuItem(
                        text = { Text(beatOutputLabel(option)) },
                        onClick = {
                            onOutputSelected(option)
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}
