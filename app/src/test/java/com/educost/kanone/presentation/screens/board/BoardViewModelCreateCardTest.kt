package com.educost.kanone.presentation.screens.board

import app.cash.turbine.test
import com.educost.kanone.dispatchers.DispatcherProvider
import com.educost.kanone.dispatchers.TestDispatcherProvider
import com.educost.kanone.domain.error.InsertDataError
import com.educost.kanone.domain.model.Board
import com.educost.kanone.domain.model.KanbanColumn
import com.educost.kanone.domain.usecase.CreateCardUseCase
import com.educost.kanone.domain.usecase.ObserveCompleteBoardUseCase
import com.educost.kanone.presentation.screens.board.components.BoardAppBarType
import com.educost.kanone.presentation.screens.board.state.CardCreationState
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
class BoardViewModelCreateCardTest {

    private lateinit var testDispatcher: CoroutineDispatcher
    private lateinit var dispatcherProvider: DispatcherProvider
    private lateinit var viewModel: BoardViewModel

    private lateinit var observeCompleteBoardUseCase: ObserveCompleteBoardUseCase
    private lateinit var createCardUseCase: CreateCardUseCase

    @Before
    fun setUp() {
        testDispatcher = UnconfinedTestDispatcher()
        dispatcherProvider = TestDispatcherProvider(testDispatcher)
        observeCompleteBoardUseCase = mockk()
        createCardUseCase = mockk()
        viewModel = BoardViewModel(
            dispatcherProvider = dispatcherProvider,
            observeCompleteBoardUseCase = observeCompleteBoardUseCase,
            createColumnUseCase = mockk(),
            createCardUseCase = createCardUseCase,
            updateColumnUseCase = mockk(),
            deleteColumnUseCase = mockk(),
            restoreColumnUseCase = mockk(),
            persistBoardPositionsUseCase = mockk(),
            reorderCardsUseCase = mockk()
        )
    }

    @Test
    fun `SHOULD update state WHEN start creating card`() {

        coEvery { observeCompleteBoardUseCase(any()) } returns flowOf(
            Result.Success(Board(id = 1, name = "test", emptyList()))
        )

        runTest(testDispatcher) {
            viewModel.uiState.test {
                viewModel.onIntent(BoardIntent.ObserveBoard(1))
                skipItems(3)

                viewModel.onIntent(BoardIntent.StartCreatingCard(0, true))
                val updatedState = awaitItem()
                assertThat(updatedState.topBarType).isEqualTo(BoardAppBarType.ADD_CARD)
                assertThat(updatedState.cardCreationState.columnId).isEqualTo(0)

                cancelAndConsumeRemainingEvents()
                coVerify { observeCompleteBoardUseCase(any()) }
            }
        }
    }

    @Test
    fun `SHOULD update state WHEN cancel card creation`() {

        coEvery { observeCompleteBoardUseCase(any()) } returns flowOf(
            Result.Success(Board(id = 1, name = "test", emptyList()))
        )

        runTest(testDispatcher) {
            viewModel.uiState.test {
                viewModel.onIntent(BoardIntent.ObserveBoard(1))
                skipItems(3)

                viewModel.onIntent(BoardIntent.StartCreatingCard(0, true))
                awaitItem()

                viewModel.onIntent(BoardIntent.CancelCardCreation)
                val updatedState = awaitItem()
                assertThat(updatedState.topBarType).isEqualTo(BoardAppBarType.DEFAULT)
                assertThat(updatedState.cardCreationState).isEqualTo(CardCreationState())

                cancelAndConsumeRemainingEvents()
                coVerify { observeCompleteBoardUseCase(any()) }
            }
        }
    }

    @Test
    fun `SHOULD update state WHEN new card title change`() {

        coEvery { observeCompleteBoardUseCase(any()) } returns flowOf(
            Result.Success(Board(id = 1, name = "test", emptyList()))
        )

        runTest(testDispatcher) {
            viewModel.uiState.test {
                viewModel.onIntent(BoardIntent.ObserveBoard(1))
                skipItems(3)

                viewModel.onIntent(BoardIntent.OnCardTitleChange("new title"))
                assertThat(awaitItem().cardCreationState.title).isEqualTo("new title")

                cancelAndConsumeRemainingEvents()
                coVerify { observeCompleteBoardUseCase(any()) }
            }
        }
    }

    @Test
    fun `SHOULD send snackbar WHEN create card with null title`() {

        val columnId = 1L
        val initialBoard = Board(
            id = 1,
            name = "test",
            columns = listOf(
                KanbanColumn(
                    id = columnId,
                    name = "column",
                    position = 0,
                    color = null,
                    cards = emptyList()
                )
            )
        )

        coEvery { observeCompleteBoardUseCase(any()) } returns flowOf(
            Result.Success(initialBoard)
        )

        runTest(testDispatcher) {
            viewModel.sideEffectFlow.test {
                viewModel.onIntent(BoardIntent.ObserveBoard(1))
                viewModel.onIntent(BoardIntent.StartCreatingCard(columnId, true))

                viewModel.onIntent(BoardIntent.ConfirmCardCreation)
                assertThat(awaitItem()).isInstanceOf(BoardSideEffect.ShowSnackBar::class.java)

                cancelAndConsumeRemainingEvents()
                coVerify { observeCompleteBoardUseCase(any()) }
            }
        }
    }

    @Test
    fun `SHOULD send snackbar WHEN create card with null columnId`() {

        val columnId = 1L
        val initialBoard = Board(
            id = 1,
            name = "test",
            columns = listOf(
                KanbanColumn(
                    id = columnId,
                    name = "column",
                    position = 0,
                    color = null,
                    cards = emptyList()
                )
            )
        )

        coEvery { observeCompleteBoardUseCase(any()) } returns flowOf(
            Result.Success(initialBoard)
        )

        runTest(testDispatcher) {
            viewModel.sideEffectFlow.test {
                viewModel.onIntent(BoardIntent.ObserveBoard(1))
                // columnId not specified because start create card is not called
                viewModel.onIntent(BoardIntent.OnCardTitleChange("new title"))

                viewModel.onIntent(BoardIntent.ConfirmCardCreation)
                assertThat(awaitItem()).isInstanceOf(BoardSideEffect.ShowSnackBar::class.java)

                cancelAndConsumeRemainingEvents()
                coVerify { observeCompleteBoardUseCase(any()) }
            }
        }
    }

    @Test
    fun `SHOULD send snackbar WHEN did not find column while creating card`() {

        val columnId = 1L
        val initialBoard = Board(
            id = 1,
            name = "test",
            columns = listOf(
                KanbanColumn(
                    id = columnId,
                    name = "column",
                    position = 0,
                    color = null,
                    cards = emptyList()
                )
            )
        )

        coEvery { observeCompleteBoardUseCase(any()) } returns flowOf(
            Result.Success(initialBoard)
        )

        runTest(testDispatcher) {
            viewModel.sideEffectFlow.test {
                viewModel.onIntent(BoardIntent.ObserveBoard(1))
                viewModel.onIntent(BoardIntent.OnCardTitleChange("new title"))
                viewModel.onIntent(BoardIntent.StartCreatingCard(columnId + 1, true)) // Should not find any column

                viewModel.onIntent(BoardIntent.ConfirmCardCreation)
                assertThat(awaitItem()).isInstanceOf(BoardSideEffect.ShowSnackBar::class.java)

                cancelAndConsumeRemainingEvents()
                coVerify { observeCompleteBoardUseCase(any()) }
            }
        }
    }

    @Test
    fun `SHOULD call card repository WHEN create card`() {

        val columnId = 1L
        val initialBoard = Board(
            id = 1,
            name = "test",
            columns = listOf(
                KanbanColumn(
                    id = columnId,
                    name = "column",
                    position = 0,
                    color = null,
                    cards = emptyList()
                )
            )
        )

        coEvery { observeCompleteBoardUseCase(any()) } returns flowOf(
            Result.Success(initialBoard)
        )

        coEvery { createCardUseCase(any(), columnId) } returns Result.Success(1)

        runTest(testDispatcher) {

            viewModel.onIntent(BoardIntent.ObserveBoard(1))
            viewModel.onIntent(BoardIntent.StartCreatingCard(columnId, true))
            viewModel.onIntent(BoardIntent.OnCardTitleChange("new title"))
            viewModel.onIntent(BoardIntent.ConfirmCardCreation)

            coVerify {
                observeCompleteBoardUseCase(any())
                createCardUseCase(any(), columnId)
            }
        }
    }

    @Test
    fun `SHOULD send snackbar WHEN create card with result error`() {

        val columnId = 1L
        val initialBoard = Board(
            id = 1,
            name = "test",
            columns = listOf(
                KanbanColumn(
                    id = columnId,
                    name = "column",
                    position = 0,
                    color = null,
                    cards = emptyList()
                )
            )
        )

        coEvery { observeCompleteBoardUseCase(any()) } returns flowOf(
            Result.Success(initialBoard)
        )
        coEvery { createCardUseCase(any(), columnId) } returns Result.Error(
            InsertDataError.UNKNOWN
        )

        runTest(testDispatcher) {
            viewModel.sideEffectFlow.test {
                viewModel.onIntent(BoardIntent.ObserveBoard(1))
                viewModel.onIntent(BoardIntent.StartCreatingCard(columnId, true))
                viewModel.onIntent(BoardIntent.OnCardTitleChange("new title"))

                viewModel.onIntent(BoardIntent.ConfirmCardCreation)
                assertThat(awaitItem()).isInstanceOf(BoardSideEffect.ShowSnackBar::class.java)

                cancelAndConsumeRemainingEvents()
                coVerify {
                    observeCompleteBoardUseCase(any())
                    createCardUseCase(any(), columnId)
                }
            }
        }
    }

}