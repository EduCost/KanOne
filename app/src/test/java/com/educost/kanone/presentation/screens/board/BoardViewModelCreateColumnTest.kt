package com.educost.kanone.presentation.screens.board

import com.educost.kanone.domain.usecase.CreateColumnResult
import com.educost.kanone.presentation.screens.board.utils.BoardAppBarType
import com.google.common.truth.Truth.assertThat
import io.mockk.coEvery
import io.mockk.coVerify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class BoardViewModelCreateColumnTest : BoardViewModelTest() {

    @Test
    fun `SHOULD set topBarType to ADD_COLUMN WHEN start creating new column`() {
        testBoardViewModelUiState(
            whenAction = {
                viewModel.onIntent(BoardIntent.StartCreatingColumn)
            },
            then = {
                assertThat(awaitItem().topBarType).isEqualTo(BoardAppBarType.ADD_COLUMN)
            }
        )
    }

    @Test
    fun `SHOULD update new column name state WHEN new column name change`() {
        val name = "new name"
        testBoardViewModelUiState(
            whenAction = {
                viewModel.onIntent(BoardIntent.OnColumnNameChanged(name))
            },
            then = {
                assertThat(awaitItem().creatingColumnName).isEqualTo(name)
            }
        )
    }

    @Test
    fun `SHOULD reset state WHEN cancel column creation`() = testBoardViewModelUiState(
        whenAction = {
            viewModel.onIntent(BoardIntent.StartCreatingColumn)
            viewModel.onIntent(BoardIntent.OnColumnNameChanged("new name"))
            skipItems(2)

            viewModel.onIntent(BoardIntent.CancelColumnCreation)
        },
        then = {
            val updatedState = awaitItem()
            assertThat(updatedState.creatingColumnName).isEqualTo(null)
            assertThat(updatedState.topBarType).isEqualTo(BoardAppBarType.DEFAULT)
        }
    )

    @Test
    fun `SHOULD reset state WHEN creation is successful`() = testBoardViewModelUiState(
        given = {
            coEvery {
                createColumnUseCase(any(), any(), any())
            } returns CreateColumnResult.SUCCESS
        },
        whenAction = {
            viewModel.onIntent(BoardIntent.StartCreatingColumn)
            viewModel.onIntent(BoardIntent.OnColumnNameChanged("name"))
            skipItems(2)

            viewModel.onIntent(BoardIntent.ConfirmColumnCreation)
        },
        then = {
            val updatedState = awaitItem()
            assertThat(updatedState.creatingColumnName).isEqualTo(null)
            assertThat(updatedState.topBarType).isEqualTo(BoardAppBarType.DEFAULT)

            cancelAndConsumeRemainingEvents()
            coVerify {
                createColumnUseCase(any(), any(), any())
            }
        }
    )

    @Test
    fun `SHOULD send snackbar WHEN result is failure`() = testBoardViewModelSideEffect(
        given = {
            coEvery {
                createColumnUseCase(any(), any(), any())
            } returns CreateColumnResult.GENERIC_ERROR
        },
        whenAction = {
            viewModel.onIntent(BoardIntent.StartCreatingColumn)
            viewModel.onIntent(BoardIntent.OnColumnNameChanged("name"))

            viewModel.onIntent(BoardIntent.ConfirmColumnCreation)
        },
        then = {
            assertThat(awaitItem()).isInstanceOf(BoardSideEffect.ShowSnackBar::class.java)

            cancelAndConsumeRemainingEvents()
            coVerify {
                createColumnUseCase(any(), any(), any())
            }
        }
    )

    @Test
    fun `SHOULD send snackbar WHEN result is empty name error`() = testBoardViewModelSideEffect(
        given = {
            coEvery {
                createColumnUseCase(any(), any(), any())
            } returns CreateColumnResult.EMPTY_NAME
        },
        whenAction = {
            viewModel.onIntent(BoardIntent.StartCreatingColumn)
            viewModel.onIntent(BoardIntent.OnColumnNameChanged("name"))

            viewModel.onIntent(BoardIntent.ConfirmColumnCreation)
        },
        then = {
            assertThat(awaitItem()).isInstanceOf(BoardSideEffect.ShowSnackBar::class.java)

            cancelAndConsumeRemainingEvents()
            coVerify {
                createColumnUseCase(any(), any(), any())
            }
        }
    )
}