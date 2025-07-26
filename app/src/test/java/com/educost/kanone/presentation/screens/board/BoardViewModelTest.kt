package com.educost.kanone.presentation.screens.board

import androidx.compose.ui.geometry.Offset
import app.cash.turbine.test
import com.educost.kanone.dispatchers.TestDispatcherProvider
import com.educost.kanone.domain.error.FetchDataError
import com.educost.kanone.domain.model.Board
import com.educost.kanone.domain.model.CardItem
import com.educost.kanone.domain.model.KanbanColumn
import com.educost.kanone.domain.repository.BoardRepository
import com.educost.kanone.domain.usecase.ObserveCompleteBoardUseCase
import com.educost.kanone.presentation.model.Coordinates
import com.educost.kanone.presentation.theme.Palette
import com.educost.kanone.utils.Result
import com.google.common.truth.Truth.assertThat
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
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
class BoardViewModelTest {

    private lateinit var repository: BoardRepository
    private lateinit var observeCompleteBoardUseCase: ObserveCompleteBoardUseCase

    @Before
    fun setUp() {
        repository = mockk()
        observeCompleteBoardUseCase = ObserveCompleteBoardUseCase(repository)
    }

    @Test
    fun `GIVEN result success, WHEN board is observed, THEN loading state updates to true then false`() {
        val testDispatcher = UnconfinedTestDispatcher()
        val dispatcherProvider = TestDispatcherProvider(testDispatcher)

        coEvery { repository.observeCompleteBoard(any()) } returns flowOf(
            Result.Success(Board(id = 1, name = "test", emptyList()))
        )

        val viewModel = BoardViewModel(
            dispatcherProvider = dispatcherProvider,
            observeCompleteBoardUseCase = observeCompleteBoardUseCase
        )

        runTest(testDispatcher) {
            viewModel.uiState.test {
                assertThat(awaitItem().isLoading).isFalse()
                viewModel.onIntent(BoardIntent.ObserveBoard(1))
                assertThat(awaitItem().isLoading).isTrue()
                assertThat(awaitItem().isLoading).isFalse()
                cancelAndConsumeRemainingEvents()
                coVerify { repository.observeCompleteBoard(any()) }
            }
        }
    }

    @Test
    fun `GIVEN result error, WHEN board is observed, THEN loading state updates to true then false`() {
        val testDispatcher = UnconfinedTestDispatcher()
        val dispatcherProvider = TestDispatcherProvider(testDispatcher)

        coEvery { repository.observeCompleteBoard(any()) } returns flowOf(
            Result.Error(FetchDataError.UNKNOWN)
        )

        val viewModel = BoardViewModel(
            dispatcherProvider = dispatcherProvider,
            observeCompleteBoardUseCase = observeCompleteBoardUseCase
        )

        runTest(testDispatcher) {
            viewModel.uiState.test {
                assertThat(awaitItem().isLoading).isFalse()
                viewModel.onIntent(BoardIntent.ObserveBoard(1))
                assertThat(awaitItem().isLoading).isTrue()
                assertThat(awaitItem().isLoading).isFalse()
                cancelAndConsumeRemainingEvents()
                coVerify { repository.observeCompleteBoard(any()) }
            }
        }
    }

    @Test
    fun `GIVEN null board, WHEN new board is observed, THEN new board is mapped to BoardUi`() {
        val testDispatcher = UnconfinedTestDispatcher()
        val dispatcherProvider = TestDispatcherProvider(testDispatcher)

        coEvery { repository.observeCompleteBoard(any()) } returns flowOf(
            Result.Success(Board(id = 1, name = "test", emptyList()))
        )

        val viewModel = BoardViewModel(
            dispatcherProvider = dispatcherProvider,
            observeCompleteBoardUseCase = observeCompleteBoardUseCase
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
                coVerify { repository.observeCompleteBoard(any()) }
            }
        }
    }

    @Test
    fun `GIVEN empty columns, WHEN new columns is collected, THEN new columns are mapped to ColumnUi`() {
        val testDispatcher = UnconfinedTestDispatcher()
        val dispatcherProvider = TestDispatcherProvider(testDispatcher)

        val newBoard = Board(
            id = 1,
            name = "board test",
            columns = listOf(
                KanbanColumn(
                    id = 1,
                    name = "column test 1",
                    position = 1,
                    color = Palette.NONE,
                    cards = emptyList()
                ),
                KanbanColumn(
                    id = 2,
                    name = "column test 2",
                    position = 2,
                    color = Palette.NONE,
                    cards = emptyList()
                )
            )
        )

        coEvery { repository.observeCompleteBoard(any()) } returns flowOf(Result.Success(newBoard))

        val viewModel = BoardViewModel(
            dispatcherProvider = dispatcherProvider,
            observeCompleteBoardUseCase = observeCompleteBoardUseCase
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
                coVerify { repository.observeCompleteBoard(any()) }
            }
        }
    }

    @Test
    fun `GIVEN empty cards, WHEN new cards is collected, THEN new cards are mapped to CardUi`() {
        val testDispatcher = UnconfinedTestDispatcher()
        val dispatcherProvider = TestDispatcherProvider(testDispatcher)

        val newBoard = Board(
            id = 1,
            name = "board test",
            columns = listOf(
                KanbanColumn(
                    id = 1,
                    name = "column test 1",
                    position = 1,
                    color = Palette.NONE,
                    cards = listOf(
                        CardItem(
                            id = 1,
                            title = "card test",
                            description = "card description",
                            position = 1,
                            color = Palette.NONE,
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
                    color = Palette.NONE,
                    cards = emptyList()
                )
            )
        )

        coEvery { repository.observeCompleteBoard(any()) } returns flowOf(Result.Success(newBoard))

        val viewModel = BoardViewModel(
            dispatcherProvider = dispatcherProvider,
            observeCompleteBoardUseCase = observeCompleteBoardUseCase
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
                coVerify { repository.observeCompleteBoard(any()) }
            }
        }
    }

    @Test
    fun `GIVEN existing board, WHEN new board is observed, THEN previous board is updated without changing Ui properties`() {
        val testDispatcher = UnconfinedTestDispatcher()
        val dispatcherProvider = TestDispatcherProvider(testDispatcher)

        val boardFlow = MutableSharedFlow<Result<Board, FetchDataError>>()
        coEvery { repository.observeCompleteBoard(any()) } returns boardFlow.asSharedFlow()

        val viewModel = BoardViewModel(
            dispatcherProvider = dispatcherProvider,
            observeCompleteBoardUseCase = observeCompleteBoardUseCase
        )

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
                coVerify { repository.observeCompleteBoard(any()) }
            }
        }
    }

    @Test
    fun `GIVEN existing columns, WHEN existing column is updated, THEN column is updated without changing Ui properties`() {
        val testDispatcher = UnconfinedTestDispatcher()
        val dispatcherProvider = TestDispatcherProvider(testDispatcher)

        val boardFlow = MutableSharedFlow<Result<Board, FetchDataError>>()
        coEvery { repository.observeCompleteBoard(any()) } returns boardFlow.asSharedFlow()

        val viewModel = BoardViewModel(
            dispatcherProvider = dispatcherProvider,
            observeCompleteBoardUseCase = observeCompleteBoardUseCase
        )

        val firstEmission = Result.Success(
            Board(
                id = 1,
                name = "board test",
                columns = listOf(
                    KanbanColumn(
                        id = 1,
                        name = "column test 1",
                        position = 1,
                        color = Palette.NONE,
                        cards = emptyList()
                    ),
                    KanbanColumn(
                        id = 2,
                        name = "column test 2",
                        position = 2,
                        color = Palette.NONE,
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
                        color = Palette.NONE,
                        cards = emptyList()
                    ),
                    KanbanColumn(
                        id = 2,
                        name = "column updated 2",
                        position = 2,
                        color = Palette.NONE,
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

                viewModel.onIntent(BoardIntent.SetColumnBodyCoordinates(1, firstColumnCoordinates))
                assertThat(awaitItem().board?.columns[0]?.bodyCoordinates).isEqualTo(
                    firstColumnCoordinates
                )

                viewModel.onIntent(BoardIntent.SetColumnBodyCoordinates(2, secondColumnCoordinates))
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
                assertThat(updatedColumns?.get(0)?.bodyCoordinates).isEqualTo(firstColumnCoordinates)
                assertThat(updatedColumns?.get(0)?.name).isEqualTo("column test 1")

                // Check if the name was updated and didn't changed the coordinates
                assertThat(updatedColumns?.get(1)?.name).isEqualTo("column updated 2")
                assertThat(updatedColumns?.get(1)?.bodyCoordinates).isEqualTo(
                    secondColumnCoordinates
                )


                cancelAndConsumeRemainingEvents()
                coVerify { repository.observeCompleteBoard(any()) }
            }
        }
    }

    @Test
    fun `GIVEN existing cards, WHEN existing card is updated, THEN card is updated without changing Ui properties`() {
        val testDispatcher = UnconfinedTestDispatcher()
        val dispatcherProvider = TestDispatcherProvider(testDispatcher)

        val boardFlow = MutableSharedFlow<Result<Board, FetchDataError>>()
        coEvery { repository.observeCompleteBoard(any()) } returns boardFlow.asSharedFlow()

        val viewModel = BoardViewModel(
            dispatcherProvider = dispatcherProvider,
            observeCompleteBoardUseCase = observeCompleteBoardUseCase
        )

        val firstEmission = Result.Success(
            Board(
                id = 1,
                name = "board test",
                columns = listOf(
                    KanbanColumn(
                        id = 1,
                        name = "column test 1",
                        position = 1,
                        color = Palette.NONE,
                        cards = listOf(
                            CardItem(
                                id = 1,
                                title = "card test 1",
                                description = null,
                                position = 1,
                                color = Palette.NONE,
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
                                color = Palette.NONE,
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
                        color = Palette.NONE,
                        cards = listOf(
                            CardItem(
                                id = 3,
                                title = "card test 3",
                                description = null,
                                position = 1,
                                color = Palette.NONE,
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
                        color = Palette.NONE,
                        cards = listOf(
                            CardItem(
                                id = 1,
                                title = "card test 1",
                                description = "updated card",
                                position = 1,
                                color = Palette.NONE,
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
                                color = Palette.NONE,
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
                        color = Palette.NONE,
                        cards = listOf(
                            CardItem(
                                id = 3,
                                title = "card test 3",
                                description = null,
                                position = 1,
                                color = Palette.NONE,
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
                viewModel.onIntent(BoardIntent.SetColumnBodyCoordinates(1, firstColumnCoordinates))
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
                viewModel.onIntent(BoardIntent.SetColumnBodyCoordinates(2, secondColumnCoordinates))
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
                assertThat(updatedColumns?.get(0)?.bodyCoordinates).isEqualTo(firstColumnCoordinates)

                // Check if second column still the same
                assertThat(updatedColumns?.get(1)?.cards?.get(0)?.coordinates).isEqualTo(
                    thirdCardCoordinates
                )
                assertThat(updatedColumns?.get(1)?.bodyCoordinates).isEqualTo(
                    secondColumnCoordinates
                )


                cancelAndConsumeRemainingEvents()
                coVerify { repository.observeCompleteBoard(any()) }
            }
        }
    }

    @Test
    fun `GIVEN new board coordinates, WHEN SetBoardCoordinates intent is processed , THEN coordinates are updated`() {
        val testDispatcher = UnconfinedTestDispatcher()
        val dispatcherProvider = TestDispatcherProvider(testDispatcher)

        coEvery { repository.observeCompleteBoard(any()) } returns flowOf(
            Result.Success(Board(id = 1, name = "test", emptyList()))
        )

        val viewModel = BoardViewModel(
            dispatcherProvider = dispatcherProvider,
            observeCompleteBoardUseCase = observeCompleteBoardUseCase
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
                coVerify { repository.observeCompleteBoard(any()) }
            }
        }
    }

    @Test
    fun `GIVEN new column header coordinates, WHEN SetColumnHeaderCoordinates intent is processed , THEN coordinates are updated`() {
        val testDispatcher = UnconfinedTestDispatcher()
        val dispatcherProvider = TestDispatcherProvider(testDispatcher)

        val newBoard = Board(
            id = 1,
            name = "board test",
            columns = listOf(
                KanbanColumn(
                    id = 1,
                    name = "column test 1",
                    position = 1,
                    color = Palette.NONE,
                    cards = emptyList()
                ),
                KanbanColumn(
                    id = 2,
                    name = "column test 2",
                    position = 2,
                    color = Palette.NONE,
                    cards = emptyList()
                )
            )
        )

        coEvery { repository.observeCompleteBoard(any()) } returns flowOf(
            Result.Success(newBoard)
        )

        val viewModel = BoardViewModel(
            dispatcherProvider = dispatcherProvider,
            observeCompleteBoardUseCase = observeCompleteBoardUseCase
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
                coVerify { repository.observeCompleteBoard(any()) }
            }
        }
    }

    @Test
    fun `GIVEN new column body coordinates, WHEN SetColumnBodyCoordinates intent is processed , THEN coordinates are updated`() {
        val testDispatcher = UnconfinedTestDispatcher()
        val dispatcherProvider = TestDispatcherProvider(testDispatcher)

        val newBoard = Board(
            id = 1,
            name = "board test",
            columns = listOf(
                KanbanColumn(
                    id = 1,
                    name = "column test 1",
                    position = 1,
                    color = Palette.NONE,
                    cards = emptyList()
                ),
                KanbanColumn(
                    id = 2,
                    name = "column test 2",
                    position = 2,
                    color = Palette.NONE,
                    cards = emptyList()
                )
            )
        )

        coEvery { repository.observeCompleteBoard(any()) } returns flowOf(
            Result.Success(newBoard)
        )

        val viewModel = BoardViewModel(
            dispatcherProvider = dispatcherProvider,
            observeCompleteBoardUseCase = observeCompleteBoardUseCase
        )



        runTest(testDispatcher) {
            viewModel.uiState.test {
                assertThat(awaitItem().board).isNull()
                viewModel.onIntent(BoardIntent.ObserveBoard(1))
                awaitItem() // Loading state

                val firstColumns = awaitItem().board?.columns
                assertThat(firstColumns?.get(0)?.bodyCoordinates).isEqualTo(
                    Coordinates()
                )
                assertThat(firstColumns?.get(1)?.bodyCoordinates).isEqualTo(
                    Coordinates()
                )

                val newCoordinates = Coordinates(10, 20, Offset(x = 100f, y = 200f))
                viewModel.onIntent(BoardIntent.SetColumnBodyCoordinates(1, newCoordinates))

                val updatedColumns = awaitItem().board?.columns
                assertThat(updatedColumns?.get(0)?.bodyCoordinates).isEqualTo(
                    newCoordinates
                )
                assertThat(updatedColumns?.get(1)?.bodyCoordinates).isEqualTo(
                    Coordinates()
                )


                cancelAndConsumeRemainingEvents()
                coVerify { repository.observeCompleteBoard(any()) }
            }
        }
    }

    @Test
    fun `GIVEN new card coordinates, WHEN SetCardCoordinates intent is processed , THEN coordinates are updated`() {
        val testDispatcher = UnconfinedTestDispatcher()
        val dispatcherProvider = TestDispatcherProvider(testDispatcher)

        val newBoard = Board(
            id = 1,
            name = "board test",
            columns = listOf(
                KanbanColumn(
                    id = 1,
                    name = "column test 1",
                    position = 1,
                    color = Palette.NONE,
                    cards = listOf(
                        CardItem(
                            id = 1,
                            title = "card test 1",
                            description = null,
                            position = 1,
                            color = Palette.NONE,
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
                            color = Palette.NONE,
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

        coEvery { repository.observeCompleteBoard(any()) } returns flowOf(
            Result.Success(newBoard)
        )

        val viewModel = BoardViewModel(
            dispatcherProvider = dispatcherProvider,
            observeCompleteBoardUseCase = observeCompleteBoardUseCase
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
                viewModel.onIntent(BoardIntent.SetCardCoordinates(1,1, newCoordinates))

                val updatedCards = awaitItem().board?.columns?.get(0)?.cards
                assertThat(updatedCards?.get(0)?.coordinates).isEqualTo(
                    newCoordinates
                )
                assertThat(updatedCards?.get(1)?.coordinates).isEqualTo(
                    Coordinates()
                )


                cancelAndConsumeRemainingEvents()
                coVerify { repository.observeCompleteBoard(any()) }
            }
        }
    }
}