package com.educost.kanone.domain.usecase

import com.educost.kanone.domain.repository.ColumnRepository
import com.google.common.truth.Truth.assertThat
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

class CreateColumnUseCaseTest {

    private lateinit var createColumnUseCase: CreateColumnUseCase
    private lateinit var columnRepository: ColumnRepository

    @Before
    fun setUp() {
        columnRepository = mockk()
        createColumnUseCase = CreateColumnUseCase(columnRepository)
    }

    @Test
    fun `SHOULD return EMPTY_NAME WHEN invoke with null column name`() = runTest {
        coEvery { columnRepository.createColumn(any(), any()) } returns true

        val result = createColumnUseCase(null, position = 0, boardId = 1)
        assertThat(CreateColumnResult.EMPTY_NAME).isEqualTo(result)

        coVerify(exactly = 0) { columnRepository.createColumn(any(), any()) }
    }

    @Test
    fun `SHOULD return EMPTY_NAME WHEN invoke with blank column name`() = runTest {
        coEvery { columnRepository.createColumn(any(), any()) } returns true

        val result = createColumnUseCase(" ", position = 0, boardId = 1)
        assertThat(CreateColumnResult.EMPTY_NAME).isEqualTo(result)

        coVerify(exactly = 0) { columnRepository.createColumn(any(), any()) }
    }

    @Test
    fun `SHOULD return GENERIC_ERROR WHEN columnRepository returns false`() = runTest {
        coEvery { columnRepository.createColumn(any(), any()) } returns false

        val result = createColumnUseCase("Test Column", position = 0, boardId = 1)
        assertThat(CreateColumnResult.GENERIC_ERROR).isEqualTo(result)

        coVerify(exactly = 1) { columnRepository.createColumn(any(), any()) }
    }

    @Test
    fun `SHOULD return SUCCESS WHEN columnRepository returns true`() = runTest {
        coEvery { columnRepository.createColumn(any(), any()) } returns true


        val result = createColumnUseCase("Test Column", position = 0, boardId = 1)
        assertThat(CreateColumnResult.SUCCESS).isEqualTo(result)

        coVerify(exactly = 1) { columnRepository.createColumn(any(), any()) }
    }

}


