package dev.dericbourg.firstclassmetronome.presentation.worklog

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import dev.dericbourg.firstclassmetronome.domain.model.PracticeStats

@Composable
fun WorkLogScreen(
    onNavigateBack: () -> Unit,
    viewModel: WorkLogViewModel = hiltViewModel(),
    modifier: Modifier = Modifier
) {
    val state by viewModel.state.collectAsState()

    WorkLogContent(
        state = state,
        onNavigateBack = onNavigateBack,
        onClearClick = viewModel::showClearDialog,
        onFirstConfirm = viewModel::confirmFirstClear,
        onSecondConfirm = viewModel::confirmSecondClear,
        onDismissDialog = viewModel::dismissClearDialog,
        modifier = modifier
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorkLogContent(
    state: WorkLogState,
    onNavigateBack: () -> Unit,
    onClearClick: () -> Unit,
    onFirstConfirm: () -> Unit,
    onSecondConfirm: () -> Unit,
    onDismissDialog: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Work Log") },
                navigationIcon = {
                    IconButton(
                        onClick = onNavigateBack
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Go back"
                        )
                    }
                },
                actions = {
                    if (!state.isEmpty) {
                        IconButton(
                            onClick = onClearClick
                        ) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Clear all data"
                            )
                        }
                    }
                }
            )
        },
        modifier = modifier
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                if (state.isEmpty) {
                    EmptyState(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                    )
                } else {
                    SessionList(
                        sessionsByDate = state.sessionsByDate,
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                    )
                }

                StatsCard(
                    stats = state.stats,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }

    ClearDataDialogs(
        dialogState = state.clearDialogState,
        onFirstConfirm = onFirstConfirm,
        onSecondConfirm = onSecondConfirm,
        onDismiss = onDismissDialog
    )
}
