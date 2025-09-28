package com.educost.kanone.presentation.screens.board

import com.google.common.truth.Truth.assertThat
import io.mockk.coEvery
import io.mockk.coVerify
import org.junit.Test

class BoardViewModelRenameBoardTest : BoardViewModelTest() {

    @Test
    fun `SHOULD set isRenamingBoard to true WHEN onRenameBoardClicked is called`() {
        testBoardViewModelUiState(
            whenAction = {
                viewModel.onIntent(BoardIntent.OnRenameBoardClicked)
            },
            then = {
                assertThat(awaitItem().isRenamingBoard).isTrue()
            }
        )
    }

    @Test
    fun `SHOULD set isRenamingBoard to false WHEN cancelBoardRename is called`() {
        testBoardViewModelUiState(
            whenAction = {
                viewModel.onIntent(BoardIntent.OnRenameBoardClicked)
                skipItems(1)
                viewModel.onIntent(BoardIntent.CancelBoardRename)
            },
            then = {
                assertThat(awaitItem().isRenamingBoard).isFalse()
            }
        )
    }

    @Test
    fun `SHOULD call updateBoardUseCase WHEN confirmBoardRename is called`() {
        val newName = "New Board Name"

        testBoardViewModelUiState(
            given = {
                coEvery {
                    updateBoardUseCase(board.copy(name = newName))
                } returns true
            },
            whenAction = {
                viewModel.onIntent(BoardIntent.ConfirmBoardRename(newName))
            },
            then = {
                cancelAndConsumeRemainingEvents()
                coVerify(exactly = 1) {
                    updateBoardUseCase(board.copy(name = newName))
                }
            }
        )
    }

    @Test
    fun `SHOULD reset isRenamingBoard to false WHEN board rename is successful`() {
        val newName = "New Board Name"

        testBoardViewModelUiState(
            given = {
                coEvery {
                    updateBoardUseCase(board.copy(name = newName))
                } returns true
            },
            whenAction = {
                viewModel.onIntent(BoardIntent.OnRenameBoardClicked)
                skipItems(1)
                viewModel.onIntent(BoardIntent.ConfirmBoardRename(newName))
            },
            then = {
                assertThat(awaitItem().isRenamingBoard).isFalse()

                cancelAndConsumeRemainingEvents()
                coVerify(exactly = 1) {
                    updateBoardUseCase(board.copy(name = newName))
                }
            }
        )
    }

    @Test
    fun `SHOULD reset isRenamingBoard to false WHEN board rename is unsuccessful`() {
        val newName = "New Board Name"

        testBoardViewModelUiState(
            given = {
                coEvery {
                    updateBoardUseCase(board.copy(name = newName))
                } returns false
            },
            whenAction = {
                viewModel.onIntent(BoardIntent.OnRenameBoardClicked)
                skipItems(1)
                viewModel.onIntent(BoardIntent.ConfirmBoardRename(newName))
            },
            then = {
                assertThat(awaitItem().isRenamingBoard).isFalse()

                cancelAndConsumeRemainingEvents()
                coVerify(exactly = 1) {
                    updateBoardUseCase(board.copy(name = newName))
                }
            }
        )
    }

    @Test
    fun `SHOULD send snackbar WHEN board rename is unsuccessful`() {
        val newName = "New Board Name"

        testBoardViewModelSideEffect(
            given = {
                coEvery {
                    updateBoardUseCase(board.copy(name = newName))
                } returns false
            },
            whenAction = {
                viewModel.onIntent(BoardIntent.ConfirmBoardRename(newName))
            },
            then = {
                assertThat(awaitItem()).isInstanceOf(BoardSideEffect.ShowSnackBar::class.java)

                cancelAndConsumeRemainingEvents()
                coVerify(exactly = 1) {
                    updateBoardUseCase(board.copy(name = newName))
                }
            }
        )
    }

}