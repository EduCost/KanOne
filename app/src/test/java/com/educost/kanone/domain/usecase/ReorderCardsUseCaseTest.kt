package com.educost.kanone.domain.usecase

import com.educost.kanone.domain.model.CardItem
import com.educost.kanone.domain.model.KanbanColumn
import com.educost.kanone.domain.repository.CardRepository
import com.educost.kanone.presentation.screens.board.utils.CardOrder
import com.educost.kanone.presentation.screens.board.utils.OrderType
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.time.LocalDateTime

class ReorderCardsUseCaseTest {

    private lateinit var reorderCardsUseCase: ReorderCardsUseCase
    private lateinit var cardRepository: CardRepository

    private val card1 = CardItem(
        id = 1,
        title = "Banana",
        dueDate = LocalDateTime.now().plusDays(1),
        createdAt = LocalDateTime.now().minusHours(2),
        position = 0,
    )
    private val card2 = CardItem(
        id = 2,
        title = "Apple",
        dueDate = LocalDateTime.now().plusDays(2),
        createdAt = LocalDateTime.now().minusHours(1),
        position = 1,
    )
    private val card3 = CardItem(
        id = 3,
        title = "Cherry",
        dueDate = LocalDateTime.now().plusDays(0),
        createdAt = LocalDateTime.now().minusHours(3),
        position = 2,
    )

    private val unsortedCards = listOf(card1, card2, card3)
    private val column = KanbanColumn(
        id = 1,
        name = "Test Column",
        position = 0,
        cards = unsortedCards
    )

    @Before
    fun setUp() {
        cardRepository = mockk()
        reorderCardsUseCase = ReorderCardsUseCase(cardRepository)
    }

    @Test
    fun `SHOULD sort by name in ascending order WHEN reorder cards`() = runTest {

        val expectedSortedCards = listOf(
            card2.copy(position = 0), // Apple
            card1.copy(position = 1), // Banana
            card3.copy(position = 2)  // Cherry
        )
        coEvery {
            cardRepository.updateCards(
                cards = expectedSortedCards,
                columnId = column.id
            )
        } returns true

        val wasReordered = reorderCardsUseCase(column, OrderType.ASCENDING, CardOrder.NAME)

        assertTrue(wasReordered)
        coVerify(exactly = 1) {
            cardRepository.updateCards(
                cards = expectedSortedCards,
                columnId = column.id
            )
        }
    }

    @Test
    fun `SHOULD sort by due date in ascending order WHEN reorder cards`() = runTest {

        val expectedSortedCards = listOf(
            card3.copy(position = 0), // day + 0
            card1.copy(position = 1), // day + 1
            card2.copy(position = 2)  // day + 2
        )
        coEvery {
            cardRepository.updateCards(
                cards = expectedSortedCards,
                columnId = column.id
            )
        } returns true

        val wasReordered = reorderCardsUseCase(column, OrderType.ASCENDING, CardOrder.DUE_DATE)

        assertTrue(wasReordered)
        coVerify(exactly = 1) {
            cardRepository.updateCards(
                cards = expectedSortedCards,
                columnId = column.id
            )
        }
    }

    @Test
    fun `SHOULD sort by date created in ascending order WHEN reorder cards`() = runTest {

        val expectedSortedCards = listOf(
            card3.copy(position = 0), // hour - 3
            card1.copy(position = 1), // hour - 2
            card2.copy(position = 2)  // hour - 1
        )
        coEvery {
            cardRepository.updateCards(
                cards = expectedSortedCards,
                columnId = column.id
            )
        } returns true

        val wasReordered = reorderCardsUseCase(column, OrderType.ASCENDING, CardOrder.DATE_CREATED)

        assertTrue(wasReordered)
        coVerify(exactly = 1) {
            cardRepository.updateCards(
                cards = expectedSortedCards,
                columnId = column.id
            )
        }
    }

    @Test
    fun `SHOULD sort by name in descending order WHEN reorder cards`() = runTest {

        val expectedSortedCards = listOf(
            card3.copy(position = 0), // Cherry
            card1.copy(position = 1), // Banana
            card2.copy(position = 2)  // Apple
        )
        coEvery {
            cardRepository.updateCards(
                cards = expectedSortedCards,
                columnId = column.id
            )
        } returns true

        val wasReordered = reorderCardsUseCase(column, OrderType.DESCENDING, CardOrder.NAME)

        assertTrue(wasReordered)
        coVerify(exactly = 1) {
            cardRepository.updateCards(
                cards = expectedSortedCards,
                columnId = column.id
            )
        }
    }

    @Test
    fun `SHOULD sort by due date in descending order WHEN reorder cards`() = runTest {

        val expectedSortedCards = listOf(
            card2.copy(position = 0), // day + 2
            card1.copy(position = 1), // day + 1
            card3.copy(position = 2)  // day + 0
        )
        coEvery {
            cardRepository.updateCards(
                cards = expectedSortedCards,
                columnId = column.id
            )
        } returns true

        val wasReordered = reorderCardsUseCase(column, OrderType.DESCENDING, CardOrder.DUE_DATE)

        assertTrue(wasReordered)
        coVerify(exactly = 1) {
            cardRepository.updateCards(
                cards = expectedSortedCards,
                columnId = column.id
            )
        }
    }

    @Test
    fun `SHOULD sort by date created in descending order WHEN reorder cards`() = runTest {

        val expectedSortedCards = listOf(
            card2.copy(position = 0), // hour - 1
            card1.copy(position = 1), // hour - 2
            card3.copy(position = 2)  // hour - 3
        )
        coEvery {
            cardRepository.updateCards(
                cards = expectedSortedCards,
                columnId = column.id
            )
        } returns true

        val wasReordered = reorderCardsUseCase(column, OrderType.DESCENDING, CardOrder.DATE_CREATED)

        assertTrue(wasReordered)
        coVerify(exactly = 1) {
            cardRepository.updateCards(
                cards = expectedSortedCards,
                columnId = column.id
            )
        }
    }

    @Test
    fun `SHOULD not call card repository WHEN card list is empty`() = runTest {

        val columnWithNoCards = column.copy(cards = emptyList())
        coEvery {
            cardRepository.updateCards(
                cards = any(),
                columnId = column.id
            )
        } returns true

        val wasReordered = reorderCardsUseCase(columnWithNoCards, OrderType.ASCENDING, CardOrder.NAME)

        assertTrue(wasReordered)
        coVerify(exactly = 0) {
            cardRepository.updateCards(
                cards = any(),
                columnId = column.id
            )
        }
    }

    @Test
    fun `SHOULD place null due dates at the bottom WHEN sorting by due date ascending`() = runTest {

        val columnWithNullDueDate = column.copy(
            cards = listOf(
                card1.copy(dueDate = null),
                card2,
                card3
            )
        )
        val expectedSortedCards = listOf(
            card3.copy(position = 0),                  // day + 0
            card2.copy(position = 1),                  // day + 2
            card1.copy(position = 2, dueDate = null)   // null
        )
        coEvery {
            cardRepository.updateCards(
                cards = expectedSortedCards,
                columnId = column.id
            )
        } returns true

        val wasReordered = reorderCardsUseCase(
            column = columnWithNullDueDate,
            orderType = OrderType.ASCENDING,
            cardOrder = CardOrder.DUE_DATE
        )

        assertTrue(wasReordered)
        coVerify(exactly = 1) {
            cardRepository.updateCards(
                cards = expectedSortedCards,
                columnId = column.id
            )
        }
    }

    @Test
    fun `SHOULD place null due dates at the bottom WHEN sorting by due date descending`() = runTest {

        val columnWithNullDueDate = column.copy(
            cards = listOf(
                card1.copy(dueDate = null),
                card2,
                card3
            )
        )

        val expectedSortedCards = listOf(
            card2.copy(position = 0),                  // day + 2
            card3.copy(position = 1),                  // day + 0
            card1.copy(position = 2, dueDate = null)   // null
        )
        coEvery {
            cardRepository.updateCards(
                cards = expectedSortedCards,
                columnId = column.id
            )
        } returns true

        val wasReordered = reorderCardsUseCase(
            column = columnWithNullDueDate,
            orderType = OrderType.DESCENDING,
            cardOrder = CardOrder.DUE_DATE
        )

        assertTrue(wasReordered)
        coVerify(exactly = 1) {
            cardRepository.updateCards(
                cards = expectedSortedCards,
                columnId = column.id
            )
        }
    }

    @Test
    fun `SHOULD return failure WHEN cardRepository updateCards fails`() = runTest {

        val expectedSortedCards = listOf(
            card3.copy(position = 0), // day + 0
            card1.copy(position = 1), // day + 1
            card2.copy(position = 2)  // day + 2
        )
        coEvery {
            cardRepository.updateCards(
                cards = expectedSortedCards,
                columnId = column.id
            )
        } returns false

        val wasReordered = reorderCardsUseCase(column, OrderType.ASCENDING, CardOrder.DUE_DATE)

        assertTrue(!wasReordered)
        coVerify(exactly = 1) {
            cardRepository.updateCards(
                cards = expectedSortedCards,
                columnId = column.id
            )
        }
    }

    @Test
    fun `SHOULD handle mixed casing in title WHEN sorting by name ascending`() = runTest {

        val columnWithMixedCasing = column.copy(
            cards = listOf(
                card1.copy(title = "banana"),
                card2,
                card3
            )
        )
        val expectedSortedCards = listOf(
            card2.copy(position = 0),                   // Apple
            card1.copy(position = 1, title = "banana"), // banana
            card3.copy(position = 2)                    // Cherry
        )
        coEvery {
            cardRepository.updateCards(
                cards = expectedSortedCards,
                columnId = column.id
            )
        } returns true

        val wasReordered = reorderCardsUseCase(columnWithMixedCasing, OrderType.ASCENDING, CardOrder.NAME)

        assertTrue(wasReordered)
        coVerify(exactly = 1) {
            cardRepository.updateCards(
                cards = expectedSortedCards,
                columnId = column.id
            )
        }
    }

    @Test
    fun `SHOULD handle mixed casing in title WHEN sorting by name descending`() = runTest {

        val columnWithMixedCasing = column.copy(
            cards = listOf(
                card1.copy(title = "banana"),
                card2,
                card3
            )
        )
        val expectedSortedCards = listOf(
            card3.copy(position = 0),                   // Cherry
            card1.copy(position = 1, title = "banana"), // banana
            card2.copy(position = 2)                    // Apple
        )
        coEvery {
            cardRepository.updateCards(
                cards = expectedSortedCards,
                columnId = column.id
            )
        } returns true

        val wasReordered = reorderCardsUseCase(columnWithMixedCasing, OrderType.DESCENDING, CardOrder.NAME)

        assertTrue(wasReordered)
        coVerify(exactly = 1) {
            cardRepository.updateCards(
                cards = expectedSortedCards,
                columnId = column.id
            )
        }
    }

}