package com.educost.kanone.domain.usecase

import com.educost.kanone.domain.error.InsertDataError
import com.educost.kanone.domain.model.KanbanColumn
import com.educost.kanone.domain.repository.ColumnRepository
import com.educost.kanone.utils.Result

class CreateColumnUseCase(private val columnRepository: ColumnRepository) {

    suspend operator fun invoke(column: KanbanColumn, boardId: Long): Result<Long, InsertDataError> {
        return columnRepository.createColumn(column, boardId)
    }
}