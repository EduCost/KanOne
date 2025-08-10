package com.educost.kanone.presentation.screens.board

import app.cash.turbine.test
import com.educost.kanone.dispatchers.DispatcherProvider
import com.educost.kanone.dispatchers.TestDispatcherProvider
import com.educost.kanone.domain.error.InsertDataError
import com.educost.kanone.domain.model.Board
import com.educost.kanone.domain.model.KanbanColumn
import com.educost.kanone.domain.usecase.ObserveCompleteBoardUseCase
import com.educost.kanone.domain.usecase.UpdateColumnUseCase
import com.educost.kanone.presentation.screens.board.components.BoardAppBarType
import com.educost.kanone.presentation.screens.board.state.ColumnEditState
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

@OptIn(ExperimentalCoroutinesApi::class)
class BoardViewModelEditColumnTest {

    private lateinit var testDispatcher: CoroutineDispatcher
    private lateinit var dispatcherProvider: DispatcherProvider
    private lateinit var viewModel: BoardViewModel

    private lateinit var observeCompleteBoardUseCase: ObserveCompleteBoardUseCase
    private lateinit var updateColumnUseCase: UpdateColumnUseCase

    @Before
    fun setUp() {
        testDispatcher = UnconfinedTestDispatcher()
        dispatcherProvider = TestDispatcherProvider(testDispatcher)
        observeCompleteBoardUseCase = mockk()
        updateColumnUseCase = mockk()
        viewModel = BoardViewModel(
            dispatcherProvider = dispatcherProvider,
            observeCompleteBoardUseCase = observeCompleteBoardUseCase,
            createColumnUseCase = mockk(),
            createCardUseCase = mockk(),
            updateColumnUseCase = updateColumnUseCase
        )
    }

    @Test
    fun `SHOULD update edit column name WHEN new name is provided`() {
        coEvery { observeCompleteBoardUseCase(any()) } returns flowOf(
            Result.Success(Board(id = 1, name = "test", emptyList()))
        )

        runTest(testDispatcher) {
            viewModel.uiState.test {
                viewModel.onIntent(BoardIntent.ObserveBoard(1))
                skipItems(3)

                viewModel.onIntent(BoardIntent.OnEditColumnNameChange("new name"))
                assertThat(awaitItem().columnEditState.newColumnName).isEqualTo("new name")

                cancelAndConsumeRemainingEvents()
            }
        }
    }

    @Test
    fun `SHOULD reset column edit state WHEN cancel column editing`() {
        coEvery { observeCompleteBoardUseCase(any()) } returns flowOf(
            Result.Success(Board(id = 1, name = "test", emptyList()))
        )

        runTest(testDispatcher) {
            viewModel.uiState.test {
                viewModel.onIntent(BoardIntent.ObserveBoard(1))
                skipItems(3)

                viewModel.onIntent(BoardIntent.OnRenameColumnClicked(columnId = 1))
                viewModel.onIntent(BoardIntent.OnEditColumnNameChange("new name"))
                skipItems(2)

                viewModel.onIntent(BoardIntent.CancelColumnRename)
                val updatedState = awaitItem()
                assertThat(updatedState.columnEditState).isEqualTo(ColumnEditState())
                assertThat(updatedState.topBarType).isEqualTo(BoardAppBarType.DEFAULT)

                cancelAndConsumeRemainingEvents()
            }
        }
    }

    @Test
    fun `SHOULD send snackbar WHEN confirm column rename with empty name`() {
        coEvery { observeCompleteBoardUseCase(any()) } returns flowOf(
            Result.Success(Board(id = 1, name = "test", emptyList()))
        )
        coEvery { updateColumnUseCase(any(), any()) } returns Result.Success(Unit)

        runTest(testDispatcher) {
            viewModel.sideEffectFlow.test {
                viewModel.onIntent(BoardIntent.ObserveBoard(1))

                // null
                viewModel.onIntent(BoardIntent.OnRenameColumnClicked(columnId = 1))
                viewModel.onIntent(BoardIntent.ConfirmColumnRename)
                assertThat(awaitItem()).isInstanceOf(BoardSideEffect.ShowSnackBar::class.java)

                // empty
                viewModel.onIntent(BoardIntent.OnEditColumnNameChange(""))
                viewModel.onIntent(BoardIntent.ConfirmColumnRename)
                assertThat(awaitItem()).isInstanceOf(BoardSideEffect.ShowSnackBar::class.java)

                // blank
                viewModel.onIntent(BoardIntent.OnEditColumnNameChange(" "))
                viewModel.onIntent(BoardIntent.ConfirmColumnRename)
                assertThat(awaitItem()).isInstanceOf(BoardSideEffect.ShowSnackBar::class.java)

                cancelAndConsumeRemainingEvents()
                coVerify(exactly = 0) { updateColumnUseCase(any(), any()) }
            }
        }
    }

    @Test
    fun `SHOULD send snackbar WHEN confirm column rename with empty columnId`() {
        coEvery { observeCompleteBoardUseCase(any()) } returns flowOf(
            Result.Success(Board(id = 1, name = "test", emptyList()))
        )
        coEvery { updateColumnUseCase(any(), any()) } returns Result.Success(Unit)

        runTest(testDispatcher) {
            viewModel.sideEffectFlow.test {
                viewModel.onIntent(BoardIntent.ObserveBoard(1))

                viewModel.onIntent(BoardIntent.OnEditColumnNameChange("new name"))
                viewModel.onIntent(BoardIntent.ConfirmColumnRename)
                assertThat(awaitItem()).isInstanceOf(BoardSideEffect.ShowSnackBar::class.java)

                cancelAndConsumeRemainingEvents()
                coVerify(exactly = 0) { updateColumnUseCase(any(), any()) }
            }
        }
    }

    @Test
    fun `SHOULD send snackbar WHEN confirm column rename and can not find column with id`() {

        val columnId = 1L
        val board = Board(
            id = 1,
            name = "board test",
            columns = listOf(
                KanbanColumn(
                    id = columnId,
                    name = "column test 1",
                    position = 1,
                    color = null,
                    cards = emptyList()
                )
            )
        )

        coEvery { observeCompleteBoardUseCase(any()) } returns flowOf(
            Result.Success(board)
        )

        coEvery { updateColumnUseCase(any(), any()) } returns Result.Success(Unit)

        runTest(testDispatcher) {
            viewModel.sideEffectFlow.test {
                viewModel.onIntent(BoardIntent.ObserveBoard(1))

                viewModel.onIntent(BoardIntent.OnRenameColumnClicked(columnId + 1))
                viewModel.onIntent(BoardIntent.OnEditColumnNameChange("new name"))
                viewModel.onIntent(BoardIntent.ConfirmColumnRename)
                assertThat(awaitItem()).isInstanceOf(BoardSideEffect.ShowSnackBar::class.java)

                cancelAndConsumeRemainingEvents()
                coVerify(exactly = 0) { updateColumnUseCase(any(), any()) }
            }
        }
    }

    @Test
    fun `SHOULD send snackbar WHEN confirm column rename returns error`() {

        val columnId = 1L
        val board = Board(
            id = 1,
            name = "board test",
            columns = listOf(
                KanbanColumn(
                    id = columnId,
                    name = "column test 1",
                    position = 1,
                    color = null,
                    cards = emptyList()
                )
            )
        )

        coEvery { observeCompleteBoardUseCase(any()) } returns flowOf(
            Result.Success(board)
        )
        coEvery { updateColumnUseCase(any(), any()) } returns
                Result.Error(InsertDataError.UNKNOWN)

        runTest(testDispatcher) {
            viewModel.sideEffectFlow.test {
                viewModel.onIntent(BoardIntent.ObserveBoard(1))

                viewModel.onIntent(BoardIntent.OnRenameColumnClicked(columnId))
                viewModel.onIntent(BoardIntent.OnEditColumnNameChange("new name"))
                viewModel.onIntent(BoardIntent.ConfirmColumnRename)
                assertThat(awaitItem()).isInstanceOf(BoardSideEffect.ShowSnackBar::class.java)

                cancelAndConsumeRemainingEvents()
                coVerify(exactly = 1) { updateColumnUseCase(any(), any()) }
            }
        }
    }

    @Test
    fun `SHOULD call update column use case WHEN confirm column rename with necessary data`() {

        val boardId = 1L
        val columnId = 1L
        val newName = "new name"
        val column = KanbanColumn(
            id = columnId,
            name = "column test 1",
            position = 1,
            color = null,
            cards = emptyList()
        )
        val expectedColumn = column.copy(name = newName)
        val board = Board(
            id = boardId,
            name = "board test",
            columns = listOf(column)
        )

        coEvery { observeCompleteBoardUseCase(boardId) } returns flowOf(
            Result.Success(board)
        )
        coEvery { updateColumnUseCase(expectedColumn, boardId) } returns Result.Success(Unit)

        runTest(testDispatcher) {
            viewModel.sideEffectFlow.test {
                viewModel.onIntent(BoardIntent.ObserveBoard(boardId))

                viewModel.onIntent(BoardIntent.OnRenameColumnClicked(columnId))
                viewModel.onIntent(BoardIntent.OnEditColumnNameChange(newName))
                viewModel.onIntent(BoardIntent.ConfirmColumnRename)

                cancelAndConsumeRemainingEvents()
                coVerify(exactly = 1) { updateColumnUseCase(expectedColumn, boardId) }
            }
        }
    }

    @Test
    fun `SHOULD reset column edit state WHEN confirm column rename is called`() {

        val boardId = 1L
        val columnId = 1L
        val newName = "new name"
        val column = KanbanColumn(
            id = columnId,
            name = "column test 1",
            position = 1,
            color = null,
            cards = emptyList()
        )
        val expectedColumn = column.copy(name = newName)
        val board = Board(
            id = boardId,
            name = "board test",
            columns = listOf(column)
        )

        coEvery { observeCompleteBoardUseCase(boardId) } returns flowOf(
            Result.Success(board)
        )
        coEvery { updateColumnUseCase(expectedColumn, boardId) } returns Result.Success(Unit)

        runTest(testDispatcher) {
            viewModel.uiState.test {
                viewModel.onIntent(BoardIntent.ObserveBoard(boardId))

                viewModel.onIntent(BoardIntent.OnRenameColumnClicked(columnId))
                viewModel.onIntent(BoardIntent.OnEditColumnNameChange(newName))
                viewModel.onIntent(BoardIntent.ConfirmColumnRename)

                skipItems(5)
                val updatedState = awaitItem()
                assertThat(updatedState.columnEditState).isEqualTo(ColumnEditState())
                assertThat(updatedState.topBarType).isEqualTo(BoardAppBarType.DEFAULT)

                cancelAndConsumeRemainingEvents()
                coVerify { updateColumnUseCase(expectedColumn, boardId) }
            }
        }
    }

}