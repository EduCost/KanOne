package com.educost.kanone.presentation.screens.board

import app.cash.turbine.test
import com.educost.kanone.dispatchers.DispatcherProvider
import com.educost.kanone.dispatchers.TestDispatcherProvider
import com.educost.kanone.domain.model.Board
import com.educost.kanone.domain.usecase.CreateColumnResult
import com.educost.kanone.domain.usecase.CreateColumnUseCase
import com.educost.kanone.domain.usecase.ObserveCompleteBoardUseCase
import com.educost.kanone.presentation.screens.board.utils.BoardAppBarType
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
class BoardViewModelCreateColumnTest {

    private lateinit var testDispatcher: CoroutineDispatcher
    private lateinit var dispatcherProvider: DispatcherProvider
    private lateinit var viewModel: BoardViewModel

    private lateinit var observeCompleteBoardUseCase: ObserveCompleteBoardUseCase
    private lateinit var createColumnUseCase: CreateColumnUseCase

    @Before
    fun setUp() {
        testDispatcher = UnconfinedTestDispatcher()
        dispatcherProvider = TestDispatcherProvider(testDispatcher)
        observeCompleteBoardUseCase = mockk()
        createColumnUseCase = mockk()
        viewModel = BoardViewModel(
            dispatcherProvider = dispatcherProvider,
            observeCompleteBoardUseCase = observeCompleteBoardUseCase,
            createColumnUseCase = createColumnUseCase,
            createCardUseCase = mockk(),
            updateColumnUseCase = mockk(),
            deleteColumnUseCase = mockk(),
            restoreColumnUseCase = mockk(),
            persistBoardPositionsUseCase = mockk(),
            reorderCardsUseCase = mockk(),
            updateBoardUseCase = mockk(),
            deleteBoardUseCase = mockk()
        )
    }

    @Test
    fun `SHOULD set topBarType to ADD_COLUMN WHEN start creating new column`() {

        coEvery { observeCompleteBoardUseCase(any()) } returns flowOf(
            Result.Success(Board(id = 1, name = "test", emptyList()))
        )

        runTest(testDispatcher) {
            viewModel.uiState.test {
                viewModel.onIntent(BoardIntent.ObserveBoard(1))
                skipItems(3)

                viewModel.onIntent(BoardIntent.StartCreatingColumn)
                assertThat(awaitItem().topBarType).isEqualTo(BoardAppBarType.ADD_COLUMN)

                cancelAndConsumeRemainingEvents()
                coVerify { observeCompleteBoardUseCase(any()) }
            }
        }
    }

    @Test
    fun `SHOULD update new column name state WHEN new column name change`() {

        coEvery { observeCompleteBoardUseCase(any()) } returns flowOf(
            Result.Success(Board(id = 1, name = "test", emptyList()))
        )

        runTest(testDispatcher) {
            viewModel.uiState.test {
                viewModel.onIntent(BoardIntent.ObserveBoard(1))
                skipItems(3)

                viewModel.onIntent(BoardIntent.OnColumnNameChanged("new name"))
                assertThat(awaitItem().creatingColumnName).isEqualTo("new name")

                cancelAndConsumeRemainingEvents()
                coVerify { observeCompleteBoardUseCase(any()) }
            }
        }
    }

    @Test
    fun `SHOULD reset state WHEN cancel column creation`() {

        coEvery { observeCompleteBoardUseCase(any()) } returns flowOf(
            Result.Success(Board(id = 1, name = "test", emptyList()))
        )

        runTest(testDispatcher) {
            viewModel.uiState.test {
                viewModel.onIntent(BoardIntent.ObserveBoard(1))
                viewModel.onIntent(BoardIntent.StartCreatingColumn)
                viewModel.onIntent(BoardIntent.OnColumnNameChanged("new name"))
                skipItems(3)

                assertThat(awaitItem().topBarType).isEqualTo(BoardAppBarType.ADD_COLUMN)
                assertThat(awaitItem().creatingColumnName).isEqualTo("new name")

                viewModel.onIntent(BoardIntent.CancelColumnCreation)
                val updatedState = awaitItem()
                assertThat(updatedState.creatingColumnName).isEqualTo(null)
                assertThat(updatedState.topBarType).isEqualTo(BoardAppBarType.DEFAULT)

                cancelAndConsumeRemainingEvents()
                coVerify { observeCompleteBoardUseCase(any()) }
            }
        }
    }

    @Test
    fun `SHOULD reset state WHEN creation is successful`() {

        coEvery { observeCompleteBoardUseCase(any()) } returns flowOf(
            Result.Success(Board(id = 1, name = "test", emptyList()))
        )
        coEvery { createColumnUseCase(any(), any(), any()) } returns CreateColumnResult.SUCCESS

        runTest(testDispatcher) {
            viewModel.uiState.test {
                // set up
                viewModel.onIntent(BoardIntent.ObserveBoard(1))
                skipItems(3)
                viewModel.onIntent(BoardIntent.StartCreatingColumn)
                viewModel.onIntent(BoardIntent.OnColumnNameChanged("new name"))
                skipItems(2)


                viewModel.onIntent(BoardIntent.ConfirmColumnCreation)
                val updatedState = awaitItem()
                assertThat(updatedState.creatingColumnName).isEqualTo(null)
                assertThat(updatedState.topBarType).isEqualTo(BoardAppBarType.DEFAULT)

                cancelAndConsumeRemainingEvents()
                coVerify {
                    createColumnUseCase(any(), any(), any())
                    observeCompleteBoardUseCase(any())
                }
            }
        }
    }

    @Test
    fun `SHOULD send snackbar WHEN creation is failure`() {

        coEvery { observeCompleteBoardUseCase(any()) } returns flowOf(
            Result.Success(Board(id = 1, name = "test", emptyList()))
        )
        coEvery {
            createColumnUseCase(any(), any(), any())
        } returns CreateColumnResult.GENERIC_ERROR

        runTest(testDispatcher) {
            viewModel.onIntent(BoardIntent.ObserveBoard(1))
            viewModel.onIntent(BoardIntent.StartCreatingColumn)
            viewModel.onIntent(BoardIntent.OnColumnNameChanged("new name"))

            viewModel.sideEffectFlow.test {
                viewModel.onIntent(BoardIntent.ConfirmColumnCreation)
                assertThat(awaitItem()).isInstanceOf(BoardSideEffect.ShowSnackBar::class.java)

                cancelAndConsumeRemainingEvents()
            }
            coVerify {
                createColumnUseCase(any(), any(), any())
                observeCompleteBoardUseCase(any())
            }
        }
    }

    @Test
    fun `SHOULD send snackbar WHEN create column is called and board is null`() {

        coEvery {
            createColumnUseCase(any(), any(), any())
        } returns CreateColumnResult.SUCCESS

        runTest(testDispatcher) {

            viewModel.sideEffectFlow.test {
                viewModel.onIntent(BoardIntent.ConfirmColumnCreation)
                assertThat(awaitItem()).isInstanceOf(BoardSideEffect.ShowSnackBar::class.java)

                cancelAndConsumeRemainingEvents()
            }
            coVerify(exactly = 0) {
                createColumnUseCase(any(), any(), any())
            }
        }
    }

    @Test
    fun `SHOULD send snackbar WHEN create column is called and column name is empty`() {

        coEvery { observeCompleteBoardUseCase(any()) } returns flowOf(
            Result.Success(Board(id = 1, name = "test", emptyList()))
        )

        coEvery {
            createColumnUseCase(any(), any(), any())
        } returns CreateColumnResult.SUCCESS


        runTest(testDispatcher) {
            viewModel.sideEffectFlow.test {
                viewModel.onIntent(BoardIntent.ObserveBoard(1))
                viewModel.onIntent(BoardIntent.StartCreatingColumn)

                // null
                viewModel.onIntent(BoardIntent.ConfirmColumnCreation)
                assertThat(awaitItem()).isInstanceOf(BoardSideEffect.ShowSnackBar::class.java)

                // empty
                viewModel.onIntent(BoardIntent.OnColumnNameChanged(""))
                viewModel.onIntent(BoardIntent.ConfirmColumnCreation)
                assertThat(awaitItem()).isInstanceOf(BoardSideEffect.ShowSnackBar::class.java)

                // blank
                viewModel.onIntent(BoardIntent.OnColumnNameChanged(" "))
                viewModel.onIntent(BoardIntent.ConfirmColumnCreation)
                assertThat(awaitItem()).isInstanceOf(BoardSideEffect.ShowSnackBar::class.java)

                cancelAndConsumeRemainingEvents()
            }
            coVerify(exactly = 0) {
                createColumnUseCase(any(), any(), any())
            }
        }
    }
}