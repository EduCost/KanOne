package com.educost.kanone.presentation.screens.board

import com.google.common.truth.Truth.assertThat
import io.mockk.coEvery
import io.mockk.coVerify
import org.junit.Test

class BoardViewModelDeleteBoardTest : BoardViewModelTest() {

    @Test
    fun `SHOULD set isShowingDeleteBoardDialog to true WHEN onDeleteBoardClicked is called`() {
        testBoardViewModelUiState(
            whenAction = {
                viewModel.onIntent(BoardIntent.OnDeleteBoardClicked)
            },
            then = {
                assertThat(awaitItem().isShowingDeleteBoardDialog).isTrue()
            }
        )
    }

    @Test
    fun `SHOULD set isShowingDeleteBoardDialog to false WHEN CancelBoardDeletion is called`() {
        testBoardViewModelUiState(
            whenAction = {
                viewModel.onIntent(BoardIntent.OnDeleteBoardClicked)
                skipItems(1)
                viewModel.onIntent(BoardIntent.CancelBoardDeletion)
            },
            then = {
                assertThat(awaitItem().isShowingDeleteBoardDialog).isFalse()
            }
        )
    }

    @Test
    fun `SHOULD navigate back WHEN board deletion is successful`() {
        testBoardViewModelSideEffect(
            given = {
                coEvery { deleteBoardUseCase(any()) } returns true
            },
            whenAction = {
                viewModel.onIntent(BoardIntent.ConfirmBoardDeletion)
            },
            then = {
                assertThat(awaitItem()).isInstanceOf(BoardSideEffect.OnNavigateBack::class.java)

                cancelAndConsumeRemainingEvents()
                coVerify(exactly = 1) { deleteBoardUseCase(any()) }
            }
        )
    }

    @Test
    fun `SHOULD show snackbar WHEN board deletion fails`() {
        testBoardViewModelSideEffect(
            given = {
                coEvery { deleteBoardUseCase(any()) } returns false
            },
            whenAction = {
                viewModel.onIntent(BoardIntent.ConfirmBoardDeletion)
            },
            then = {
                assertThat(awaitItem()).isInstanceOf(BoardSideEffect.ShowSnackBar::class.java)

                cancelAndConsumeRemainingEvents()
                coVerify(exactly = 1) { deleteBoardUseCase(any()) }
            }
        )
    }

    @Test
    fun `SHOULD set isShowingDeleteBoardDialog to false WHEN board deletion fails`() {
        testBoardViewModelUiState(
            given = {
                coEvery { deleteBoardUseCase(any()) } returns false
            },
            whenAction = {
                viewModel.onIntent(BoardIntent.OnDeleteBoardClicked)
                skipItems(1)
                viewModel.onIntent(BoardIntent.ConfirmBoardDeletion)
            },
            then = {
                assertThat(awaitItem().isShowingDeleteBoardDialog).isFalse()

                cancelAndConsumeRemainingEvents()
                coVerify(exactly = 1) { deleteBoardUseCase(any()) }
            }
        )
    }
}