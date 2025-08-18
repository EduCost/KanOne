package com.educost.kanone.presentation.screens.board

import app.cash.turbine.test
import com.educost.kanone.dispatchers.DispatcherProvider
import com.educost.kanone.dispatchers.TestDispatcherProvider
import com.educost.kanone.domain.error.InsertDataError
import com.educost.kanone.domain.model.Board
import com.educost.kanone.domain.model.KanbanColumn
import com.educost.kanone.domain.usecase.DeleteColumnUseCase
import com.educost.kanone.domain.usecase.ObserveCompleteBoardUseCase
import com.educost.kanone.domain.usecase.ReorderCardsUseCase
import com.educost.kanone.presentation.screens.board.utils.BoardAppBarType
import com.educost.kanone.presentation.screens.board.utils.CardOrder
import com.educost.kanone.presentation.screens.board.utils.OrderType
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
    private lateinit var deleteColumnUseCase: DeleteColumnUseCase
    private lateinit var reorderCardsUseCase: ReorderCardsUseCase

    @Before
    fun setUp() {
        testDispatcher = UnconfinedTestDispatcher()
        dispatcherProvider = TestDispatcherProvider(testDispatcher)
        observeCompleteBoardUseCase = mockk()
        deleteColumnUseCase = mockk()
        reorderCardsUseCase = mockk()

        viewModel = BoardViewModel(
            dispatcherProvider = dispatcherProvider,
            observeCompleteBoardUseCase = observeCompleteBoardUseCase,
            createColumnUseCase = mockk(),
            createCardUseCase = mockk(),
            updateColumnUseCase = mockk(),
            deleteColumnUseCase = deleteColumnUseCase,
            restoreColumnUseCase = mockk(),
            persistBoardPositionsUseCase = mockk(),
            reorderCardsUseCase = reorderCardsUseCase
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

                cancelAndConsumeRemainingEvents()
                coVerify { observeCompleteBoardUseCase(boardId) }
            }
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

                cancelAndConsumeRemainingEvents()
                coVerify { observeCompleteBoardUseCase(boardId) }
            }
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
                assertThat(updatedState.columnEditState.isRenaming).isTrue()

                cancelAndConsumeRemainingEvents()
                coVerify { observeCompleteBoardUseCase(boardId) }
            }
        }
    }

    @Test
    fun `SHOULD send snackbar WHEN did not find column on delete column`() {

        val boardId = 1L
        val columnId = 1L
        val column = KanbanColumn(
            id = columnId,
            name = "column test 1",
            position = 1,
            color = null,
            cards = emptyList()
        )
        val board = Board(
            id = boardId,
            name = "board test",
            columns = listOf(column)
        )

        coEvery { observeCompleteBoardUseCase(boardId) } returns flowOf(
            Result.Success(board)
        )

        coEvery { deleteColumnUseCase(column, boardId) } returns Result.Success(Unit)

        runTest(testDispatcher) {
            viewModel.sideEffectFlow.test {
                viewModel.onIntent(BoardIntent.ObserveBoard(boardId))
                viewModel.onIntent(BoardIntent.OnDeleteColumnClicked(columnId + 1))
                assertThat(awaitItem()).isInstanceOf(BoardSideEffect.ShowSnackBar::class.java)

                cancelAndConsumeRemainingEvents()
                coVerify(exactly = 0) { deleteColumnUseCase(column, boardId) }
            }
        }
    }

    @Test
    fun `SHOULD call delete column use case WHEN delete column is called`() {

        val boardId = 1L
        val columnId = 1L
        val column = KanbanColumn(
            id = columnId,
            name = "column test 1",
            position = 1,
            color = null,
            cards = emptyList()
        )
        val board = Board(
            id = boardId,
            name = "board test",
            columns = listOf(column)
        )

        coEvery { observeCompleteBoardUseCase(boardId) } returns flowOf(
            Result.Success(board)
        )

        coEvery { deleteColumnUseCase(column, boardId) } returns Result.Success(Unit)

        runTest(testDispatcher) {
            viewModel.onIntent(BoardIntent.ObserveBoard(boardId))
            viewModel.onIntent(BoardIntent.OnDeleteColumnClicked(columnId))

            coVerify(exactly = 1) { deleteColumnUseCase(column, boardId) }
        }
    }

    @Test
    fun `SHOULD send snackbar WHEN delete column is successful`() {

        val boardId = 1L
        val columnId = 1L
        val column = KanbanColumn(
            id = columnId,
            name = "column test 1",
            position = 1,
            color = null,
            cards = emptyList()
        )
        val board = Board(
            id = boardId,
            name = "board test",
            columns = listOf(column)
        )

        coEvery { observeCompleteBoardUseCase(boardId) } returns flowOf(
            Result.Success(board)
        )

        coEvery { deleteColumnUseCase(column, boardId) } returns Result.Success(Unit)

        runTest(testDispatcher) {
            viewModel.sideEffectFlow.test {
                viewModel.onIntent(BoardIntent.ObserveBoard(boardId))
                viewModel.onIntent(BoardIntent.OnDeleteColumnClicked(columnId))
                assertThat(awaitItem()).isInstanceOf(BoardSideEffect.ShowSnackBar::class.java)

                cancelAndConsumeRemainingEvents()
                coVerify(exactly = 1) { deleteColumnUseCase(column, boardId) }
            }
        }
    }

    @Test
    fun `SHOULD send snackbar WHEN delete column is not successful`() {

        val boardId = 1L
        val columnId = 1L
        val column = KanbanColumn(
            id = columnId,
            name = "column test 1",
            position = 1,
            color = null,
            cards = emptyList()
        )
        val board = Board(
            id = boardId,
            name = "board test",
            columns = listOf(column)
        )

        coEvery { observeCompleteBoardUseCase(boardId) } returns flowOf(
            Result.Success(board)
        )

        coEvery {
            deleteColumnUseCase(
                column,
                boardId
            )
        } returns Result.Error(InsertDataError.UNKNOWN)

        runTest(testDispatcher) {
            viewModel.sideEffectFlow.test {
                viewModel.onIntent(BoardIntent.ObserveBoard(boardId))
                viewModel.onIntent(BoardIntent.OnDeleteColumnClicked(columnId))
                assertThat(awaitItem()).isInstanceOf(BoardSideEffect.ShowSnackBar::class.java)

                cancelAndConsumeRemainingEvents()
                coVerify(exactly = 1) { deleteColumnUseCase(column, boardId) }
            }
        }
    }

    @Test
    fun `SHOULD send snackbar WHEN call reorder cards but did not found column`() {
        val column = KanbanColumn(
            id = 1,
            name = "column test 1",
            position = 1,
            cards = emptyList()
        )
        val board = Board(
            id = 1,
            name = "board test",
            columns = listOf(column)
        )

        coEvery { observeCompleteBoardUseCase(board.id) } returns flowOf(
            Result.Success(board)
        )

        coEvery {
            reorderCardsUseCase(
                column,
                OrderType.ASCENDING,
                CardOrder.NAME
            )
        } returns Result.Success(Unit)

        runTest(testDispatcher) {
            viewModel.sideEffectFlow.test {
                viewModel.onIntent(BoardIntent.ObserveBoard(board.id))
                viewModel.onIntent(
                    BoardIntent.OnOrderByClicked(
                        columnId = column.id + 1,
                        orderType = OrderType.ASCENDING,
                        cardOrder = CardOrder.NAME
                    )
                )
                assertThat(awaitItem()).isInstanceOf(BoardSideEffect.ShowSnackBar::class.java)

                cancelAndConsumeRemainingEvents()
                coVerify(exactly = 0) { reorderCardsUseCase(any(), any(), any()) }
            }
        }
    }

    @Test
    fun `SHOULD call reorder cards use case WHEN call reorder cards`() {

        val column = KanbanColumn(
            id = 1,
            name = "column test 1",
            position = 1,
            cards = emptyList()
        )
        val board = Board(
            id = 1,
            name = "board test",
            columns = listOf(column)
        )

        coEvery { observeCompleteBoardUseCase(board.id) } returns flowOf(
            Result.Success(board)
        )

        coEvery {
            reorderCardsUseCase(
                column,
                OrderType.ASCENDING,
                CardOrder.NAME
            )
        } returns Result.Success(Unit)


        runTest(testDispatcher) {
            viewModel.onIntent(BoardIntent.ObserveBoard(board.id))
            viewModel.onIntent(
                BoardIntent.OnOrderByClicked(
                    column.id,
                    OrderType.ASCENDING,
                    CardOrder.NAME
                )
            )

            coVerify(exactly = 1) {
                reorderCardsUseCase(
                    column,
                    OrderType.ASCENDING,
                    CardOrder.NAME
                )
            }
        }
    }

    @Test
    fun `SHOULD send snackbar WHEN call reorder cards and returns error`() {
        val column = KanbanColumn(
            id = 1,
            name = "column test 1",
            position = 1,
            cards = emptyList()
        )
        val board = Board(
            id = 1,
            name = "board test",
            columns = listOf(column)
        )

        coEvery { observeCompleteBoardUseCase(board.id) } returns flowOf(
            Result.Success(board)
        )

        coEvery {
            reorderCardsUseCase(
                column,
                OrderType.ASCENDING,
                CardOrder.NAME
            )
        } returns Result.Error(InsertDataError.UNKNOWN)


        runTest(testDispatcher) {
            viewModel.sideEffectFlow.test {
                viewModel.onIntent(BoardIntent.ObserveBoard(board.id))
                viewModel.onIntent(
                    BoardIntent.OnOrderByClicked(
                        column.id,
                        OrderType.ASCENDING,
                        CardOrder.NAME
                    )
                )

                assertThat(awaitItem()).isInstanceOf(BoardSideEffect.ShowSnackBar::class.java)
                cancelAndConsumeRemainingEvents()
                coVerify(exactly = 1) {
                    reorderCardsUseCase(
                        column,
                        OrderType.ASCENDING,
                        CardOrder.NAME
                    )
                }
            }
        }
    }
}