package dev.dericbourg.firstclassmetronome.presentation.worklog

import dev.dericbourg.firstclassmetronome.data.repository.PracticeRepository
import dev.dericbourg.firstclassmetronome.domain.StatsComputer
import dev.dericbourg.firstclassmetronome.domain.model.PracticeSession
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class WorkLogViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var repository: PracticeRepository
    private lateinit var viewModel: WorkLogViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        repository = mockk(relaxed = true)
        every { repository.getAllSessions() } returns flowOf(emptyList())
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun createViewModel(): WorkLogViewModel {
        return WorkLogViewModel(repository, StatsComputer())
    }

    @Test
    fun initialState_isEmpty() = runTest {
        viewModel = createViewModel()
        advanceUntilIdle()

        assertTrue(viewModel.state.value.isEmpty)
    }

    @Test
    fun initialState_hasHiddenClearDialog() = runTest {
        viewModel = createViewModel()
        advanceUntilIdle()

        assertEquals(ClearDataDialogState.Hidden, viewModel.state.value.clearDialogState)
    }

    @Test
    fun showClearDialog_showsFirstConfirmation() = runTest {
        viewModel = createViewModel()
        advanceUntilIdle()

        viewModel.showClearDialog()

        assertEquals(ClearDataDialogState.FirstConfirmation, viewModel.state.value.clearDialogState)
    }

    @Test
    fun confirmFirstClear_showsSecondConfirmation() = runTest {
        val sessions = List(5) { i ->
            PracticeSession(startTime = i * 100_000L, endTime = i * 100_000L + 36_000L, durationMs = 36_000L)
        }
        every { repository.getAllSessions() } returns flowOf(sessions)
        viewModel = createViewModel()
        advanceUntilIdle()
        viewModel.showClearDialog()

        viewModel.confirmFirstClear()

        val dialogState = viewModel.state.value.clearDialogState
        assertTrue(dialogState is ClearDataDialogState.SecondConfirmation)
        assertEquals(5, (dialogState as ClearDataDialogState.SecondConfirmation).sessionCount)
        assertEquals(180_000L, dialogState.totalDurationMs)
    }

    @Test
    fun confirmSecondClear_clearsDataAndHidesDialog() = runTest {
        viewModel = createViewModel()
        advanceUntilIdle()
        viewModel.showClearDialog()
        viewModel.confirmFirstClear()

        viewModel.confirmSecondClear()
        advanceUntilIdle()

        coVerify { repository.clearAllData() }
        assertEquals(ClearDataDialogState.Hidden, viewModel.state.value.clearDialogState)
    }

    @Test
    fun dismissClearDialog_hidesDialog() = runTest {
        viewModel = createViewModel()
        advanceUntilIdle()
        viewModel.showClearDialog()

        viewModel.dismissClearDialog()

        assertEquals(ClearDataDialogState.Hidden, viewModel.state.value.clearDialogState)
    }

    @Test
    fun dismissClearDialog_fromSecondConfirmation_hidesDialog() = runTest {
        viewModel = createViewModel()
        advanceUntilIdle()
        viewModel.showClearDialog()
        viewModel.confirmFirstClear()

        viewModel.dismissClearDialog()

        assertEquals(ClearDataDialogState.Hidden, viewModel.state.value.clearDialogState)
    }

    @Test
    fun collectsSessionsAndStats_fromRepository() = runTest {
        val sessions = listOf(
            PracticeSession(startTime = 1000L, endTime = 2000L, durationMs = 1000L)
        )
        every { repository.getAllSessions() } returns flowOf(sessions)
        viewModel = createViewModel()
        advanceUntilIdle()

        assertEquals(sessions, viewModel.state.value.sessions)
        assertFalse(viewModel.state.value.isEmpty)
        assertEquals(1, viewModel.state.value.stats.allTime.sessionCount)
        assertEquals(1000L, viewModel.state.value.stats.allTime.totalDurationMs)
    }
}
