package com.educost.kanone.presentation.screens.board

import app.cash.turbine.test
import com.educost.kanone.domain.error.GenericError
import com.educost.kanone.utils.Result
import com.google.common.truth.Truth.assertThat
import io.mockk.coEvery
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Test

class BoardViewModelOthersTest : BoardViewModelTest() {

    @Test
    fun `SHOULD clear states and keep full screen WHEN is on full screen and has edit states`() {
        testBoardViewModelUiState(
            whenAction = {
                viewModel.onIntent(BoardIntent.EnterFullScreen)
                skipItems(1)

                viewModel.onIntent(BoardIntent.OnRenameBoardClicked)
                skipItems(1)

                viewModel.onIntent(BoardIntent.OnBackPressed)
            },
            then = {
                val currentState = awaitItem()

                assertThat(currentState.isRenamingBoard).isFalse()
                assertThat(currentState.isOnFullScreen).isTrue()
            }
        )
    }

    @Test
    fun `SHOULD exit full screen WHEN is on full screen and does not have edit states`() {
        testBoardViewModelUiState(
            whenAction = {
                viewModel.onIntent(BoardIntent.EnterFullScreen)
                skipItems(1)

                viewModel.onIntent(BoardIntent.OnBackPressed)
            },
            then = {
                assertThat(awaitItem().isOnFullScreen).isFalse()
            }
        )
    }

    @Test
    fun `SHOULD clear states WHEN has edit states and is not on full screen`() {
        testBoardViewModelUiState(
            whenAction = {
                viewModel.onIntent(BoardIntent.OnRenameBoardClicked)
                skipItems(1)

                viewModel.onIntent(BoardIntent.OnBackPressed)
            },
            then = {
                assertThat(awaitItem().isRenamingBoard).isFalse()
            }
        )
    }

    @Test
    fun `SHOULD navigate back WHEN navigate back is called`() {
        testBoardViewModelSideEffect(
            whenAction = {
                viewModel.onIntent(BoardIntent.OnNavigateBack)
            },
            then = {
                assertThat(awaitItem()).isInstanceOf(BoardSideEffect.OnNavigateBack::class.java)
            }
        )
    }

    @Test
    fun `SHOULD navigate to card screen WHEN navigate to card screen is called`() {
        testBoardViewModelSideEffect(
            whenAction = {
                viewModel.onIntent(BoardIntent.OnCardClick(1))
            },
            then = {
                assertThat(awaitItem()).isInstanceOf(BoardSideEffect.NavigateToCardScreen::class.java)
            }
        )
    }

    @Test
    fun `SHOULD navigate to settings WHEN navigate to settings is called`() {
        testBoardViewModelSideEffect(
            whenAction = {
                viewModel.onIntent(BoardIntent.NavigateToSettings)
            },
            then = {
                assertThat(awaitItem()).isInstanceOf(BoardSideEffect.NavigateToSettings::class.java)
            }
        )
    }

    @Test
    fun `SHOULD open board dropdown menu WHEN open board dropdown menu is called`() {
        testBoardViewModelUiState(
            whenAction = {
                viewModel.onIntent(BoardIntent.OpenBoardDropdownMenu)
            },
            then = {
                assertThat(awaitItem().isBoardDropdownMenuExpanded).isTrue()
            }
        )
    }

    @Test
    fun `SHOULD close board dropdown menu WHEN close board dropdown menu is called`() {
        testBoardViewModelUiState(
            whenAction = {
                viewModel.onIntent(BoardIntent.OpenBoardDropdownMenu)
                skipItems(1)
                viewModel.onIntent(BoardIntent.CloseBoardDropdownMenu)
            },
            then = {
                assertThat(awaitItem().isBoardDropdownMenuExpanded).isFalse()
            }
        )
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