package com.educost.kanone.domain.usecase

import com.educost.kanone.domain.model.KanbanColumn
import com.educost.kanone.domain.repository.ColumnRepository

class CreateColumnUseCase(private val columnRepository: ColumnRepository) {

    suspend operator fun invoke(
        columnName: String?,
        position: Int,
        boardId: Long
    ): CreateColumnResult {

        if (columnName.isNullOrBlank()) return CreateColumnResult.EMPTY_NAME

        val newColumn = KanbanColumn(
            id = 0,
            name = columnName,
            position = position
        )

        val wasColumnCreated = columnRepository.createColumn(newColumn, boardId)

        if (!wasColumnCreated) return CreateColumnResult.GENERIC_ERROR

        return CreateColumnResult.SUCCESS
    }
}

enum class CreateColumnResult {
    SUCCESS,
    EMPTY_NAME,
    GENERIC_ERROR
}