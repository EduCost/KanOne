package com.educost.kanone.presentation.screens.board

import com.educost.kanone.presentation.screens.board.utils.BoardAppBarType
import com.educost.kanone.presentation.screens.board.utils.CardOrder
import com.educost.kanone.presentation.screens.board.utils.OrderType
import com.google.common.truth.Truth.assertThat
import io.mockk.coEvery
import io.mockk.coVerify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class BoardViewModelColumnDropdownTest : BoardViewModelTest() {

    @Test
    fun `SHOULD update UI state WHEN open dropdown menu`() {
        val columnId = 1L
        testBoardViewModelUiState(
            whenAction = {
                viewModel.onIntent(BoardIntent.OpenColumnDropdownMenu(columnId))
            },
            then = {
                assertThat(awaitItem().activeDropdownColumnId).isEqualTo(columnId)

            }
        )
    }

    @Test
    fun `SHOULD reset UI state WHEN close dropdown menu`() = testBoardViewModelUiState(
        whenAction = {
            // Open
            viewModel.onIntent(BoardIntent.OpenColumnDropdownMenu(1L))
            skipItems(1)

            // Close
            viewModel.onIntent(BoardIntent.CloseColumnDropdownMenu)
        },
        then = {
            assertThat(awaitItem().activeDropdownColumnId).isEqualTo(null)
        }
    )

    @Test
    fun `SHOULD update UI state WHEN rename column is clicked`() {
        val columnId = firstColumn.id

        testBoardViewModelUiState(
            whenAction = {
                viewModel.onIntent(BoardIntent.OnRenameColumnClicked(columnId))
            },
            then = {
                val updatedState = awaitItem()
                assertThat(updatedState.topBarType).isEqualTo(BoardAppBarType.RENAME_COLUMN)
                assertThat(updatedState.columnEditState.editingColumnId).isEqualTo(columnId)
                assertThat(updatedState.columnEditState.newColumnName).isEqualTo(firstColumn.name)
                assertThat(updatedState.columnEditState.isRenaming).isTrue()
            }
        )
    }

    @Test
    fun `SHOULD send snackbar WHEN did not find column on delete column`() {
        testBoardViewModelSideEffect(
            given = {
                coEvery {
                    deleteColumnUseCase(any(), any())
                } returns false
            },
            whenAction = {
                val columnId = -1L // Column not found

                viewModel.onIntent(BoardIntent.OnDeleteColumnClicked(columnId))
            },
            then = {
                assertThat(awaitItem()).isInstanceOf(BoardSideEffect.ShowSnackBar::class.java)

                cancelAndConsumeRemainingEvents()
                coVerify(exactly = 0) { deleteColumnUseCase(any(), any()) }
            }
        )
    }

    @Test
    fun `SHOULD send snackbar WHEN delete column is successful`() {
        testBoardViewModelSideEffect(
            given = {
                coEvery { deleteColumnUseCase(any(), any()) } returns true // Success
            },
            whenAction = {
                viewModel.onIntent(BoardIntent.OnDeleteColumnClicked(1L))
            },
            then = {
                assertThat(awaitItem()).isInstanceOf(BoardSideEffect.ShowSnackBar::class.java)

                cancelAndConsumeRemainingEvents()
                coVerify(exactly = 1) { deleteColumnUseCase(any(), any()) }
            }
        )
    }

    @Test
    fun `SHOULD send snackbar WHEN delete column is not successful`() {
        testBoardViewModelSideEffect(
            given = {
                coEvery { deleteColumnUseCase(any(), any()) } returns false // Failure
            },
            whenAction = {
                viewModel.onIntent(BoardIntent.OnDeleteColumnClicked(1L))
            },
            then = {
                assertThat(awaitItem()).isInstanceOf(BoardSideEffect.ShowSnackBar::class.java)

                cancelAndConsumeRemainingEvents()
                coVerify(exactly = 1) { deleteColumnUseCase(any(), any()) }
            }
        )
    }

    @Test
    fun `SHOULD send snackbar WHEN reorder cards use case returns error`() {
        testBoardViewModelSideEffect(
            given = {
                coEvery {
                    reorderCardsUseCase(any(), any(), any())
                } returns false // Failure
            },
            whenAction = {
                viewModel.onIntent(
                    BoardIntent.OnOrderByClicked(
                        columnId = 1L,
                        orderType = OrderType.ASCENDING,
                        cardOrder = CardOrder.NAME
                    )
                )
            },
            then = {
                assertThat(awaitItem()).isInstanceOf(BoardSideEffect.ShowSnackBar::class.java)

                cancelAndConsumeRemainingEvents()
                coVerify(exactly = 1) {
                    reorderCardsUseCase(any(), any(), any())
                }
            }
        )
    }

    @Test
    fun `SHOULD call reorder cards use case WHEN call reorder cards`() {
        testBoardViewModelSideEffect(
            given = {
                coEvery {
                    reorderCardsUseCase(any(), any(), any())
                } returns true // Success
            },
            whenAction = {
                viewModel.onIntent(
                    BoardIntent.OnOrderByClicked(
                        columnId = 1L,
                        orderType = OrderType.ASCENDING,
                        cardOrder = CardOrder.NAME
                    )
                )
            },
            then = {
                coVerify(exactly = 1) {
                    reorderCardsUseCase(any(), any(), any())
                }
            }
        )
    }

}