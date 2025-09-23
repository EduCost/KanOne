package com.educost.kanone.presentation.screens.board

import com.educost.kanone.presentation.screens.board.state.ColumnEditState
import com.educost.kanone.presentation.screens.board.utils.BoardAppBarType
import com.google.common.truth.Truth.assertThat
import io.mockk.coEvery
import io.mockk.coVerify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class BoardViewModelEditColumnTest : BoardViewModelTest() {

    @Test
    fun `SHOULD update edit column name WHEN new name is provided`() = testBoardViewModelUiState(
        whenAction = {
            viewModel.onIntent(BoardIntent.OnEditColumnNameChange("new name"))
        },
        then = {
            assertThat(awaitItem().columnEditState.newColumnName).isEqualTo("new name")
        }
    )

    @Test
    fun `SHOULD reset column edit state WHEN cancel column editing`() = testBoardViewModelUiState(
        whenAction = {
            viewModel.onIntent(BoardIntent.OnRenameColumnClicked(columnId = 1))
            viewModel.onIntent(BoardIntent.OnEditColumnNameChange("new name"))
            skipItems(2)

            viewModel.onIntent(BoardIntent.CancelColumnRename)
        },
        then = {
            val updatedState = awaitItem()
            assertThat(updatedState.columnEditState).isEqualTo(ColumnEditState())
            assertThat(updatedState.topBarType).isEqualTo(BoardAppBarType.DEFAULT)
        }
    )

    @Test
    fun `SHOULD send snackbar WHEN confirm column rename with empty name`() {
        testBoardViewModelSideEffect(
            given = {
                coEvery { updateColumnUseCase(any(), any()) } returns true
            },
            whenAction = {
                viewModel.onIntent(BoardIntent.OnRenameColumnClicked(firstColumn.id))

                // empty
                viewModel.onIntent(BoardIntent.OnEditColumnNameChange(""))
                viewModel.onIntent(BoardIntent.ConfirmColumnRename)

                // blank
                viewModel.onIntent(BoardIntent.OnEditColumnNameChange(" "))
                viewModel.onIntent(BoardIntent.ConfirmColumnRename)
            },
            then = {
                assertThat(awaitItem()).isInstanceOf(BoardSideEffect.ShowSnackBar::class.java)
                assertThat(awaitItem()).isInstanceOf(BoardSideEffect.ShowSnackBar::class.java)

                cancelAndConsumeRemainingEvents()
                coVerify(exactly = 0) { updateColumnUseCase(any(), any()) }
            }
        )
    }

    @Test
    fun `SHOULD send snackbar WHEN confirm column rename and can not find column with id`() {
        testBoardViewModelSideEffect(
            given = {
                coEvery { updateColumnUseCase(any(), any()) } returns true
            },
            whenAction = {
                viewModel.onIntent(BoardIntent.OnRenameColumnClicked(-1L))
                viewModel.onIntent(BoardIntent.OnEditColumnNameChange("new name"))
                viewModel.onIntent(BoardIntent.ConfirmColumnRename)
            },
            then = {
                assertThat(awaitItem()).isInstanceOf(BoardSideEffect.ShowSnackBar::class.java)

                cancelAndConsumeRemainingEvents()
                coVerify(exactly = 0) { updateColumnUseCase(any(), any()) }
            }
        )
    }

    @Test
    fun `SHOULD send snackbar WHEN confirm column rename returns error`() {
        testBoardViewModelSideEffect(
            given = {
                coEvery { updateColumnUseCase(any(), any()) } returns false // error
            },
            whenAction = {
                val columnId = firstColumn.id
                viewModel.onIntent(BoardIntent.OnRenameColumnClicked(columnId))
                viewModel.onIntent(BoardIntent.OnEditColumnNameChange("new name"))
                viewModel.onIntent(BoardIntent.ConfirmColumnRename)
            },
            then = {
                assertThat(awaitItem()).isInstanceOf(BoardSideEffect.ShowSnackBar::class.java)

                cancelAndConsumeRemainingEvents()
                coVerify(exactly = 1) { updateColumnUseCase(any(), any()) }
            }
        )
    }

    @Test
    fun `SHOULD call update column use case WHEN confirm column rename with necessary data`() {
        val newName = "new name"
        val expectedColumn = firstColumn.copy(name = newName)
        testBoardViewModelSideEffect(
            given = {
                coEvery { updateColumnUseCase(expectedColumn, board.id) } returns true
            },
            whenAction = {
                val columnId = firstColumn.id
                viewModel.onIntent(BoardIntent.OnRenameColumnClicked(columnId))
                viewModel.onIntent(BoardIntent.OnEditColumnNameChange(newName))
                viewModel.onIntent(BoardIntent.ConfirmColumnRename)
            },
            then = {
                cancelAndConsumeRemainingEvents()
                coVerify(exactly = 1) { updateColumnUseCase(expectedColumn, board.id) }
            }
        )
    }

    @Test
    fun `SHOULD reset column edit state WHEN confirm column rename is called`() {
        val newName = "new name"
        val expectedColumn = firstColumn.copy(name = newName)

        testBoardViewModelUiState(
            given = {
                coEvery { updateColumnUseCase(expectedColumn, board.id) } returns true
            },
            whenAction = {
                val columnId = firstColumn.id
                viewModel.onIntent(BoardIntent.OnRenameColumnClicked(columnId))
                viewModel.onIntent(BoardIntent.OnEditColumnNameChange(newName))
                viewModel.onIntent(BoardIntent.ConfirmColumnRename)
                skipItems(2)
            },
            then = {
                val updatedState = awaitItem()
                assertThat(updatedState.columnEditState).isEqualTo(ColumnEditState())
                assertThat(updatedState.topBarType).isEqualTo(BoardAppBarType.DEFAULT)

                cancelAndConsumeRemainingEvents()
                coVerify { updateColumnUseCase(expectedColumn, board.id) }
            }
        )
    }

    @Test
    fun `SHOULD update column edit state WHEN start editing column color`() {
        val columnId = 1L

        testBoardViewModelUiState(
            whenAction = {
                viewModel.onIntent(BoardIntent.StartEditingColumnColor(columnId))
            },
            then = {
                val updatedState = awaitItem()
                assertThat(updatedState.columnEditState.editingColumnId).isEqualTo(columnId)
                assertThat(updatedState.columnEditState.isShowingColorPicker).isTrue()
            }
        )
    }

    @Test
    fun `SHOULD send snackbar WHEN tried to update color but did not find column`() {
        testBoardViewModelSideEffect(
            given = {
                coEvery { updateColumnUseCase(any(), any()) } returns true
            },
            whenAction = {
                viewModel.onIntent(BoardIntent.StartEditingColumnColor(-1L)) // should not find column
                viewModel.onIntent(BoardIntent.ConfirmColumnColorEdit(-1))
            },
            then = {
                assertThat(awaitItem()).isInstanceOf(BoardSideEffect.ShowSnackBar::class.java)

                cancelAndConsumeRemainingEvents()
                coVerify(exactly = 0) { updateColumnUseCase(any(), any()) }
            }
        )
    }

    @Test
    fun `SHOULD send snackbar WHEN update column color returns error`() {
        testBoardViewModelSideEffect(
            given = {
                coEvery { updateColumnUseCase(any(), any()) } returns false
            },
            whenAction = {
                val columnId = firstColumn.id
                viewModel.onIntent(BoardIntent.StartEditingColumnColor(columnId))
                viewModel.onIntent(BoardIntent.ConfirmColumnColorEdit(-1))
            },
            then = {
                assertThat(awaitItem()).isInstanceOf(BoardSideEffect.ShowSnackBar::class.java)

                cancelAndConsumeRemainingEvents()
                coVerify { updateColumnUseCase(any(), any()) }
            }
        )
    }

    @Test
    fun `SHOULD call update column use case WHEN update column color`() {
        val newColor = -1
        val expectedColumn = firstColumn.copy(color = newColor)

        testBoardViewModelSideEffect(
            given = {
                coEvery { updateColumnUseCase(expectedColumn, board.id) } returns true
            },
            whenAction = {
                viewModel.onIntent(BoardIntent.StartEditingColumnColor(expectedColumn.id))
                viewModel.onIntent(BoardIntent.ConfirmColumnColorEdit(newColor))
            },
            then = {
                cancelAndConsumeRemainingEvents()
                coVerify(exactly = 1) { updateColumnUseCase(expectedColumn, board.id) }
            }
        )
    }

    @Test
    fun `SHOULD reset column edit state WHEN update column color`() {
        val newColor = -1
        val expectedColumn = firstColumn.copy(color = newColor)

        testBoardViewModelUiState(
            given = {
                coEvery { updateColumnUseCase(expectedColumn, board.id) } returns true
            },
            whenAction = {
                viewModel.onIntent(BoardIntent.StartEditingColumnColor(expectedColumn.id))
                skipItems(1)
                viewModel.onIntent(BoardIntent.ConfirmColumnColorEdit(newColor))
            },
            then = {
                val updatedState = awaitItem()
                assertThat(updatedState.columnEditState).isEqualTo(ColumnEditState())
                assertThat(updatedState.topBarType).isEqualTo(BoardAppBarType.DEFAULT)

                cancelAndConsumeRemainingEvents()
                coVerify(exactly = 1) { updateColumnUseCase(expectedColumn, board.id) }
            }
        )
    }

}