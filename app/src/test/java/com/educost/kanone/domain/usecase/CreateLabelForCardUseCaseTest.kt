package com.educost.kanone.domain.usecase

import com.educost.kanone.domain.error.GenericError
import com.educost.kanone.domain.model.Label
import com.educost.kanone.domain.repository.LabelRepository
import com.educost.kanone.utils.Result
import com.google.common.truth.Truth.assertThat
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

class CreateLabelForCardUseCaseTest {

    private lateinit var createLabelForCardUseCase: CreateLabelForCardUseCase
    private lateinit var labelRepository: LabelRepository

    private val label = Label(
        id = 1,
        name = "name",
        color = null
    )


    @Before
    fun setUp() {
        labelRepository = mockk()
        createLabelForCardUseCase = CreateLabelForCardUseCase(labelRepository)
    }

    @Test
    fun `SHOULD return false WHEN getBoardId returns error`() = runTest {
        coEvery { labelRepository.getBoardId(any()) } returns Result.Error(GenericError)
        coEvery {
            labelRepository.createLabelAndAssociateWithCard(
                any(),
                any(),
                any()
            )
        } returns true

        val result = createLabelForCardUseCase(label = label, cardId = 1)

        assertThat(result).isFalse()

        coVerify(exactly = 1) { labelRepository.getBoardId(any()) }
        coVerify(exactly = 0) {
            labelRepository.createLabelAndAssociateWithCard(
                label = any(),
                boardId = any(),
                cardId = any()
            )
        }
    }

    @Test
    fun `SHOULD return false WHEN createLabelAndAssociateWithCard returns false`() = runTest {
        coEvery { labelRepository.getBoardId(any()) } returns Result.Success(1L)
        coEvery {
            labelRepository.createLabelAndAssociateWithCard(
                label = any(),
                boardId = any(),
                cardId = any()
            )
        } returns false

        val result = createLabelForCardUseCase(label = label, cardId = 1)

        assertThat(result).isFalse()

        coVerify(exactly = 1) { labelRepository.getBoardId(any()) }
        coVerify(exactly = 1) {
            labelRepository.createLabelAndAssociateWithCard(
                label = any(),
                boardId = any(),
                cardId = any()
            )
        }
    }

    @Test
    fun `SHOULD return true WHEN everything was successful`() = runTest {
        coEvery { labelRepository.getBoardId(any()) } returns Result.Success(1L)
        coEvery {
            labelRepository.createLabelAndAssociateWithCard(
                label = any(),
                boardId = any(),
                cardId = any()
            )
        } returns true

        val result = createLabelForCardUseCase(label = label, cardId = 1)

        assertThat(result).isTrue()

        coVerify(exactly = 1) { labelRepository.getBoardId(any()) }
        coVerify(exactly = 1) {
            labelRepository.createLabelAndAssociateWithCard(
                label = any(),
                boardId = any(),
                cardId = any()
            )
        }
    }

}