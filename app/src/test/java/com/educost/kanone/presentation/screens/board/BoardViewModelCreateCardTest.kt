package com.educost.kanone.presentation.screens.board

import com.educost.kanone.domain.usecase.CreateCardResult
import com.educost.kanone.presentation.screens.board.state.CardCreationState
import com.educost.kanone.presentation.screens.board.utils.BoardAppBarType
import com.google.common.truth.Truth.assertThat
import io.mockk.coEvery
import io.mockk.coVerify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class BoardViewModelCreateCardTest : BoardViewModelTest() {

    @Test
    fun `SHOULD update state WHEN start creating card`() {
        val columnId = 1L
        val isAppendingToEnd = true
        testBoardViewModelUiState(
            whenAction = {
                viewModel.onIntent(BoardIntent.StartCreatingCard(columnId, isAppendingToEnd))
            },
            then = {
                val updatedState = awaitItem()
                assertThat(updatedState.topBarType).isEqualTo(BoardAppBarType.ADD_CARD)
                assertThat(updatedState.cardCreationState.columnId).isEqualTo(columnId)
                assertThat(updatedState.cardCreationState.isAppendingToEnd).isEqualTo(isAppendingToEnd)
            }
        )
    }

    @Test
    fun `SHOULD reset state WHEN cancel card creation`() = testBoardViewModelUiState(
        whenAction = {
            viewModel.onIntent(BoardIntent.StartCreatingCard(0, true))
            skipItems(1)

            viewModel.onIntent(BoardIntent.CancelCardCreation)
        },
        then = {
            val updatedState = awaitItem()
            assertThat(updatedState.topBarType).isEqualTo(BoardAppBarType.DEFAULT)
            assertThat(updatedState.cardCreationState).isEqualTo(CardCreationState())
        }
    )

    @Test
    fun `SHOULD update state WHEN new card title change`() = testBoardViewModelUiState(
        whenAction = {
            viewModel.onIntent(BoardIntent.OnCardTitleChange("new title"))
        },
        then = {
            assertThat(awaitItem().cardCreationState.title).isEqualTo("new title")
        }
    )

    @Test
    fun `SHOULD send snackbar WHEN result is empty title error`() = testBoardViewModelSideEffect(
        given = {
            coEvery {
                createCardUseCase(any(), any(), any())
            } returns CreateCardResult.EMPTY_TITLE
        },
        whenAction = {
            viewModel.onIntent(BoardIntent.ConfirmCardCreation)
        },
        then = {
            assertThat(awaitItem()).isInstanceOf(BoardSideEffect.ShowSnackBar::class.java)

            cancelAndConsumeRemainingEvents()
            coVerify { createCardUseCase(any(), any(), any()) }
        }
    )

    @Test
    fun `SHOULD send snackbar WHEN result is error`() = testBoardViewModelSideEffect(
        given = {
            coEvery {
                createCardUseCase(any(), any(), any())
            } returns CreateCardResult.GENERIC_ERROR
        },
        whenAction = {
            viewModel.onIntent(BoardIntent.ConfirmCardCreation)
        },
        then = {
            assertThat(awaitItem()).isInstanceOf(BoardSideEffect.ShowSnackBar::class.java)

            cancelAndConsumeRemainingEvents()
            coVerify { createCardUseCase(any(), any(), any()) }
        }
    )

    @Test
    fun `SHOULD call create card use case WHEN create card`() {
        val newTitle = "new title"
        val position = firstColumn.cards.size
        val columnId = firstColumn.id

        testBoardViewModelSideEffect(
            given = {
                coEvery {
                    createCardUseCase(newTitle, position, columnId)
                } returns CreateCardResult.SUCCESS
            },
            whenAction = {
                viewModel.onIntent(BoardIntent.StartCreatingCard(columnId, true))
                viewModel.onIntent(BoardIntent.OnCardTitleChange(newTitle))
                viewModel.onIntent(BoardIntent.ConfirmCardCreation)
            },
            then = {
                cancelAndConsumeRemainingEvents()
                coVerify(exactly = 1) {
                    createCardUseCase(newTitle, position, columnId)
                }
            }
        )
    }
}