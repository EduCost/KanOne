package com.educost.kanone.presentation.screens.board

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class BoardViewModelOthersTest : BoardViewModelTest() {

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
}