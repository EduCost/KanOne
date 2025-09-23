package com.educost.kanone.presentation.screens.board

import app.cash.turbine.test
import com.educost.kanone.domain.error.GenericError
import com.educost.kanone.domain.model.Board
import com.educost.kanone.utils.Result
import com.google.common.truth.Truth.assertThat
import io.mockk.coEvery
import io.mockk.coVerify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Test

@Suppress("UnusedFlow")
@OptIn(ExperimentalCoroutinesApi::class)
class BoardViewModelObserveBoardTest : BoardViewModelTest() {

    @Test
    fun `SHOULD update loading state to true than to false WHEN observe board is successful`() {

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
    fun `SHOULD update loading state to true than to false WHEN observe board returns error`() {

        coEvery { observeCompleteBoardUseCase(any()) } returns flowOf(
            Result.Error(GenericError)
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
    fun `SHOULD send snackbar WHEN observe board returns error`() {

        coEvery { observeCompleteBoardUseCase(any()) } returns flowOf(
            Result.Error(GenericError)
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