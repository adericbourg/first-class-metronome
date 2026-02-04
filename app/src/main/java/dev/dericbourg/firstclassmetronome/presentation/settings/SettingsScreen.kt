package dev.dericbourg.firstclassmetronome.presentation.settings

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import dev.dericbourg.firstclassmetronome.data.settings.AppSettings
import dev.dericbourg.firstclassmetronome.data.settings.HapticStrength

private val MIN_TOUCH_TARGET = 48.dp

@Composable
fun SettingsScreen(
    onNavigateBack: () -> Unit,
    viewModel: SettingsViewModel = hiltViewModel(),
    modifier: Modifier = Modifier
) {
    val state by viewModel.state.collectAsState()

    SettingsContent(
        state = state,
        onNavigateBack = onNavigateBack,
        onBpmIncrementChange = viewModel::setBpmIncrement,
        onHapticFeedbackChange = viewModel::setHapticFeedback,
        onHapticStrengthChange = viewModel::setHapticStrength,
        onResetClick = viewModel::showResetDialog,
        onConfirmReset = viewModel::confirmReset,
        onDismissDialog = viewModel::dismissDialog,
        modifier = modifier
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsContent(
    state: SettingsState,
    onNavigateBack: () -> Unit,
    onBpmIncrementChange: (Int) -> Unit,
    onHapticFeedbackChange: (Boolean) -> Unit,
    onHapticStrengthChange: (HapticStrength) -> Unit,
    onResetClick: () -> Unit,
    onConfirmReset: () -> Unit,
    onDismissDialog: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Go back"
                        )
                    }
                }
            )
        },
        modifier = modifier
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            BpmIncrementSetting(
                value = state.bpmIncrement,
                onValueChange = onBpmIncrementChange
            )

            if (state.isHapticSupported) {
                Spacer(modifier = Modifier.height(16.dp))
                HorizontalDivider()
                Spacer(modifier = Modifier.height(16.dp))

                HapticFeedbackSetting(
                    enabled = state.hapticFeedbackEnabled,
                    onEnabledChange = onHapticFeedbackChange,
                    strength = state.hapticStrength,
                    onStrengthChange = onHapticStrengthChange,
                    showStrengthSelector = state.hasAmplitudeControl
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            ResetButton(onClick = onResetClick)
        }
    }

    if (state.showResetDialog) {
        ResetConfirmationDialog(
            onConfirm = onConfirmReset,
            onDismiss = onDismissDialog
        )
    }
}

@Composable
private fun BpmIncrementSetting(
    value: Int,
    onValueChange: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = "BPM Increment",
            style = MaterialTheme.typography.titleMedium
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "Amount to increase or decrease tempo with the +/- buttons",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            val canDecrease = value > AppSettings.MIN_BPM_INCREMENT
            val canIncrease = value < AppSettings.MAX_BPM_INCREMENT

            OutlinedButton(
                onClick = { onValueChange(value - 1) },
                enabled = canDecrease,
                modifier = Modifier
                    .sizeIn(minWidth = MIN_TOUCH_TARGET, minHeight = MIN_TOUCH_TARGET)
                    .semantics {
                        contentDescription = if (canDecrease) {
                            "Decrease increment"
                        } else {
                            "Decrease increment, minimum reached"
                        }
                    }
            ) {
                Text("-")
            }

            Text(
                text = "$value",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier
                    .padding(horizontal = 32.dp)
                    .semantics { contentDescription = "BPM increment: $value" }
            )

            OutlinedButton(
                onClick = { onValueChange(value + 1) },
                enabled = canIncrease,
                modifier = Modifier
                    .sizeIn(minWidth = MIN_TOUCH_TARGET, minHeight = MIN_TOUCH_TARGET)
                    .semantics {
                        contentDescription = if (canIncrease) {
                            "Increase increment"
                        } else {
                            "Increase increment, maximum reached"
                        }
                    }
            ) {
                Text("+")
            }
        }
    }
}

@Composable
private fun HapticFeedbackSetting(
    enabled: Boolean,
    onEnabledChange: (Boolean) -> Unit,
    strength: HapticStrength,
    onStrengthChange: (HapticStrength) -> Unit,
    showStrengthSelector: Boolean,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Haptic Feedback",
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Vibrate on each beat",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Switch(
                checked = enabled,
                onCheckedChange = onEnabledChange,
                modifier = Modifier.semantics {
                    contentDescription = if (enabled) {
                        "Haptic feedback enabled, tap to disable"
                    } else {
                        "Haptic feedback disabled, tap to enable"
                    }
                }
            )
        }

        AnimatedVisibility(
            visible = enabled && showStrengthSelector,
            enter = expandVertically() + fadeIn(),
            exit = shrinkVertically() + fadeOut()
        ) {
            HapticStrengthSelector(
                selectedStrength = strength,
                onStrengthChange = onStrengthChange,
                modifier = Modifier.padding(top = 16.dp)
            )
        }
    }
}

@Composable
private fun HapticStrengthSelector(
    selectedStrength: HapticStrength,
    onStrengthChange: (HapticStrength) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = "Vibration strength",
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .selectableGroup(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            HapticStrength.entries.forEach { strength ->
                val isSelected = strength == selectedStrength
                val label = strength.name.lowercase().replaceFirstChar { it.uppercase() }

                Row(
                    modifier = Modifier
                        .sizeIn(minHeight = MIN_TOUCH_TARGET)
                        .selectable(
                            selected = isSelected,
                            onClick = { onStrengthChange(strength) },
                            role = Role.RadioButton
                        )
                        .padding(horizontal = 8.dp)
                        .semantics {
                            contentDescription = "Vibration strength: $label" +
                                if (isSelected) ", selected" else ""
                        },
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = isSelected,
                        onClick = null
                    )
                    Text(
                        text = label,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(start = 4.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun ResetButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .sizeIn(minHeight = MIN_TOUCH_TARGET)
    ) {
        Text("Reset to defaults")
    }
}

@Composable
private fun ResetConfirmationDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Reset Settings") },
        text = { Text("This will reset all settings to their default values. Continue?") },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text("Reset")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
