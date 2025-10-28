package com.educost.kanone.presentation.screens.board

import app.cash.turbine.TurbineTestContext
import app.cash.turbine.test
import com.educost.kanone.dispatchers.DispatcherProvider
import com.educost.kanone.dispatchers.TestDispatcherProvider
import com.educost.kanone.domain.model.Board
import com.educost.kanone.domain.model.CardItem
import com.educost.kanone.domain.model.KanbanColumn
import com.educost.kanone.domain.usecase.CreateCardUseCase
import com.educost.kanone.domain.usecase.CreateColumnUseCase
import com.educost.kanone.domain.usecase.DeleteBoardUseCase
import com.educost.kanone.domain.usecase.DeleteColumnUseCase
import com.educost.kanone.domain.usecase.ObserveCompleteBoardUseCase
import com.educost.kanone.domain.usecase.PersistBoardPositionsUseCase
import com.educost.kanone.domain.usecase.ReorderCardsUseCase
import com.educost.kanone.domain.usecase.RestoreColumnUseCase
import com.educost.kanone.domain.usecase.UpdateBoardUseCase
import com.educost.kanone.domain.usecase.UpdateColumnUseCase
import com.educost.kanone.presentation.screens.board.state.BoardUiState
import com.educost.kanone.utils.Result
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Before
import java.time.LocalDateTime

@OptIn(ExperimentalCoroutinesApi::class)
@Suppress("UnusedFlow")
open class BoardViewModelTest {

    // TEST DISPATCHERS
    protected lateinit var testDispatcher: CoroutineDispatcher
    protected lateinit var dispatcherProvider: DispatcherProvider

    // USE CASES
    protected lateinit var observeCompleteBoardUseCase: ObserveCompleteBoardUseCase
    protected lateinit var createColumnUseCase: CreateColumnUseCase
    protected lateinit var createCardUseCase: CreateCardUseCase
    protected lateinit var updateColumnUseCase: UpdateColumnUseCase
    protected lateinit var deleteColumnUseCase: DeleteColumnUseCase
    protected lateinit var restoreColumnUseCase: RestoreColumnUseCase
    protected lateinit var persistBoardPositionsUseCase: PersistBoardPositionsUseCase
    protected lateinit var reorderCardsUseCase: ReorderCardsUseCase
    protected lateinit var updateBoardUseCase: UpdateBoardUseCase
    protected lateinit var deleteBoardUseCase: DeleteBoardUseCase


    // VIEW MODEL
    protected lateinit var viewModel: BoardViewModel


    // DEFAULT BOARD
    private val defaultCard1OfColumn1 = CardItem(
        id = 1,
        title = "card1",
        position = 1,
        createdAt = LocalDateTime.now()
    )
    private val defaultCard2OfColumn1 = CardItem(
        id = 2,
        title = "card2",
        position = 2,
        createdAt = LocalDateTime.now()
    )
    private val defaultColumn1 = KanbanColumn(
        id = 1,
        name = "column1",
        position = 1,
        cards = listOf(defaultCard1OfColumn1, defaultCard2OfColumn1)
    )
    private val defaultCard1OfColumn2 = CardItem(
        id = 3,
        title = "card3",
        position = 1,
        createdAt = LocalDateTime.now()
    )
    private val defaultCard2OfColumn2 = CardItem(
        id = 4,
        title = "card4",
        position = 2,
        createdAt = LocalDateTime.now()
    )
    private val defaultColumn2 = KanbanColumn(
        id = 2,
        name = "column2",
        position = 2,
        cards = listOf(defaultCard1OfColumn2, defaultCard2OfColumn2)
    )
    private val defaultBoard = Board(
        id = 1,
        name = "test",
        columns = listOf(defaultColumn1, defaultColumn2)
    )


    protected lateinit var firstColumn: KanbanColumn
    protected lateinit var firstCardOfFirstColumn: CardItem
    protected lateinit var secondCardOfFirstColumn: CardItem
    protected lateinit var secondColumn: KanbanColumn
    protected lateinit var firstCardOfSecondColumn: CardItem
    protected lateinit var secondCardOfSecondColumn: CardItem
    protected lateinit var board: Board


    @Before
    fun setUp() {
        // Test dispatchers
        testDispatcher = UnconfinedTestDispatcher()
        dispatcherProvider = TestDispatcherProvider(testDispatcher)

        // Use cases
        observeCompleteBoardUseCase = mockk()
        createColumnUseCase = mockk()
        createCardUseCase = mockk()
        updateColumnUseCase = mockk()
        deleteColumnUseCase = mockk()
        restoreColumnUseCase = mockk()
        persistBoardPositionsUseCase = mockk()
        reorderCardsUseCase = mockk()
        updateBoardUseCase = mockk()
        deleteBoardUseCase = mockk()

        // Default board
        firstColumn = defaultColumn1
        firstCardOfFirstColumn = defaultCard1OfColumn1
        secondCardOfFirstColumn = defaultCard2OfColumn1
        secondColumn = defaultColumn2
        secondCardOfSecondColumn = defaultCard2OfColumn2
        firstCardOfSecondColumn = defaultCard1OfColumn2
        board = defaultBoard

        // View model
        viewModel = BoardViewModel(
            dispatchers = dispatcherProvider,
            observeCompleteBoardUseCase = observeCompleteBoardUseCase,
            createColumnUseCase = createColumnUseCase,
            createCardUseCase = createCardUseCase,
            updateColumnUseCase = updateColumnUseCase,
            deleteColumnUseCase = deleteColumnUseCase,
            restoreColumnUseCase = restoreColumnUseCase,
            persistBoardPositionsUseCase = persistBoardPositionsUseCase,
            reorderCardsUseCase = reorderCardsUseCase,
            updateBoardUseCase = updateBoardUseCase,
            deleteBoardUseCase = deleteBoardUseCase
        )
    }


    fun testBoardViewModelUiState(
        testDispatcher: CoroutineDispatcher = this.testDispatcher,
        given: TestScope.() -> Unit = {},
        whenAction: suspend TurbineTestContext<BoardUiState>.() -> Unit,
        then: suspend TurbineTestContext<BoardUiState>.() -> Unit
    ) = runTest(testDispatcher) {

        // GIVEN
        given()

        coEvery {
            observeCompleteBoardUseCase(board.id)
        } returns flowOf(Result.Success(board))


        // WHEN
        viewModel.uiState.test {
            viewModel.onIntent(BoardIntent.ObserveBoard(board.id))
            skipItems(2)

            whenAction()


            // THEN
            then()
            cancelAndConsumeRemainingEvents()
            coVerify { observeCompleteBoardUseCase(board.id) }
        }
    }


    fun testBoardViewModelSideEffect(
        testDispatcher: CoroutineDispatcher = this.testDispatcher,
        board: Board = defaultBoard,
        given: TestScope.() -> Unit = {},
        whenAction: suspend TurbineTestContext<BoardSideEffect>.() -> Unit,
        then: suspend TurbineTestContext<BoardSideEffect>.() -> Unit
    ) = runTest(testDispatcher) {

        // GIVEN
        given()

        coEvery {
            observeCompleteBoardUseCase(board.id)
        } returns flowOf(Result.Success(board))


        // WHEN
        viewModel.sideEffectFlow.test {
            viewModel.onIntent(BoardIntent.ObserveBoard(board.id))

            whenAction()


            // THEN
            then()
            cancelAndConsumeRemainingEvents()
            coVerify { observeCompleteBoardUseCase(board.id) }
        }
    }

}