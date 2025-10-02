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

class UpdateLabelUseCaseTest {

    private lateinit var updateLabelUseCase: UpdateLabelUseCase
    private lateinit var labelRepository: LabelRepository

    private val testLabel = Label(
        id = 1L,
        name = "Test Label",
        color = null
    )
    private val testCardId = 100L
    private val testBoardId = 10L

    @Before
    fun setUp() {
        labelRepository = mockk()
        updateLabelUseCase = UpdateLabelUseCase(labelRepository)
    }

    @Test
    fun `SHOULD return false WHEN getBoardId returns error`() = runTest {
        // GIVEN
        coEvery { labelRepository.getBoardId(testCardId) } returns Result.Error(GenericError)
        coEvery { labelRepository.updateLabel(any(), any()) } returns false

        // WHEN
        val result = updateLabelUseCase(testLabel, testCardId)

        // THEN
        assertThat(result).isFalse()
        coVerify(exactly = 1) { labelRepository.getBoardId(testCardId) }
        coVerify(exactly = 0) { labelRepository.updateLabel(any(), any()) }
    }

    @Test
    fun `SHOULD return false WHEN updateLabel returns false`() = runTest {
        // GIVEN
        coEvery { labelRepository.getBoardId(testCardId) } returns Result.Success(testBoardId)
        coEvery { labelRepository.updateLabel(testLabel, testBoardId) } returns false

        // WHEN
        val result = updateLabelUseCase(testLabel, testCardId)

        // THEN
        assertThat(result).isFalse()
        coVerify(exactly = 1) { labelRepository.getBoardId(testCardId) }
        coVerify(exactly = 1) { labelRepository.updateLabel(testLabel, testBoardId) }
    }

    @Test
    fun `SHOULD return true WHEN everything is successful`() = runTest {
        // GIVEN
        coEvery { labelRepository.getBoardId(testCardId) } returns Result.Success(testBoardId)
        coEvery { labelRepository.updateLabel(testLabel, testBoardId) } returns true

        // WHEN
        val result = updateLabelUseCase(testLabel, testCardId)

        // THEN
        assertThat(result).isTrue()
        coVerify(exactly = 1) { labelRepository.getBoardId(testCardId) }
        coVerify(exactly = 1) { labelRepository.updateLabel(testLabel, testBoardId) }
    }
}