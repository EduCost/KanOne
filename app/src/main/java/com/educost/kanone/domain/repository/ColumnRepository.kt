package com.educost.kanone.domain.repository

import com.educost.kanone.domain.error.GenericError
import com.educost.kanone.domain.model.KanbanColumn
import com.educost.kanone.utils.Result
import kotlinx.coroutines.flow.Flow

interface ColumnRepository {

    fun observeColumns(boardId: Long): Flow<Result<List<KanbanColumn>, GenericError>>

    suspend fun createColumn(column: KanbanColumn, boardId: Long): Boolean

    suspend fun updateColumn(column: KanbanColumn, boardId: Long): Boolean

    suspend fun deleteColumn(column: KanbanColumn, boardId: Long): Boolean

    suspend fun restoreColumn(column: KanbanColumn, boardId: Long): Boolean

}