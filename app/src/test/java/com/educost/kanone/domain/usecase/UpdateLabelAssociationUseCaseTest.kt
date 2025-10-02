package com.educost.kanone.domain.usecase

import com.educost.kanone.domain.repository.LabelRepository
import com.google.common.truth.Truth.assertThat
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

class UpdateLabelAssociationUseCaseTest {

    private lateinit var labelRepository: LabelRepository
    private lateinit var updateLabelAssociationUseCase: UpdateLabelAssociationUseCase

    private val labelId = 1L
    private val cardId = 2L

    @Before
    fun setUp() {
        labelRepository = mockk()
        updateLabelAssociationUseCase = UpdateLabelAssociationUseCase(labelRepository)
    }

    @Test
    fun `SHOULD call deleteLabelAssociation WHEN label is already associated`() = runTest {
        coEvery { labelRepository.hasLabelAssociation(labelId, cardId) } returns true
        coEvery { labelRepository.deleteLabelAssociation(labelId, cardId) } returns true
        coEvery { labelRepository.associateLabelWithCard(labelId, cardId) } returns true

        val result = updateLabelAssociationUseCase(labelId, cardId)

        assertThat(result).isTrue()
        coVerify { labelRepository.hasLabelAssociation(labelId, cardId) }
        coVerify { labelRepository.deleteLabelAssociation(labelId, cardId) }
        coVerify(exactly = 0) { labelRepository.associateLabelWithCard(labelId, cardId) }
    }

    @Test
    fun `SHOULD call associateLabelWithCard WHEN label is not associated`() = runTest {
        coEvery { labelRepository.hasLabelAssociation(labelId, cardId) } returns false
        coEvery { labelRepository.deleteLabelAssociation(labelId, cardId) } returns true
        coEvery { labelRepository.associateLabelWithCard(labelId, cardId) } returns true

        val result = updateLabelAssociationUseCase(labelId, cardId)

        assertThat(result).isTrue()
        coVerify { labelRepository.hasLabelAssociation(labelId, cardId) }
        coVerify(exactly = 0) { labelRepository.deleteLabelAssociation(labelId, cardId) }
        coVerify { labelRepository.associateLabelWithCard(labelId, cardId) }
    }

}