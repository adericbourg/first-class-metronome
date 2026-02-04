package dev.dericbourg.firstclassmetronome.presentation.worklog

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics

@Composable
fun ClearDataDialogs(
    dialogState: ClearDataDialogState,
    onFirstConfirm: () -> Unit,
    onSecondConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    when (dialogState) {
        ClearDataDialogState.Hidden -> {}

        ClearDataDialogState.FirstConfirmation -> {
            AlertDialog(
                onDismissRequest = onDismiss,
                title = { Text("Clear all practice data?") },
                text = {
                    Text("This will permanently delete all your practice history. This action cannot be undone.")
                },
                confirmButton = {
                    TextButton(
                        onClick = onFirstConfirm,
                        modifier = androidx.compose.ui.Modifier.semantics {
                            contentDescription = "Clear all data"
                        }
                    ) {
                        Text(
                            text = "Clear",
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = onDismiss,
                        modifier = androidx.compose.ui.Modifier.semantics {
                            contentDescription = "Cancel"
                        }
                    ) {
                        Text("Cancel")
                    }
                }
            )
        }

        is ClearDataDialogState.SecondConfirmation -> {
            AlertDialog(
                onDismissRequest = onDismiss,
                title = { Text("Are you sure?") },
                text = {
                    Text(
                        "You are about to delete:\n" +
                        "• ${dialogState.sessionCount} practice sessions\n" +
                        "• ${dialogState.totalDurationFormatted} of tracked practice\n\n" +
                        "This cannot be undone."
                    )
                },
                confirmButton = {
                    TextButton(
                        onClick = onSecondConfirm,
                        modifier = androidx.compose.ui.Modifier.semantics {
                            contentDescription = "Delete forever"
                        }
                    ) {
                        Text(
                            text = "Delete forever",
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = onDismiss,
                        modifier = androidx.compose.ui.Modifier.semantics {
                            contentDescription = "Cancel"
                        }
                    ) {
                        Text("Cancel")
                    }
                }
            )
        }
    }
}
