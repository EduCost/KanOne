package com.educost.kanone.domain.usecase

import com.educost.kanone.domain.error.GenericError
import com.educost.kanone.domain.model.CardItem
import com.educost.kanone.domain.repository.CardRepository
import com.educost.kanone.utils.Result
import com.google.common.truth.Truth.assertThat
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import java.time.LocalDateTime

class UpdateCardUseCaseTest {

    private lateinit var updateCardUseCase: UpdateCardUseCase
    private lateinit var cardRepository: CardRepository

    private val testCard = CardItem(
        id = 1L,
        title = "Test Card",
        description = "",
        color = null,
        createdAt = LocalDateTime.now(),
        position = 0
    )
    private val testColumnId = 10L

    @Before
    fun setUp() {
        cardRepository = mockk()
        updateCardUseCase = UpdateCardUseCase(cardRepository)
    }

    @Test
    fun `SHOULD return false WHEN getCardColumnId returns error`() = runTest {
        // GIVEN
        coEvery { cardRepository.getCardColumnId(testCard.id) } returns Result.Error(GenericError)
        coEvery { cardRepository.updateCard(any(), any()) } returns true

        // WHEN
        val result = updateCardUseCase(testCard)

        // THEN
        assertThat(result).isFalse()
        coVerify(exactly = 1) { cardRepository.getCardColumnId(testCard.id) }
        coVerify(exactly = 0) { cardRepository.updateCard(any(), any()) }
    }

    @Test
    fun `SHOULD return false WHEN updateCard returns false`() = runTest {
        // GIVEN
        coEvery { cardRepository.getCardColumnId(testCard.id) } returns Result.Success(testColumnId)
        coEvery { cardRepository.updateCard(testCard, testColumnId) } returns false

        // WHEN
        val result = updateCardUseCase(testCard)

        // THEN
        assertThat(result).isFalse()
        coVerify(exactly = 1) { cardRepository.getCardColumnId(testCard.id) }
        coVerify(exactly = 1) { cardRepository.updateCard(testCard, testColumnId) }
    }

    @Test
    fun `SHOULD return true WHEN everything is successful`() = runTest {
        // GIVEN
        coEvery { cardRepository.getCardColumnId(testCard.id) } returns Result.Success(testColumnId)
        coEvery { cardRepository.updateCard(testCard, testColumnId) } returns true

        // WHEN
        val result = updateCardUseCase(testCard)

        // THEN
        assertThat(result).isTrue()
        coVerify(exactly = 1) { cardRepository.getCardColumnId(testCard.id) }
        coVerify(exactly = 1) { cardRepository.updateCard(testCard, testColumnId) }
    }
}