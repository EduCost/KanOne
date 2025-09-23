package com.educost.kanone.presentation.screens.board

import com.educost.kanone.presentation.screens.board.utils.BoardAppBarType
import com.educost.kanone.presentation.screens.board.utils.CardOrder
import com.educost.kanone.presentation.screens.board.utils.OrderType
import com.educost.kanone.utils.Result
import com.google.common.truth.Truth.assertThat
import io.mockk.coEvery
import io.mockk.coVerify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class BoardViewModelColumnDropdownTest : BoardViewModelTest() {

    @Test
    fun `SHOULD update UI state WHEN open dropdown menu`() = testBoardViewModelUiState {
        val columnId = column1.id

        viewModel.onIntent(BoardIntent.OpenColumnDropdownMenu(columnId))
        assertThat(awaitItem().activeDropdownColumnId).isEqualTo(columnId)
    }

    @Test
    fun `SHOULD reset UI state WHEN close dropdown menu`() = testBoardViewModelUiState {
        val columnId = column1.id

        // Open
        viewModel.onIntent(BoardIntent.OpenColumnDropdownMenu(columnId))
        skipItems(1)

        // Close
        viewModel.onIntent(BoardIntent.CloseColumnDropdownMenu)
        assertThat(awaitItem().activeDropdownColumnId).isEqualTo(null)
    }

    @Test
    fun `SHOULD update UI state WHEN rename column is called`() = testBoardViewModelUiState {
        val columnId = column1.id

        viewModel.onIntent(BoardIntent.OnRenameColumnClicked(columnId))

        val updatedState = awaitItem()
        assertThat(updatedState.topBarType).isEqualTo(BoardAppBarType.RENAME_COLUMN)
        assertThat(updatedState.columnEditState.editingColumnId).isEqualTo(columnId)
        assertThat(updatedState.columnEditState.isRenaming).isTrue()
    }

    @Test
    fun `SHOULD send snackbar WHEN did not find column on delete column`() {
        val boardId = defaultBoard.id
        val column = column1

        testBoardViewModelSideEffect(
            testSetUp = {
                coEvery {
                    deleteColumnUseCase(column, boardId)
                } returns false
            },
            testBody = {
                val columnId = -1L // Column not found

                viewModel.onIntent(BoardIntent.OnDeleteColumnClicked(columnId))
                assertThat(awaitItem()).isInstanceOf(BoardSideEffect.ShowSnackBar::class.java)
            },
            testValidate = {
                coVerify(exactly = 0) { deleteColumnUseCase(column, boardId) }
            }
        )
    }


    @Test
    fun `SHOULD call delete column use case WHEN delete column is called`() {
        runTest(testDispatcher) {

            coEvery { observeCompleteBoardUseCase(any()) } returns flowOf(
                Result.Success(defaultBoard)
            )
            coEvery { deleteColumnUseCase(column1, defaultBoard.id) } returns true

            viewModel.onIntent(BoardIntent.ObserveBoard(defaultBoard.id))
            viewModel.onIntent(BoardIntent.OnDeleteColumnClicked(column1.id))

            coVerify(exactly = 1) { deleteColumnUseCase(column1, defaultBoard.id) }
        }
    }

    @Test
    fun `SHOULD send snackbar WHEN delete column is successful`() {
        testBoardViewModelSideEffect(
            testSetUp = {
                coEvery { deleteColumnUseCase(any(), any()) } returns true // Success
            },
            testBody = {
                viewModel.onIntent(BoardIntent.OnDeleteColumnClicked(1L))
                assertThat(awaitItem()).isInstanceOf(BoardSideEffect.ShowSnackBar::class.java)
            },
            testValidate = {
                coVerify(exactly = 1) { deleteColumnUseCase(any(), any()) }
            }
        )
    }

    @Test
    fun `SHOULD send snackbar WHEN delete column is not successful`() {
        testBoardViewModelSideEffect(
            testSetUp = {
                coEvery { deleteColumnUseCase(any(), any()) } returns false // Failure
            },
            testBody = {
                viewModel.onIntent(BoardIntent.OnDeleteColumnClicked(1L))
                assertThat(awaitItem()).isInstanceOf(BoardSideEffect.ShowSnackBar::class.java)
            },
            testValidate = {
                coVerify { deleteColumnUseCase(any(), any()) }
            }
        )
    }

    @Test
    fun `SHOULD send snackbar WHEN use case returns error`() {
        testBoardViewModelSideEffect(
            testSetUp = {
                coEvery {
                    reorderCardsUseCase(any(), any(), any())
                } returns false // Failure
            },
            testBody = {
                viewModel.onIntent(
                    BoardIntent.OnOrderByClicked(
                        columnId = 1L,
                        orderType = OrderType.ASCENDING,
                        cardOrder = CardOrder.NAME
                    )
                )
                assertThat(awaitItem()).isInstanceOf(BoardSideEffect.ShowSnackBar::class.java)
            },
            testValidate = {
                coVerify {
                    reorderCardsUseCase(any(), any(), any())
                }
            }
        )
    }

    @Test
    fun `SHOULD call reorder cards use case WHEN call reorder cards`() {
        testBoardViewModelSideEffect(
            testSetUp = {
                coEvery {
                    reorderCardsUseCase(any(), any(), any())
                } returns true // Success
            },
            testBody = {
                viewModel.onIntent(
                    BoardIntent.OnOrderByClicked(
                        columnId = 1L,
                        orderType = OrderType.ASCENDING,
                        cardOrder = CardOrder.NAME
                    )
                )
            },
            testValidate = {
                coVerify(exactly = 1) {
                    reorderCardsUseCase(any(), any(), any())
                }
            }
        )
    }

    @Test
    fun `SHOULD send snackbar WHEN call reorder cards and returns error`() {
       testBoardViewModelSideEffect(
           testSetUp = {
               coEvery {
                   reorderCardsUseCase(any(), any(), any())
               } returns false // Failure
           },
           testBody = {
               viewModel.onIntent(
                   BoardIntent.OnOrderByClicked(
                       columnId = 1L,
                       orderType = OrderType.ASCENDING,
                       cardOrder = CardOrder.NAME
                   )
               )
               assertThat(awaitItem()).isInstanceOf(BoardSideEffect.ShowSnackBar::class.java)
           },
           testValidate = {
               coVerify {
                   reorderCardsUseCase(any(), any(), any())
               }
           }
       )
    }
}