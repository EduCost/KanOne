package com.educost.kanone.domain.usecase

import com.educost.kanone.domain.model.KanbanColumn
import com.educost.kanone.domain.repository.ColumnRepository

class RestoreColumnUseCase(val columnRepository: ColumnRepository) {

    suspend operator fun invoke(column: KanbanColumn, boardId: Long): Boolean {
        return columnRepository.restoreColumn(column, boardId)
    }

}