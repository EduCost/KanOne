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
        testBoardViewModelUiState {
            viewModel.onIntent(BoardIntent.StartCreatingColumn)
            assertThat(awaitItem().topBarType).isEqualTo(BoardAppBarType.ADD_COLUMN)
        }
    }

    @Test
    fun `SHOULD update new column name state WHEN new column name change`() {
        testBoardViewModelUiState {
            val name = "new name"
            viewModel.onIntent(BoardIntent.OnColumnNameChanged(name))
            assertThat(awaitItem().creatingColumnName).isEqualTo(name)
        }
    }

    @Test
    fun `SHOULD reset state WHEN cancel column creation`() = testBoardViewModelUiState {

        viewModel.onIntent(BoardIntent.StartCreatingColumn)
        viewModel.onIntent(BoardIntent.OnColumnNameChanged("new name"))
        skipItems(2)


        viewModel.onIntent(BoardIntent.CancelColumnCreation)
        val updatedState = awaitItem()
        assertThat(updatedState.creatingColumnName).isEqualTo(null)
        assertThat(updatedState.topBarType).isEqualTo(BoardAppBarType.DEFAULT)
    }

    @Test
    fun `SHOULD reset state WHEN creation is successful`() {
        val columnName = "new name"
        val position = defaultBoard.columns.size
        val boardId = defaultBoard.id

        testBoardViewModelUiState(
            testSetUp = {
                coEvery {
                    createColumnUseCase(columnName, position, boardId)
                } returns CreateColumnResult.SUCCESS
            },
            testBody = {
                viewModel.onIntent(BoardIntent.StartCreatingColumn)
                viewModel.onIntent(BoardIntent.OnColumnNameChanged(columnName))
                skipItems(2)

                viewModel.onIntent(BoardIntent.ConfirmColumnCreation)
                val updatedState = awaitItem()
                assertThat(updatedState.creatingColumnName).isEqualTo(null)
                assertThat(updatedState.topBarType).isEqualTo(BoardAppBarType.DEFAULT)
            },
            testValidate = {
                coVerify {
                    createColumnUseCase(columnName, position, boardId)
                }
            }

        )
    }

    @Test
    fun `SHOULD send snackbar WHEN creation is failure`() {
        val columnName = "new name"
        val position = defaultBoard.columns.size
        val boardId = defaultBoard.id

        testBoardViewModelSideEffect(
            testSetUp = {
                coEvery {
                    createColumnUseCase(columnName, position, boardId)
                } returns CreateColumnResult.GENERIC_ERROR
            },
            testBody = {
                viewModel.onIntent(BoardIntent.StartCreatingColumn)
                viewModel.onIntent(BoardIntent.OnColumnNameChanged(columnName))

                viewModel.onIntent(BoardIntent.ConfirmColumnCreation)
                assertThat(awaitItem()).isInstanceOf(BoardSideEffect.ShowSnackBar::class.java)
            },
            testValidate = {
                coVerify {
                    createColumnUseCase(columnName, position, boardId)
                }
            }

        )
    }

    @Test
    fun `SHOULD send snackbar WHEN use case return empty name error`() {
        testBoardViewModelSideEffect(
            testSetUp = {
                coEvery {
                    createColumnUseCase(any(), any(), any())
                } returns CreateColumnResult.EMPTY_NAME
            },
            testBody = {
                viewModel.onIntent(BoardIntent.StartCreatingColumn)
                viewModel.onIntent(BoardIntent.OnColumnNameChanged(""))

                viewModel.onIntent(BoardIntent.ConfirmColumnCreation)
                assertThat(awaitItem()).isInstanceOf(BoardSideEffect.ShowSnackBar::class.java)
            },
            testValidate = {
                coVerify {
                    createColumnUseCase(any(), any(), any())
                }
            }
        )
    }
}