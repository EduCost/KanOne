package com.educost.kanone.domain.usecase

import com.educost.kanone.domain.repository.CardRepository
import com.google.common.truth.Truth.assertThat
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

class CreateCardUseCaseTest {

    private lateinit var createCardUseCase: CreateCardUseCase
    private lateinit var cardRepository: CardRepository

    @Before
    fun setUp() {
        cardRepository = mockk()
        createCardUseCase = CreateCardUseCase(cardRepository)
    }

    @Test
    fun `SHOULD return empty title WHEN invoke with null title`() = runTest {
        coEvery { cardRepository.createCard(any(), any()) } returns true

        val result = createCardUseCase(title = null, position = 0, columnId = 1)

        assertThat(result).isEqualTo(CreateCardResult.EMPTY_TITLE)
        coVerify(exactly = 0) { cardRepository.createCard(any(), any()) }
    }

    @Test
    fun `SHOULD return empty title WHEN invoke with blank title`() = runTest {
        coEvery { cardRepository.createCard(any(), any()) } returns true

        val result = createCardUseCase(title = "  ", position = 0, columnId = 1)

        assertThat(result).isEqualTo(CreateCardResult.EMPTY_TITLE)
        coVerify(exactly = 0) { cardRepository.createCard(any(), any()) }
    }

    @Test
    fun `SHOULD return generic error WHEN invoke with null columnId`() = runTest {
        coEvery { cardRepository.createCard(any(), any()) } returns true

        val result = createCardUseCase(title = "Test Title", position = 0, columnId = null)

        assertThat(result).isEqualTo(CreateCardResult.GENERIC_ERROR)
        coVerify(exactly = 0) { cardRepository.createCard(any(), any()) }
    }

    @Test
    fun `SHOULD return generic error WHEN invoke with null position`() = runTest {
        coEvery { cardRepository.createCard(any(), any()) } returns true

        val result = createCardUseCase(title = "Test Title", position = null, columnId = 1)

        assertThat(result).isEqualTo(CreateCardResult.GENERIC_ERROR)
        coVerify(exactly = 0) { cardRepository.createCard(any(), any()) }
    }

    @Test
    fun `SHOULD return success WHEN invoke with valid inputs and repository returns true`() {
        runTest {
            coEvery { cardRepository.createCard(any(), any()) } returns true

            val result = createCardUseCase(title = "Test Title", position = 0, columnId = 1)

            assertThat(result).isEqualTo(CreateCardResult.SUCCESS)
            coVerify(exactly = 1) { cardRepository.createCard(any(), any()) }
        }
    }

    @Test
    fun `SHOULD return generic error WHEN invoke with valid inputs and repository returns false`() {
        runTest {
            coEvery { cardRepository.createCard(any(), any()) } returns false

            val result = createCardUseCase(title = "Test Title", position = 0, columnId = 1)

            assertThat(result).isEqualTo(CreateCardResult.GENERIC_ERROR)
            coVerify(exactly = 1) { cardRepository.createCard(any(), any()) }
        }
    }

}