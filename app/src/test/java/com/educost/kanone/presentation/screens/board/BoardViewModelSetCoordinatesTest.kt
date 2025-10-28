package com.educost.kanone.presentation.screens.board

import androidx.compose.ui.geometry.Offset
import com.educost.kanone.presentation.screens.board.model.Coordinates
import com.educost.kanone.presentation.screens.board.utils.CoordinatesIntent
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class BoardViewModelSetCoordinatesTest : BoardViewModelTest() {

    @Test
    fun `SHOULD update board coordinates WHEN new board coordinates are set`() {
        val newCoordinates = Coordinates(10, 20, Offset(x = 100f, y = 200f))

        testBoardViewModelUiState(
            whenAction = {
                viewModel.onIntent(BoardIntent.OnSetCoordinates(
                    CoordinatesIntent.SetBoardCoordinates(newCoordinates)
                ))
            },
            then = {
                assertThat(awaitItem().board?.coordinates).isEqualTo(newCoordinates)
            }
        )
    }

    @Test
    fun `SHOULD update column header coordinates WHEN new column header coordinates are set`() {
        val newCoordinates = Coordinates(10, 20, Offset(x = 100f, y = 200f))

        testBoardViewModelUiState(
            whenAction = {
                val columnId = firstColumn.id
                viewModel.onIntent(BoardIntent.OnSetCoordinates(
                    CoordinatesIntent.SetColumnHeaderCoordinates(columnId, newCoordinates)
                ))
            },
            then = {
                val updatedColumns = awaitItem().board?.columns
                assertThat(updatedColumns?.get(0)?.headerCoordinates).isEqualTo(newCoordinates)
                assertThat(updatedColumns?.get(1)?.headerCoordinates).isEqualTo(Coordinates())
            }
        )
    }

    @Test
    fun `SHOULD update column list coordinates WHEN new column list coordinates are set`() {
        val newCoordinates = Coordinates(10, 20, Offset(x = 100f, y = 200f))

        testBoardViewModelUiState(
            whenAction = {
                val columnId = firstColumn.id
                viewModel.onIntent(BoardIntent.OnSetCoordinates(
                    CoordinatesIntent.SetColumnListCoordinates(columnId, newCoordinates)
                ))
            },
            then = {
                val updatedColumns = awaitItem().board?.columns
                assertThat(updatedColumns?.get(0)?.listCoordinates).isEqualTo(newCoordinates)
                assertThat(updatedColumns?.get(1)?.listCoordinates).isEqualTo(Coordinates())
            }
        )
    }

    @Test
    fun `SHOULD update column coordinates WHEN new column coordinates are set`() {
        val newCoordinates = Coordinates(10, 20, Offset(x = 100f, y = 200f))

        testBoardViewModelUiState(
            whenAction = {
                val columnId = firstColumn.id
                viewModel.onIntent(BoardIntent.OnSetCoordinates(
                    CoordinatesIntent.SetColumnCoordinates(columnId, newCoordinates)
                ))
            },
            then = {
                val updatedColumns = awaitItem().board?.columns
                assertThat(updatedColumns?.get(0)?.coordinates).isEqualTo(newCoordinates)
                assertThat(updatedColumns?.get(1)?.coordinates).isEqualTo(Coordinates())
            }
        )
    }

    @Test
    fun `SHOULD update card coordinates WHEN new card coordinates are set`() {
        val newCoordinates = Coordinates(10, 20, Offset(x = 100f, y = 200f))

        testBoardViewModelUiState(
            whenAction = {
                val cardId = firstCardOfFirstColumn.id
                viewModel.onIntent(BoardIntent.OnSetCoordinates(
                    CoordinatesIntent.SetCardCoordinates(cardId, newCoordinates)
                ))
            },
            then = {
                val updatedCards = awaitItem().board!!.columns[0].cards
                assertThat(updatedCards[0].coordinates).isEqualTo(newCoordinates)
                assertThat(updatedCards[1].coordinates).isEqualTo(Coordinates())
            }
        )
    }
}