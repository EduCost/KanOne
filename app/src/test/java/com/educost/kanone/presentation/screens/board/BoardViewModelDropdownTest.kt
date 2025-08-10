package com.educost.kanone.presentation.screens.board

import app.cash.turbine.test
import com.educost.kanone.dispatchers.DispatcherProvider
import com.educost.kanone.dispatchers.TestDispatcherProvider
import com.educost.kanone.domain.model.Board
import com.educost.kanone.domain.usecase.ObserveCompleteBoardUseCase
import com.educost.kanone.presentation.screens.board.components.BoardAppBarType
import com.educost.kanone.utils.Result
import com.google.common.truth.Truth.assertThat
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

@Suppress("UnusedFlow")
@OptIn(ExperimentalCoroutinesApi::class)
class BoardViewModelDropdownTest {

    private lateinit var testDispatcher: CoroutineDispatcher
    private lateinit var dispatcherProvider: DispatcherProvider
    private lateinit var viewModel: BoardViewModel

    private lateinit var observeCompleteBoardUseCase: ObserveCompleteBoardUseCase

    @Before
    fun setUp() {
        testDispatcher = UnconfinedTestDispatcher()
        dispatcherProvider = TestDispatcherProvider(testDispatcher)
        observeCompleteBoardUseCase = mockk()
        viewModel = BoardViewModel(
            dispatcherProvider = dispatcherProvider,
            observeCompleteBoardUseCase = observeCompleteBoardUseCase,
            createColumnUseCase = mockk(),
            createCardUseCase = mockk(),
            updateColumnUseCase = mockk()
        )
    }

    @Test
    fun `SHOULD update UI state WHEN open dropdown menu`() {
        val boardId = 1L
        val columnId = 1L

        coEvery { observeCompleteBoardUseCase(boardId) } returns flowOf(
            Result.Success(Board(id = boardId, name = "test", emptyList()))
        )

        runTest(testDispatcher) {
            viewModel.uiState.test {
                viewModel.onIntent(BoardIntent.ObserveBoard(boardId))
                skipItems(3)

                viewModel.onIntent(BoardIntent.OpenColumnDropdownMenu(columnId))
                val updatedState = awaitItem()
                assertThat(updatedState.activeDropdownColumnId).isEqualTo(columnId)
            }
            coVerify { observeCompleteBoardUseCase(boardId) }
        }
    }

    @Test
    fun `SHOULD update UI state WHEN close dropdown menu`() {
        val boardId = 1L
        val columnId = 1L

        coEvery { observeCompleteBoardUseCase(boardId) } returns flowOf(
            Result.Success(Board(id = boardId, name = "test", emptyList()))
        )

        runTest(testDispatcher) {
            viewModel.uiState.test {
                viewModel.onIntent(BoardIntent.ObserveBoard(boardId))
                skipItems(3)

                viewModel.onIntent(BoardIntent.OpenColumnDropdownMenu(columnId))
                skipItems(1)

                viewModel.onIntent(BoardIntent.CloseColumnDropdownMenu)
                val updatedState = awaitItem()
                assertThat(updatedState.activeDropdownColumnId).isEqualTo(null)
            }
            coVerify { observeCompleteBoardUseCase(boardId) }
        }
    }

    @Test
    fun `SHOULD update UI state WHEN rename column is called`() {
        val boardId = 1L
        val columnId = 1L

        coEvery { observeCompleteBoardUseCase(boardId) } returns flowOf(
            Result.Success(Board(id = boardId, name = "test", emptyList()))
        )

        runTest(testDispatcher) {
            viewModel.uiState.test {
                viewModel.onIntent(BoardIntent.ObserveBoard(boardId))
                skipItems(3)

                viewModel.onIntent(BoardIntent.OnRenameColumnClicked(columnId))
                val updatedState = awaitItem()
                assertThat(updatedState.topBarType).isEqualTo(BoardAppBarType.RENAME_COLUMN)
                assertThat(updatedState.columnEditState.editingColumnId).isEqualTo(columnId)
            }
            coVerify { observeCompleteBoardUseCase(boardId) }
        }
    }
}