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

class DeleteCardUseCaseTest {

    private lateinit var deleteCardUseCase: DeleteCardUseCase
    private lateinit var cardRepository: CardRepository

    private val testCard = CardItem(
        id = 1L,
        title = "Test Card",
        description = null,
        color = null,
        createdAt = LocalDateTime.now(),
        position = 0
    )
    private val testColumnId = 10L

    @Before
    fun setUp() {
        cardRepository = mockk()
        deleteCardUseCase = DeleteCardUseCase(cardRepository)
    }

    @Test
    fun `SHOULD return false WHEN getCardColumnId returns error`() = runTest {
        // Given
        coEvery { cardRepository.getCardColumnId(testCard.id) } returns Result.Error(GenericError)
        coEvery { cardRepository.deleteCard(testCard, testColumnId) } returns true

        // When
        val result = deleteCardUseCase(testCard)

        // Then
        assertThat(result).isFalse()
        coVerify(exactly = 1) { cardRepository.getCardColumnId(testCard.id) }
        coVerify(exactly = 0) { cardRepository.deleteCard(any(), any()) }
    }

    @Test
    fun `SHOULD return false WHEN deleteCard returns false`() = runTest {
        // Given
        coEvery { cardRepository.getCardColumnId(testCard.id) } returns Result.Success(testColumnId)
        coEvery { cardRepository.deleteCard(testCard, testColumnId) } returns false

        // When
        val result = deleteCardUseCase(testCard)

        // Then
        assertThat(result).isFalse()
        coVerify(exactly = 1) { cardRepository.getCardColumnId(testCard.id) }
        coVerify(exactly = 1) { cardRepository.deleteCard(testCard, testColumnId) }
    }

    @Test
    fun `SHOULD return true WHEN everything is successful`() = runTest {
        // Given
        coEvery { cardRepository.getCardColumnId(testCard.id) } returns Result.Success(testColumnId)
        coEvery { cardRepository.deleteCard(testCard, testColumnId) } returns true

        // When
        val result = deleteCardUseCase(testCard)

        // Then
        assertThat(result).isTrue()
        coVerify(exactly = 1) { cardRepository.getCardColumnId(testCard.id) }
        coVerify(exactly = 1) { cardRepository.deleteCard(testCard, testColumnId) }
    }
}