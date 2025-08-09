package com.educost.kanone.domain.usecase

import com.educost.kanone.domain.error.InsertDataError
import com.educost.kanone.domain.model.KanbanColumn
import com.educost.kanone.domain.repository.ColumnRepository
import com.educost.kanone.utils.Result

class UpdateColumnUseCase(val columnRepository: ColumnRepository) {

    suspend operator fun invoke(
        column: KanbanColumn,
        boardId: Long
    ): Result<Unit, InsertDataError> {
        return columnRepository.updateColumn(column, boardId)
    }

}