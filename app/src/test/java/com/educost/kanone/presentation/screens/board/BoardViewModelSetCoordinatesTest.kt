package com.educost.kanone.presentation.screens.board

import androidx.compose.ui.geometry.Offset
import app.cash.turbine.test
import com.educost.kanone.dispatchers.DispatcherProvider
import com.educost.kanone.dispatchers.TestDispatcherProvider
import com.educost.kanone.domain.model.Board
import com.educost.kanone.domain.model.CardItem
import com.educost.kanone.domain.model.KanbanColumn
import com.educost.kanone.domain.usecase.ObserveCompleteBoardUseCase
import com.educost.kanone.presentation.screens.board.model.Coordinates
import com.educost.kanone.utils.Result
import com.google.common.truth.Truth.assertThat
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import java.time.LocalDateTime

@Suppress("UnusedFlow")
@OptIn(ExperimentalCoroutinesApi::class)
class BoardViewModelSetCoordinatesTest {

    private lateinit var testDispatcher: CoroutineDispatcher
    private lateinit var dispatcherProvider: DispatcherProvider
    private lateinit var viewModel: BoardViewModel
    private lateinit var observeCompleteBoardUseCase: ObserveCompleteBoardUseCase

    @Before
    fun setUp() {
        testDispatcher = UnconfinedTestDispatcher()
        dispatcherProvider = TestDispatcherProvider(testDispatcher)
        observeCompleteBoardUseCase = mockk()
        viewModel = BoardViewModel(
            dispatcherProvider = dispatcherProvider,
            observeCompleteBoardUseCase = observeCompleteBoardUseCase,
            createColumnUseCase = mockk(),
            createCardUseCase = mockk(),
            updateColumnUseCase = mockk(),
            deleteColumnUseCase = mockk(),
            restoreColumnUseCase = mockk(),
            persistBoardPositionsUseCase = mockk(),
            reorderCardsUseCase = mockk()
        )
    }

    @Test
    fun `SHOULD update board coordinates WHEN new board coordinates are set`() {

        coEvery { observeCompleteBoardUseCase(any()) } returns flowOf(
            Result.Success(Board(id = 1, name = "test", emptyList()))
        )

        runTest(testDispatcher) {
            viewModel.uiState.test {
                assertThat(awaitItem().board).isNull()
                viewModel.onIntent(BoardIntent.ObserveBoard(1))
                awaitItem() // Loading state

                assertThat(awaitItem().board?.coordinates).isEqualTo(Coordinates())
                val newCoordinates = Coordinates(10, 20, Offset(x = 100f, y = 200f))
                viewModel.onIntent(BoardIntent.SetBoardCoordinates(newCoordinates))
                assertThat(awaitItem().board?.coordinates).isEqualTo(newCoordinates)
                cancelAndConsumeRemainingEvents()
                coVerify { observeCompleteBoardUseCase(any()) }
            }
        }
    }

    @Test
    fun `SHOULD update column header coordinates WHEN new column header coordinates are set`() {

        val newBoard = Board(
            id = 1,
            name = "board test",
            columns = listOf(
                KanbanColumn(
                    id = 1,
                    name = "column test 1",
                    position = 1,
                    color = null,
                    cards = emptyList()
                ),
                KanbanColumn(
                    id = 2,
                    name = "column test 2",
                    position = 2,
                    color = null,
                    cards = emptyList()
                )
            )
        )

        coEvery { observeCompleteBoardUseCase(any()) } returns flowOf(
            Result.Success(newBoard)
        )

        runTest(testDispatcher) {
            viewModel.uiState.test {
                assertThat(awaitItem().board).isNull()
                viewModel.onIntent(BoardIntent.ObserveBoard(1))
                awaitItem() // Loading state

                val firstColumns = awaitItem().board?.columns
                assertThat(firstColumns?.get(0)?.headerCoordinates).isEqualTo(
                    Coordinates()
                )
                assertThat(firstColumns?.get(1)?.headerCoordinates).isEqualTo(
                    Coordinates()
                )

                val newCoordinates = Coordinates(10, 20, Offset(x = 100f, y = 200f))
                viewModel.onIntent(BoardIntent.SetColumnHeaderCoordinates(1, newCoordinates))

                val updatedColumns = awaitItem().board?.columns
                assertThat(updatedColumns?.get(0)?.headerCoordinates).isEqualTo(
                    newCoordinates
                )
                assertThat(updatedColumns?.get(1)?.headerCoordinates).isEqualTo(
                    Coordinates()
                )


                cancelAndConsumeRemainingEvents()
                coVerify { observeCompleteBoardUseCase(any()) }
            }
        }
    }

    @Test
    fun `SHOULD update column body coordinates WHEN new column body coordinates are set`() {

        val newBoard = Board(
            id = 1,
            name = "board test",
            columns = listOf(
                KanbanColumn(
                    id = 1,
                    name = "column test 1",
                    position = 1,
                    color = null,
                    cards = emptyList()
                ),
                KanbanColumn(
                    id = 2,
                    name = "column test 2",
                    position = 2,
                    color = null,
                    cards = emptyList()
                )
            )
        )

        coEvery { observeCompleteBoardUseCase(any()) } returns flowOf(
            Result.Success(newBoard)
        )


        runTest(testDispatcher) {
            viewModel.uiState.test {
                assertThat(awaitItem().board).isNull()
                viewModel.onIntent(BoardIntent.ObserveBoard(1))
                awaitItem() // Loading state

                val firstColumns = awaitItem().board?.columns
                assertThat(firstColumns?.get(0)?.listCoordinates).isEqualTo(
                    Coordinates()
                )
                assertThat(firstColumns?.get(1)?.listCoordinates).isEqualTo(
                    Coordinates()
                )

                val newCoordinates = Coordinates(10, 20, Offset(x = 100f, y = 200f))
                viewModel.onIntent(BoardIntent.SetColumnBodyCoordinates(1, newCoordinates))

                val updatedColumns = awaitItem().board?.columns
                assertThat(updatedColumns?.get(0)?.listCoordinates).isEqualTo(
                    newCoordinates
                )
                assertThat(updatedColumns?.get(1)?.listCoordinates).isEqualTo(
                    Coordinates()
                )


                cancelAndConsumeRemainingEvents()
                coVerify { observeCompleteBoardUseCase(any()) }
            }
        }
    }

    @Test
    fun `SHOULD update column coordinates WHEN new column coordinates are set`() {

        val newBoard = Board(
            id = 1,
            name = "board test",
            columns = listOf(
                KanbanColumn(
                    id = 1,
                    name = "column test 1",
                    position = 1,
                    color = null,
                    cards = emptyList()
                ),
                KanbanColumn(
                    id = 2,
                    name = "column test 2",
                    position = 2,
                    color = null,
                    cards = emptyList()
                )
            )
        )

        coEvery { observeCompleteBoardUseCase(any()) } returns flowOf(
            Result.Success(newBoard)
        )


        runTest(testDispatcher) {
            viewModel.uiState.test {
                viewModel.onIntent(BoardIntent.ObserveBoard(1))
                skipItems(2)

                val firstColumns = awaitItem().board?.columns
                assertThat(firstColumns?.get(0)?.coordinates).isEqualTo(
                    Coordinates()
                )
                assertThat(firstColumns?.get(1)?.coordinates).isEqualTo(
                    Coordinates()
                )

                val newCoordinates = Coordinates(10, 20, Offset(x = 100f, y = 200f))
                viewModel.onIntent(BoardIntent.SetColumnCoordinates(1, newCoordinates))

                val updatedColumns = awaitItem().board?.columns
                assertThat(updatedColumns?.get(0)?.coordinates).isEqualTo(
                    newCoordinates
                )
                assertThat(updatedColumns?.get(1)?.coordinates).isEqualTo(
                    Coordinates()
                )


                cancelAndConsumeRemainingEvents()
                coVerify { observeCompleteBoardUseCase(any()) }
            }
        }
    }

    @Test
    fun `SHOULD update card coordinates WHEN new card coordinates are set`() {

        val newBoard = Board(
            id = 1,
            name = "board test",
            columns = listOf(
                KanbanColumn(
                    id = 1,
                    name = "column test 1",
                    position = 1,
                    color = null,
                    cards = listOf(
                        CardItem(
                            id = 1,
                            title = "card test 1",
                            description = null,
                            position = 1,
                            color = null,
                            createdAt = LocalDateTime.now(),
                            dueDate = null,
                            thumbnailFileName = null,
                            tasks = emptyList(),
                            attachments = emptyList(),
                            labels = emptyList()
                        ),
                        CardItem(
                            id = 2,
                            title = "card test 2",
                            description = null,
                            position = 1,
                            color = null,
                            createdAt = LocalDateTime.now(),
                            dueDate = null,
                            thumbnailFileName = null,
                            tasks = emptyList(),
                            attachments = emptyList(),
                            labels = emptyList()
                        )
                    )
                )
            )
        )

        coEvery { observeCompleteBoardUseCase(any()) } returns flowOf(
            Result.Success(newBoard)
        )

        runTest(testDispatcher) {
            viewModel.uiState.test {
                assertThat(awaitItem().board).isNull()
                viewModel.onIntent(BoardIntent.ObserveBoard(1))
                awaitItem() // Loading state

                val firstCards = awaitItem().board?.columns?.get(0)?.cards
                assertThat(firstCards?.get(0)?.coordinates).isEqualTo(
                    Coordinates()
                )
                assertThat(firstCards?.get(1)?.coordinates).isEqualTo(
                    Coordinates()
                )

                val newCoordinates = Coordinates(10, 20, Offset(x = 100f, y = 200f))
                viewModel.onIntent(BoardIntent.SetCardCoordinates(1, 1, newCoordinates))

                val updatedCards = awaitItem().board?.columns?.get(0)?.cards
                assertThat(updatedCards?.get(0)?.coordinates).isEqualTo(
                    newCoordinates
                )
                assertThat(updatedCards?.get(1)?.coordinates).isEqualTo(
                    Coordinates()
                )


                cancelAndConsumeRemainingEvents()
                coVerify { observeCompleteBoardUseCase(any()) }
            }
        }
    }
}