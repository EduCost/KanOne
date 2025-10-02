package com.educost.kanone.domain.usecase

import com.educost.kanone.domain.model.CardItem
import com.educost.kanone.domain.model.KanbanColumn
import com.educost.kanone.domain.repository.BoardRepository
import com.google.common.truth.Truth.assertThat
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import java.time.LocalDateTime

class PersistBoardPositionsUseCaseTest {

    private lateinit var boardRepository: BoardRepository
    private lateinit var persistBoardPositionsUseCase: PersistBoardPositionsUseCase


    @Before
    fun setUp() {
        boardRepository = mockk()
        persistBoardPositionsUseCase = PersistBoardPositionsUseCase(boardRepository)
    }


    @Test
    fun `SHOULD update position correctly WHEN invoke is called`() = runTest {
        val boardId = 1L

        /*  Called  */
        // First Column
        val firstCardOfFirstColumn = CardItem(
            id = 1,
            title = "Card 1",
            position = 1,
            createdAt = LocalDateTime.now()
        )
        val secondCardOfFirstColumn = CardItem(
            id = 2,
            title = "Card 2",
            position = 0,
            createdAt = LocalDateTime.now()
        )
        val firstColumn = KanbanColumn(
            id = 1,
            name = "Column 1",
            cards = listOf(firstCardOfFirstColumn, secondCardOfFirstColumn),
            position = 1
        )

        // Second Column
        val firstCardOfSecondColumn = CardItem(
            id = 3,
            title = "Card 3",
            position = 1,
            createdAt = LocalDateTime.now()
        )
        val secondCardOfSecondColumn = CardItem(
            id = 4,
            title = "Card 4",
            position = 0,
            createdAt = LocalDateTime.now()
        )
        val secondColumn = KanbanColumn(
            id = 2,
            name = "Column 2",
            cards = listOf(firstCardOfSecondColumn, secondCardOfSecondColumn),
            position = 0
        )

        val columns = listOf(firstColumn, secondColumn)
        /*  Called  */

        /*  Expected  */
        // First Column
        val expectedFirstCardOfFirstColumn = firstCardOfFirstColumn.copy(position = 0)
        val expectedSecondCardOfFirstColumn = secondCardOfFirstColumn.copy(position = 1)
        val expectedFirstColumn = firstColumn.copy(
            position = 0,
            cards = listOf(
                expectedFirstCardOfFirstColumn,
                expectedSecondCardOfFirstColumn
            )
        )

        // Second Column
        val expectedFirstCardOfSecondColumn = firstCardOfSecondColumn.copy(position = 0)
        val expectedSecondCardOfSecondColumn = secondCardOfSecondColumn.copy(position = 1)
        val expectedSecondColumn = secondColumn.copy(
            position = 1,
            cards = listOf(
                expectedFirstCardOfSecondColumn,
                expectedSecondCardOfSecondColumn
            )
        )

        val expectedColumns = listOf(expectedFirstColumn, expectedSecondColumn)
        /*  Expected  */


        // GIVEN
        coEvery { boardRepository.updateBoardData(boardId, expectedColumns) } returns true

        // WHEN
        val result = persistBoardPositionsUseCase(boardId, columns)

        // THAN
        assertThat(result).isTrue()
        coVerify(exactly = 1) { boardRepository.updateBoardData(boardId, expectedColumns) }
    }


    @Test
    fun `SHOULD return false WHEN board repository returns false`() = runTest {
        // GIVEN
        val boardId = 1L
        val columns = listOf(
            KanbanColumn(
                id = 1,
                name = "Column 1",
                cards = listOf(
                    CardItem(
                        id = 1,
                        title = "Card 1",
                        position = 1,
                        createdAt = LocalDateTime.now()
                    )
                ),
                position = 1
            )
        )
        coEvery { boardRepository.updateBoardData(any(), any()) } returns false

        // WHEN
        val result = persistBoardPositionsUseCase(boardId, columns)

        // THAN
        assertThat(result).isFalse()
        coVerify(exactly = 1) { boardRepository.updateBoardData(any(), any()) }
    }

}