package com.educost.kanone.presentation.screens.board

import app.cash.turbine.test
import com.educost.kanone.dispatchers.DispatcherProvider
import com.educost.kanone.dispatchers.TestDispatcherProvider
import com.educost.kanone.domain.error.FetchDataError
import com.educost.kanone.domain.model.Board
import com.educost.kanone.domain.usecase.ObserveCompleteBoardUseCase
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
class BoardViewModelObserveBoardTest {

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
            updateColumnUseCase = mockk(),
            deleteColumnUseCase = mockk(),
            restoreColumnUseCase = mockk(),
            persistBoardPositionsUseCase = mockk(),
            reorderCardsUseCase = mockk()
        )
    }


    @Test
    fun `GIVEN result success, WHEN board is observed, THEN loading state updates to true then false`() {

        val boardId = 1L

        coEvery { observeCompleteBoardUseCase(boardId) } returns flowOf(
            Result.Success(Board(id = boardId, name = "test", emptyList()))
        )

        runTest(testDispatcher) {
            viewModel.uiState.test {
                assertThat(awaitItem().isLoading).isFalse()
                viewModel.onIntent(BoardIntent.ObserveBoard(boardId))
                assertThat(awaitItem().isLoading).isTrue()
                assertThat(awaitItem().isLoading).isFalse()
                cancelAndConsumeRemainingEvents()
                coVerify { observeCompleteBoardUseCase(boardId) }
            }
        }
    }

    @Test
    fun `GIVEN result error, WHEN board is observed, THEN loading state updates to true then false`() {

        coEvery { observeCompleteBoardUseCase(any()) } returns flowOf(
            Result.Error(FetchDataError.UNKNOWN)
        )

        runTest(testDispatcher) {
            viewModel.uiState.test {
                assertThat(awaitItem().isLoading).isFalse()
                viewModel.onIntent(BoardIntent.ObserveBoard(1))
                assertThat(awaitItem().isLoading).isTrue()
                assertThat(awaitItem().isLoading).isFalse()
                cancelAndConsumeRemainingEvents()
                coVerify { observeCompleteBoardUseCase(any()) }
            }
        }
    }

    @Test
    fun `GIVEN result error, WHEN board is observed, THEN snackbar is sent`() {

        coEvery { observeCompleteBoardUseCase(any()) } returns flowOf(
            Result.Error(FetchDataError.UNKNOWN)
        )

        runTest(testDispatcher) {
            viewModel.sideEffectFlow.test {

                viewModel.onIntent(BoardIntent.ObserveBoard(1))
                assertThat(awaitItem()).isInstanceOf(BoardSideEffect.ShowSnackBar::class.java)

                cancelAndConsumeRemainingEvents()
            }
        }

    }

}