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

class UpdateMarkdownUseCaseTest {

    private lateinit var updateMarkdownUseCase: UpdateMarkdownUseCase
    private lateinit var cardRepository: CardRepository
    private lateinit var updateCardUseCase: UpdateCardUseCase

    private val testCardId = 1L
    private val testMarkdown = "New markdown description"
    private val testCard = CardItem(
        id = testCardId,
        title = "Test Card",
        description = "Old description",
        color = null,
        createdAt = LocalDateTime.now(),
        position = 0
    )

    @Before
    fun setUp() {
        cardRepository = mockk()
        updateCardUseCase = mockk()
        updateMarkdownUseCase = UpdateMarkdownUseCase(cardRepository, updateCardUseCase)
    }

    @Test
    fun `SHOULD return false WHEN getCard returns error`() = runTest {
        // GIVEN
        coEvery { cardRepository.getCard(testCardId) } returns Result.Error(GenericError)

        // WHEN
        val result = updateMarkdownUseCase(testCardId, testMarkdown)

        // THEN
        assertThat(result).isFalse()
        coVerify(exactly = 1) { cardRepository.getCard(testCardId) }
        coVerify(exactly = 0) { updateCardUseCase(any()) }
    }

    @Test
    fun `SHOULD return false WHEN updateCardUseCase returns false`() = runTest {
        // GIVEN
        val updatedCard = testCard.copy(description = testMarkdown)
        coEvery { cardRepository.getCard(testCardId) } returns Result.Success(testCard)
        coEvery { updateCardUseCase(updatedCard) } returns false

        // WHEN
        val result = updateMarkdownUseCase(testCardId, testMarkdown)

        // THEN
        assertThat(result).isFalse()
        coVerify(exactly = 1) { cardRepository.getCard(testCardId) }
        coVerify(exactly = 1) { updateCardUseCase(updatedCard) }
    }

    @Test
    fun `SHOULD return true WHEN everything is successful`() = runTest {
        // GIVEN
        val updatedCard = testCard.copy(description = testMarkdown)
        coEvery { cardRepository.getCard(testCardId) } returns Result.Success(testCard)
        coEvery { updateCardUseCase(updatedCard) } returns true

        // WHEN
        val result = updateMarkdownUseCase(testCardId, testMarkdown)

        // THEN
        assertThat(result).isTrue()
        coVerify(exactly = 1) { cardRepository.getCard(testCardId) }
        coVerify(exactly = 1) { updateCardUseCase(updatedCard) }
    }
}