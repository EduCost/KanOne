package com.educost.kanone.presentation.screens.board

import app.cash.turbine.test
import com.educost.kanone.dispatchers.DispatcherProvider
import com.educost.kanone.dispatchers.TestDispatcherProvider
import com.educost.kanone.domain.error.InsertDataError
import com.educost.kanone.domain.model.Board
import com.educost.kanone.domain.usecase.CreateColumnUseCase
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
            updateColumnUseCase = mockk()
        )
    }

    @Test
    fun `GIVEN default UI state, WHEN startCreatingColumn intent is processed, THEN topBarType is ADD_COLUMN`() {

        coEvery { observeCompleteBoardUseCase(any()) } returns flowOf(
            Result.Success(Board(id = 1, name = "test", emptyList()))
        )

        runTest(testDispatcher) {
            viewModel.uiState.test {
                viewModel.onIntent(BoardIntent.ObserveBoard(1))

                repeat(3) { // initial value -> loading state -> board received
                    awaitItem()
                }

                viewModel.onIntent(BoardIntent.StartCreatingColumn)
                assertThat(awaitItem().topBarType).isEqualTo(BoardAppBarType.ADD_COLUMN)

                cancelAndConsumeRemainingEvents()
                coVerify { observeCompleteBoardUseCase(any()) }
            }
        }
    }

    @Test
    fun `GIVEN new column name, WHEN OnColumnNameChanged intent is processed, THEN column name state is updated`() {

        coEvery { observeCompleteBoardUseCase(any()) } returns flowOf(
            Result.Success(Board(id = 1, name = "test", emptyList()))
        )

        runTest(testDispatcher) {
            viewModel.uiState.test {
                viewModel.onIntent(BoardIntent.ObserveBoard(1))

                repeat(3) { // initial value -> loading state -> board received
                    awaitItem()
                }

                viewModel.onIntent(BoardIntent.OnColumnNameChanged("new name"))
                assertThat(awaitItem().creatingColumnName).isEqualTo("new name")

                cancelAndConsumeRemainingEvents()
                coVerify { observeCompleteBoardUseCase(any()) }
            }
        }
    }

    @Test
    fun `GIVEN creating column, WHEN CancelColumnCreation intent is processed, THEN state is reset`() {

        coEvery { observeCompleteBoardUseCase(any()) } returns flowOf(
            Result.Success(Board(id = 1, name = "test", emptyList()))
        )

        runTest(testDispatcher) {
            viewModel.uiState.test {
                viewModel.onIntent(BoardIntent.ObserveBoard(1))
                viewModel.onIntent(BoardIntent.StartCreatingColumn)
                viewModel.onIntent(BoardIntent.OnColumnNameChanged("new name"))

                repeat(3) { // initial value -> loading state -> board received
                    awaitItem()
                }

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
    fun `GIVEN creating column, WHEN creation is successful, THEN state is reset`() {

        coEvery { observeCompleteBoardUseCase(any()) } returns flowOf(
            Result.Success(Board(id = 1, name = "test", emptyList()))
        )
        coEvery { createColumnUseCase(any(), any()) } returns Result.Success(1)

        runTest(testDispatcher) {
            viewModel.uiState.test {
                viewModel.onIntent(BoardIntent.ObserveBoard(1))
                viewModel.onIntent(BoardIntent.StartCreatingColumn)
                viewModel.onIntent(BoardIntent.OnColumnNameChanged("new name"))

                repeat(5) {
                    awaitItem()
                }

                viewModel.onIntent(BoardIntent.ConfirmColumnCreation)
                val updatedState = awaitItem()
                assertThat(updatedState.creatingColumnName).isEqualTo(null)
                assertThat(updatedState.topBarType).isEqualTo(BoardAppBarType.DEFAULT)

                cancelAndConsumeRemainingEvents()
                coVerify {
                    createColumnUseCase(any(), any())
                    observeCompleteBoardUseCase(any())
                }
            }
        }
    }

    @Test
    fun `GIVEN creating column, WHEN creation is failure, THEN snackbar is sent`() {

        coEvery { observeCompleteBoardUseCase(any()) } returns flowOf(
            Result.Success(Board(id = 1, name = "test", emptyList()))
        )
        coEvery {
            createColumnUseCase(any(), any())
        } returns Result.Error(InsertDataError.UNKNOWN)

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
                createColumnUseCase(any(), any())
                observeCompleteBoardUseCase(any())
            }
        }
    }

    @Test
    fun `GIVEN null board, WHEN create column is processed, THEN snackbar is sent`() {

        coEvery {
            createColumnUseCase(any(), any())
        } returns Result.Success(1)

        runTest(testDispatcher) {

            viewModel.sideEffectFlow.test {
                viewModel.onIntent(BoardIntent.ConfirmColumnCreation)
                assertThat(awaitItem()).isInstanceOf(BoardSideEffect.ShowSnackBar::class.java)

                cancelAndConsumeRemainingEvents()
            }
            coVerify(exactly = 0) {
                createColumnUseCase(any(), any())
            }
        }
    }

    /*TODO: test send snackbar when name is empty*/
}