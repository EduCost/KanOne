package com.educost.kanone.presentation.screens.board

import com.educost.kanone.domain.usecase.CreateCardResult
import com.educost.kanone.presentation.screens.board.state.CardCreationState
import com.educost.kanone.presentation.screens.board.utils.BoardAppBarType
import com.educost.kanone.utils.Result
import com.google.common.truth.Truth.assertThat
import io.mockk.coEvery
import io.mockk.coVerify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class BoardViewModelCreateCardTest : BoardViewModelTest() {

    @Test
    fun `SHOULD update state WHEN start creating card`() = testBoardViewModelUiState {
        val columnId = 1L
        viewModel.onIntent(BoardIntent.StartCreatingCard(columnId, true))

        val updatedState = awaitItem()
        assertThat(updatedState.topBarType).isEqualTo(BoardAppBarType.ADD_CARD)
        assertThat(updatedState.cardCreationState.columnId).isEqualTo(columnId)
    }

    @Test
    fun `SHOULD reset state WHEN cancel card creation`() = testBoardViewModelUiState {
        viewModel.onIntent(BoardIntent.StartCreatingCard(0, true))
        skipItems(1)

        viewModel.onIntent(BoardIntent.CancelCardCreation)
        val updatedState = awaitItem()
        assertThat(updatedState.topBarType).isEqualTo(BoardAppBarType.DEFAULT)
        assertThat(updatedState.cardCreationState).isEqualTo(CardCreationState())
    }

    @Test
    fun `SHOULD update state WHEN new card title change`() = testBoardViewModelUiState {

        viewModel.onIntent(BoardIntent.OnCardTitleChange("new title"))
        assertThat(awaitItem().cardCreationState.title).isEqualTo("new title")

    }

    @Test
    fun `SHOULD send snackbar WHEN create card with null title`() {
        val title = null
        val position = column1.cards.size
        val columnId = column1.id

        testBoardViewModelSideEffect(
            testSetUp = {
                coEvery {
                    createCardUseCase(title, position, columnId)
                } returns CreateCardResult.EMPTY_TITLE
            },
            testBody = {
                viewModel.onIntent(BoardIntent.StartCreatingCard(column1.id, true))

                viewModel.onIntent(BoardIntent.ConfirmCardCreation)
                assertThat(awaitItem()).isInstanceOf(BoardSideEffect.ShowSnackBar::class.java)
            },
            testValidate = {
                coVerify { createCardUseCase(title, position, columnId) }
            }
        )
    }

    @Test
    fun `SHOULD send snackbar WHEN create card with null columnId`() {
        val newTitle = "new title"
        val columnId = null

        testBoardViewModelSideEffect(
            testSetUp = {
                coEvery {
                    createCardUseCase(newTitle, any(), columnId)
                } returns CreateCardResult.GENERIC_ERROR
            },
            testBody = {
                // columnId not specified because start create card is not called
                viewModel.onIntent(BoardIntent.OnCardTitleChange(newTitle))

                viewModel.onIntent(BoardIntent.ConfirmCardCreation)
                assertThat(awaitItem()).isInstanceOf(BoardSideEffect.ShowSnackBar::class.java)
            },
            testValidate = {
                coVerify { createCardUseCase(newTitle, any(), columnId) }
            }
        )
    }

    @Test
    fun `SHOULD send snackbar WHEN did not find column while creating card`() {
        val newTitle = "new title"
        val columnId = -1L // column not found

        testBoardViewModelSideEffect(
            testSetUp = {
                coEvery {
                    createCardUseCase(newTitle, any(), columnId)
                } returns CreateCardResult.GENERIC_ERROR
            },
            testBody = {
                viewModel.onIntent(
                    BoardIntent.StartCreatingCard(
                        columnId = columnId,
                        isAppendingToEnd = true
                    )
                )
                viewModel.onIntent(BoardIntent.OnCardTitleChange(newTitle))

                viewModel.onIntent(BoardIntent.ConfirmCardCreation)
                assertThat(awaitItem()).isInstanceOf(BoardSideEffect.ShowSnackBar::class.java)
            },
            testValidate = {
                coVerify { createCardUseCase(newTitle, any(), columnId) }
            }
        )
    }

    @Test
    fun `SHOULD call card repository WHEN create card`() = runTest(testDispatcher) {
        val newTitle = "new title"
        val position = column1.cards.size
        val columnId = column1.id

        coEvery { observeCompleteBoardUseCase(defaultBoard.id) } returns flowOf(
            Result.Success(defaultBoard)
        )
        coEvery { createCardUseCase(newTitle, position, columnId) } returns CreateCardResult.SUCCESS

        viewModel.onIntent(BoardIntent.ObserveBoard(defaultBoard.id))
        viewModel.onIntent(BoardIntent.StartCreatingCard(columnId, true))
        viewModel.onIntent(BoardIntent.OnCardTitleChange(newTitle))
        viewModel.onIntent(BoardIntent.ConfirmCardCreation)

        coVerify {
            createCardUseCase(newTitle, position, columnId)
        }
    }

}