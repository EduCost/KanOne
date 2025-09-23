package com.educost.kanone.presentation.screens.board

import app.cash.turbine.Turbine
import app.cash.turbine.TurbineContext
import app.cash.turbine.TurbineTestContext
import app.cash.turbine.test
import com.educost.kanone.dispatchers.DispatcherProvider
import com.educost.kanone.dispatchers.TestDispatcherProvider
import com.educost.kanone.domain.model.Board
import com.educost.kanone.domain.model.KanbanColumn
import com.educost.kanone.domain.usecase.CreateCardUseCase
import com.educost.kanone.domain.usecase.CreateColumnUseCase
import com.educost.kanone.domain.usecase.DeleteColumnUseCase
import com.educost.kanone.domain.usecase.ObserveCompleteBoardUseCase
import com.educost.kanone.domain.usecase.ReorderCardsUseCase
import com.educost.kanone.presentation.screens.board.state.BoardState
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

@Suppress("UnusedFlow")
open class BoardViewModelTest {

    protected lateinit var testDispatcher: CoroutineDispatcher
    protected lateinit var dispatcherProvider: DispatcherProvider

    protected lateinit var observeCompleteBoardUseCase: ObserveCompleteBoardUseCase
    protected lateinit var createCardUseCase: CreateCardUseCase
    protected lateinit var createColumnUseCase: CreateColumnUseCase
    protected lateinit var deleteColumnUseCase: DeleteColumnUseCase
    protected lateinit var reorderCardsUseCase: ReorderCardsUseCase

    protected lateinit var viewModel: BoardViewModel


    protected val column1 = KanbanColumn(
        id = 1,
        name = "column1",
        position = 1
    )
    protected val column2 = KanbanColumn(
        id = 2,
        name = "column2",
        position = 2
    )

    protected val defaultBoard = Board(
        id = 1,
        name = "test",
        columns = listOf(column1, column2)
    )

    @OptIn(ExperimentalCoroutinesApi::class)
    @Before
    fun setUp() {
        testDispatcher = UnconfinedTestDispatcher()
        dispatcherProvider = TestDispatcherProvider(testDispatcher)
        observeCompleteBoardUseCase = mockk()
        createColumnUseCase = mockk()
        createCardUseCase = mockk()
        deleteColumnUseCase = mockk()
        reorderCardsUseCase = mockk()

        viewModel = BoardViewModel(
            dispatcherProvider = dispatcherProvider,
            observeCompleteBoardUseCase = observeCompleteBoardUseCase,
            createColumnUseCase = createColumnUseCase,
            createCardUseCase = createCardUseCase,
            updateColumnUseCase = mockk(),
            deleteColumnUseCase = deleteColumnUseCase,
            restoreColumnUseCase = mockk(),
            persistBoardPositionsUseCase = mockk(),
            reorderCardsUseCase = reorderCardsUseCase,
            updateBoardUseCase = mockk(),
            deleteBoardUseCase = mockk()
        )
    }

    fun testBoardViewModelUiState(
        testDispatcher: CoroutineDispatcher = this.testDispatcher,
        board: Board = defaultBoard,
        testSetUp: TestScope.() -> Unit = {},
        testValidate: TestScope.() -> Unit = {},
        testBody: suspend TurbineTestContext<BoardState>.() -> Unit
    ) = runTest(testDispatcher) {

        // SETUP
        coEvery {
            observeCompleteBoardUseCase(board.id)
        } returns flowOf(Result.Success(board))

        testSetUp()

        viewModel.uiState.test {
            viewModel.onIntent(BoardIntent.ObserveBoard(board.id))

            skipItems(3) // Loading state = true  ->  New Board  ->  Loading state = false


            // TEST
            testBody()


            // VALIDATE
            cancelAndConsumeRemainingEvents()
            testValidate()
            coVerify { observeCompleteBoardUseCase(board.id) }
        }
    }

    fun testBoardViewModelSideEffect(
        testDispatcher: CoroutineDispatcher = this.testDispatcher,
        board: Board = defaultBoard,
        testSetUp: TestScope.() -> Unit = {},
        testValidate: TestScope.() -> Unit = {},
        testBody: suspend TurbineTestContext<BoardSideEffect>.() -> Unit
    ) = runTest(testDispatcher) {

        // SETUP
        coEvery {
            observeCompleteBoardUseCase(board.id)
        } returns flowOf(Result.Success(board))

        testSetUp()

        viewModel.sideEffectFlow.test {
            viewModel.onIntent(BoardIntent.ObserveBoard(board.id))


            // TEST
            testBody()


            // VALIDATE
            cancelAndConsumeRemainingEvents()
            testValidate()
            coVerify { observeCompleteBoardUseCase(board.id) }
        }
    }

}