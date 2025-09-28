package com.educost.kanone.presentation.screens.board

import com.google.common.truth.Truth.assertThat
import io.mockk.coEvery
import io.mockk.coVerify
import org.junit.Test

class BoardViewModelBoardSettingsTest : BoardViewModelTest() {

    @Test
    fun `SHOULD open board settings WHEN openBoardSettings is called`() {
        testBoardViewModelUiState(
            whenAction = {
                viewModel.onIntent(BoardIntent.OpenBoardSettings)
            },
            then = {
                assertThat(awaitItem().isModalSheetExpanded).isTrue()
            }
        )
    }

    @Test
    fun `SHOULD close board settings WHEN closeBoardSettings is called`() {
        testBoardViewModelUiState(
            whenAction = {
                viewModel.onIntent(BoardIntent.OpenBoardSettings)
                skipItems(1)
                viewModel.onIntent(BoardIntent.CloseBoardSettings)
            },
            then = {
                assertThat(awaitItem().isModalSheetExpanded).isFalse()
            }
        )
    }

    @Test
    fun `SHOULD toggle show images WHEN toggleShowImages is called`() {
        val expectedBoard = board.copy(showImages = !board.showImages)

        testBoardViewModelUiState(
            given = {
                coEvery { updateBoardUseCase(expectedBoard) } returns true
            },
            whenAction = {
                viewModel.onIntent(BoardIntent.ToggleShowImages)
            },
            then = {
                cancelAndConsumeRemainingEvents()
                coVerify(exactly = 1) { updateBoardUseCase(expectedBoard) }
            }
        )
    }

    @Test
    fun `SHOULD update isChangingZoom WHEN onZoomChange is called`() {
        testBoardViewModelUiState(
            given = {
                coEvery { updateBoardUseCase(any()) } returns true
            },
            whenAction = {
                viewModel.onIntent(BoardIntent.OnZoomChange(2f, 0f))
            },
            then = {
                assertThat(awaitItem().isChangingZoom).isTrue()
            }
        )
    }

    @Test
    fun `SHOULD update zoom WHEN onZoomChange is called`() {
        val currentZoom = board.zoomPercentage
        testBoardViewModelUiState(
            given = {
                coEvery { updateBoardUseCase(any()) } returns true
            },
            whenAction = {
                viewModel.onIntent(BoardIntent.OnZoomChange(2f, 0f))
                skipItems(1) // skip isChangingZoom state
            },
            then = {
                val newZoom = awaitItem().board?.sizes?.zoomPercentage
                assertThat(newZoom).isNotEqualTo(currentZoom)
            }
        )
    }

    @Test
    fun `SHOULD update isChangingZoom WHEN setZoom is called`() {
        testBoardViewModelUiState(
            given = {
                coEvery { updateBoardUseCase(any()) } returns true
            },
            whenAction = {
                viewModel.onIntent(BoardIntent.SetZoom(50f))
            },
            then = {
                assertThat(awaitItem().isChangingZoom).isTrue()
            }
        )
    }

    @Test
    fun `SHOULD set zoom WHEN setZoom is called`() {
        val zoom = 50f

        testBoardViewModelUiState(
            given = {
                coEvery { updateBoardUseCase(any()) } returns true
            },
            whenAction = {
                viewModel.onIntent(BoardIntent.SetZoom(zoom))
                skipItems(1) // skip isChangingZoom state
            },
            then = {
                val updatedZoom = awaitItem().board?.sizes?.zoomPercentage
                assertThat(updatedZoom).isEqualTo(zoom)
            }
        )
    }

    @Test
    fun `SHOULD update isChangingZoom to false WHEN zoom is persisted`() {
        testBoardViewModelUiState(
            given = {
                coEvery { updateBoardUseCase(any()) } returns true
            },
            whenAction = {
                viewModel.onIntent(BoardIntent.SetZoom(50f))
                skipItems(1) // skip isChangingZoom set to true
                skipItems(1) // skip new zoom state
            },
            then = {
                assertThat(awaitItem().isChangingZoom).isFalse()
            }
        )
    }

    @Test
    fun `SHOULD enter full screen WHEN enterFullScreen is called`() {
        testBoardViewModelUiState(
            whenAction = {
                viewModel.onIntent(BoardIntent.EnterFullScreen)
            },
            then = {
                assertThat(awaitItem().isOnFullScreen).isTrue()
            }
        )
    }

    @Test
    fun `SHOULD exit full screen WHEN exitFullScreen is called`() {
        testBoardViewModelUiState(
            whenAction = {
                viewModel.onIntent(BoardIntent.EnterFullScreen)
                skipItems(1)
                viewModel.onIntent(BoardIntent.ExitFullScreen)
            },
            then = {
                assertThat(awaitItem().isOnFullScreen).isFalse()
            }
        )
    }
}