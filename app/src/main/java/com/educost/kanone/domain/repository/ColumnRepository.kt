package com.educost.kanone.domain.repository

import com.educost.kanone.domain.error.FetchDataError
import com.educost.kanone.domain.error.InsertDataError
import com.educost.kanone.domain.model.KanbanColumn
import com.educost.kanone.utils.Result
import kotlinx.coroutines.flow.Flow

interface ColumnRepository {

    fun observeColumns(boardId: Long): Flow<Result<List<KanbanColumn>, FetchDataError>>

    suspend fun createColumn(column: KanbanColumn, boardId: Long): Result<Long, InsertDataError>

    suspend fun updateColumn(column: KanbanColumn, boardId: Long): Result<Unit, InsertDataError>

    suspend fun deleteColumn(column: KanbanColumn, boardId: Long): Result<Unit, InsertDataError>

    suspend fun restoreColumn(column: KanbanColumn, boardId: Long): Result<Unit, InsertDataError>

}