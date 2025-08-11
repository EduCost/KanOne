package com.educost.kanone.presentation.screens.board

import androidx.compose.ui.geometry.Offset
import app.cash.turbine.test
import com.educost.kanone.dispatchers.DispatcherProvider
import com.educost.kanone.dispatchers.TestDispatcherProvider
import com.educost.kanone.domain.error.FetchDataError
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
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import java.time.LocalDateTime

@OptIn(ExperimentalCoroutinesApi::class)
@Suppress("UnusedFlow")
class BoardViewModelMappingTest {

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
            restoreColumnUseCase = mockk()
        )
    }

    @Test
    fun `GIVEN null board, WHEN new board is observed, THEN new board is mapped to BoardUi`() {

        coEvery { observeCompleteBoardUseCase(any()) } returns flowOf(
            Result.Success(Board(id = 1, name = "test", emptyList()))
        )

        runTest(testDispatcher) {
            viewModel.uiState.test {
                assertThat(awaitItem().board).isNull()
                viewModel.onIntent(BoardIntent.ObserveBoard(1))

                awaitItem() // Loading state

                val newBoard = awaitItem().board
                assertThat(newBoard?.id).isEqualTo(1)
                assertThat(newBoard?.name).isEqualTo("test")
                assertThat(newBoard?.coordinates).isEqualTo(Coordinates())
                cancelAndConsumeRemainingEvents()
                coVerify { observeCompleteBoardUseCase(any()) }
            }
        }
    }

    @Test
    fun `GIVEN existing board, WHEN new board is observed, THEN previous board is updated without changing Ui properties`() {

        val boardFlow = MutableSharedFlow<Result<Board, FetchDataError>>()
        coEvery { observeCompleteBoardUseCase(any()) } returns boardFlow.asSharedFlow()

        runTest(testDispatcher) {
            viewModel.uiState.test {

                assertThat(awaitItem().board).isNull()
                viewModel.onIntent(BoardIntent.ObserveBoard(1))
                awaitItem() // Loading state

                boardFlow.emit(Result.Success(Board(id = 1, name = "test", emptyList())))
                val firstBoard = awaitItem().board

                assertThat(firstBoard?.id).isEqualTo(1)
                assertThat(firstBoard?.name).isEqualTo("test")
                assertThat(firstBoard?.coordinates).isEqualTo(Coordinates())

                val newCoordinates = Coordinates(10, 20, Offset(x = 100f, y = 200f))
                viewModel.onIntent(BoardIntent.SetBoardCoordinates(newCoordinates))
                assertThat(awaitItem().board?.coordinates).isEqualTo(newCoordinates)

                boardFlow.emit(Result.Success(Board(id = 1, name = "new name", emptyList())))
                val updatedBoard = awaitItem().board
                assertThat(updatedBoard?.id).isEqualTo(1)
                assertThat(updatedBoard?.name).isEqualTo("new name")
                assertThat(updatedBoard?.coordinates).isEqualTo(newCoordinates)


                cancelAndConsumeRemainingEvents()
                coVerify { observeCompleteBoardUseCase(any()) }
            }
        }
    }

    @Test
    fun `GIVEN empty columns, WHEN new columns is collected, THEN new columns are mapped to ColumnUi`() {

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
            Result.Success(
                newBoard
            )
        )

        runTest(testDispatcher) {
            viewModel.uiState.test {
                assertThat(awaitItem().board).isNull()
                viewModel.onIntent(BoardIntent.ObserveBoard(1))

                awaitItem() // Loading state

                val updatedBoard = awaitItem().board
                assertThat(updatedBoard?.id).isEqualTo(1)
                assertThat(updatedBoard?.columns).hasSize(2)
                assertThat(updatedBoard?.columns[0]?.id).isEqualTo(1)
                assertThat(updatedBoard?.columns[0]?.bodyCoordinates).isEqualTo(Coordinates())

                assertThat(updatedBoard?.columns[1]?.id).isEqualTo(2)
                assertThat(updatedBoard?.columns[1]?.bodyCoordinates).isEqualTo(Coordinates())

                cancelAndConsumeRemainingEvents()
                coVerify { observeCompleteBoardUseCase(any()) }
            }
        }
    }

    @Test
    fun `GIVEN existing columns, WHEN existing column is updated, THEN column is updated without changing Ui properties`() {

        val boardFlow = MutableSharedFlow<Result<Board, FetchDataError>>()
        coEvery { observeCompleteBoardUseCase(any()) } returns boardFlow.asSharedFlow()

        val firstEmission = Result.Success(
            Board(
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
        )
        val secondEmission = Result.Success(
            Board(
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
                        name = "column updated 2",
                        position = 2,
                        color = null,
                        cards = emptyList()
                    )
                )
            )
        )

        runTest(testDispatcher) {
            viewModel.uiState.test {

                assertThat(awaitItem().board).isNull()
                viewModel.onIntent(BoardIntent.ObserveBoard(1))
                awaitItem() // Loading state

                boardFlow.emit(firstEmission)
                val firstBoard = awaitItem().board
                assertThat(firstBoard?.id).isEqualTo(1)
                assertThat(firstBoard?.columns).hasSize(2)

                val firstColumnCoordinates = Coordinates(50, 60, Offset(x = 100f, y = 300f))
                val secondColumnCoordinates = Coordinates(30, 70, Offset(x = 200f, y = 300f))

                viewModel.onIntent(
                    BoardIntent.SetColumnBodyCoordinates(
                        1,
                        firstColumnCoordinates
                    )
                )
                assertThat(awaitItem().board?.columns[0]?.bodyCoordinates).isEqualTo(
                    firstColumnCoordinates
                )

                viewModel.onIntent(
                    BoardIntent.SetColumnBodyCoordinates(
                        2,
                        secondColumnCoordinates
                    )
                )
                assertThat(awaitItem().board?.columns[1]?.bodyCoordinates).isEqualTo(
                    secondColumnCoordinates
                )

                val newBoardCoordinates = Coordinates(10, 20, Offset(x = 100f, y = 200f))
                viewModel.onIntent(BoardIntent.SetBoardCoordinates(newBoardCoordinates))
                val updatedBoardCoordinates = awaitItem().board
                assertThat(updatedBoardCoordinates?.coordinates).isEqualTo(newBoardCoordinates)
                assertThat(updatedBoardCoordinates?.columns[0]?.bodyCoordinates).isEqualTo(
                    firstColumnCoordinates
                )

                boardFlow.emit(secondEmission)
                val updatedColumns = awaitItem().board?.columns

                // Check if the first column still the same
                assertThat(updatedColumns?.get(0)?.bodyCoordinates).isEqualTo(
                    firstColumnCoordinates
                )
                assertThat(updatedColumns?.get(0)?.name).isEqualTo("column test 1")

                // Check if the name was updated and didn't changed the coordinates
                assertThat(updatedColumns?.get(1)?.name).isEqualTo("column updated 2")
                assertThat(updatedColumns?.get(1)?.bodyCoordinates).isEqualTo(
                    secondColumnCoordinates
                )


                cancelAndConsumeRemainingEvents()
                coVerify { observeCompleteBoardUseCase(any()) }
            }
        }
    }

    @Test
    fun `GIVEN empty cards, WHEN new cards is collected, THEN new cards are mapped to CardUi`() {

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
                            title = "card test",
                            description = "card description",
                            position = 1,
                            color = null,
                            createdAt = LocalDateTime.now(),
                            dueDate = null,
                            thumbnailFileName = "thumbnail.png",
                            checklists = emptyList(),
                            attachments = emptyList(),
                            labels = emptyList()
                        )
                    )
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
            Result.Success(
                newBoard
            )
        )

        runTest(testDispatcher) {
            viewModel.uiState.test {
                assertThat(awaitItem().board).isNull()
                viewModel.onIntent(BoardIntent.ObserveBoard(1))

                awaitItem() // Loading state

                val updatedBoard = awaitItem().board
                assertThat(updatedBoard?.id).isEqualTo(1)
                assertThat(updatedBoard?.columns).hasSize(2)
                assertThat(updatedBoard?.columns[0]?.id).isEqualTo(1)
                assertThat(updatedBoard?.columns[0]?.cards).hasSize(1)
                assertThat(updatedBoard?.columns[0]?.cards[0]?.id).isEqualTo(1)

                assertThat(updatedBoard?.columns[1]?.cards).isEmpty()

                cancelAndConsumeRemainingEvents()
                coVerify { observeCompleteBoardUseCase(any()) }
            }
        }
    }

    @Test
    fun `GIVEN existing cards, WHEN existing card is updated, THEN card is updated without changing Ui properties`() {

        val boardFlow = MutableSharedFlow<Result<Board, FetchDataError>>()
        coEvery { observeCompleteBoardUseCase(any()) } returns boardFlow.asSharedFlow()

        val firstEmission = Result.Success(
            Board(
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
                                checklists = emptyList(),
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
                                checklists = emptyList(),
                                attachments = emptyList(),
                                labels = emptyList()
                            )
                        )
                    ),
                    KanbanColumn(
                        id = 2,
                        name = "column test 2",
                        position = 2,
                        color = null,
                        cards = listOf(
                            CardItem(
                                id = 3,
                                title = "card test 3",
                                description = null,
                                position = 1,
                                color = null,
                                createdAt = LocalDateTime.now(),
                                dueDate = null,
                                thumbnailFileName = null,
                                checklists = emptyList(),
                                attachments = emptyList(),
                                labels = emptyList()
                            )
                        )
                    )
                )
            )
        )
        val secondEmission = Result.Success(
            Board(
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
                                description = "updated card",
                                position = 1,
                                color = null,
                                createdAt = LocalDateTime.now(),
                                dueDate = null,
                                thumbnailFileName = null,
                                checklists = emptyList(),
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
                                checklists = emptyList(),
                                attachments = emptyList(),
                                labels = emptyList()
                            )
                        )
                    ),
                    KanbanColumn(
                        id = 2,
                        name = "column updated 2",
                        position = 2,
                        color = null,
                        cards = listOf(
                            CardItem(
                                id = 3,
                                title = "card test 3",
                                description = null,
                                position = 1,
                                color = null,
                                createdAt = LocalDateTime.now(),
                                dueDate = null,
                                thumbnailFileName = null,
                                checklists = emptyList(),
                                attachments = emptyList(),
                                labels = emptyList()
                            )
                        )
                    )
                )
            )
        )

        runTest(testDispatcher) {
            viewModel.uiState.test {

                assertThat(awaitItem().board).isNull()
                viewModel.onIntent(BoardIntent.ObserveBoard(1))
                awaitItem() // Loading state

                boardFlow.emit(firstEmission)
                val firstBoard = awaitItem().board
                assertThat(firstBoard?.id).isEqualTo(1)
                assertThat(firstBoard?.columns).hasSize(2)
                assertThat(firstBoard?.columns?.get(0)?.cards).hasSize(2)
                assertThat(firstBoard?.columns?.get(1)?.cards).hasSize(1)

                val firstColumnCoordinates = Coordinates(50, 60, Offset(x = 100f, y = 300f))
                val secondColumnCoordinates = Coordinates(30, 70, Offset(x = 200f, y = 300f))
                val firstCardCoordinates = Coordinates(20, 40, Offset(x = 100f, y = 200f))
                val secondCardCoordinates = Coordinates(25, 53, Offset(x = 200f, y = 400f))
                val thirdCardCoordinates = Coordinates(44, 22, Offset(x = 300f, y = 600f))

                // Update first column coordinates
                viewModel.onIntent(
                    BoardIntent.SetColumnBodyCoordinates(
                        1,
                        firstColumnCoordinates
                    )
                )
                viewModel.onIntent(BoardIntent.SetCardCoordinates(1, 1, firstCardCoordinates))
                viewModel.onIntent(BoardIntent.SetCardCoordinates(2, 1, secondCardCoordinates))
                assertThat(awaitItem().board?.columns[0]?.bodyCoordinates).isEqualTo(
                    firstColumnCoordinates
                )
                assertThat(awaitItem().board?.columns[0]?.cards?.get(0)?.coordinates).isEqualTo(
                    firstCardCoordinates
                )
                assertThat(awaitItem().board?.columns[0]?.cards?.get(1)?.coordinates).isEqualTo(
                    secondCardCoordinates
                )

                // Update second column coordinates
                viewModel.onIntent(
                    BoardIntent.SetColumnBodyCoordinates(
                        2,
                        secondColumnCoordinates
                    )
                )
                viewModel.onIntent(BoardIntent.SetCardCoordinates(3, 2, thirdCardCoordinates))
                assertThat(awaitItem().board?.columns[1]?.bodyCoordinates).isEqualTo(
                    secondColumnCoordinates
                )
                assertThat(awaitItem().board?.columns[1]?.cards?.get(0)?.coordinates).isEqualTo(
                    thirdCardCoordinates
                )


                boardFlow.emit(secondEmission)
                val updatedColumns = awaitItem().board?.columns

                // Check if first card was updated without change the coordinates
                assertThat(updatedColumns?.get(0)?.cards?.get(0)?.description).isEqualTo("updated card")
                assertThat(updatedColumns?.get(0)?.cards?.get(0)?.coordinates).isEqualTo(
                    firstCardCoordinates
                )
                assertThat(updatedColumns?.get(0)?.cards?.get(1)?.coordinates).isEqualTo(
                    secondCardCoordinates
                )
                assertThat(updatedColumns?.get(0)?.bodyCoordinates).isEqualTo(
                    firstColumnCoordinates
                )

                // Check if second column still the same
                assertThat(updatedColumns?.get(1)?.cards?.get(0)?.coordinates).isEqualTo(
                    thirdCardCoordinates
                )
                assertThat(updatedColumns?.get(1)?.bodyCoordinates).isEqualTo(
                    secondColumnCoordinates
                )


                cancelAndConsumeRemainingEvents()
                coVerify { observeCompleteBoardUseCase(any()) }
            }
        }
    }

}