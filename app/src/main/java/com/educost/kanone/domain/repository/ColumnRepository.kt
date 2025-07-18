package com.educost.kanone.domain.repository

import com.educost.kanone.domain.error.FetchDataError
import com.educost.kanone.domain.model.KanbanColumn
import com.educost.kanone.utils.Result
import kotlinx.coroutines.flow.Flow

interface ColumnRepository {

    fun observeColumns(boardId: Long): Flow<Result<List<KanbanColumn>, FetchDataError>>
}