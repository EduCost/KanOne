package com.educost.kanone.data.repository

import android.util.Log
import com.educost.kanone.data.local.ColumnDao
import com.educost.kanone.data.mapper.toKanbanColumn
import com.educost.kanone.domain.error.LocalDataError
import com.educost.kanone.domain.model.KanbanColumn
import com.educost.kanone.domain.repository.ColumnRepository
import com.educost.kanone.utils.LogTags
import com.educost.kanone.utils.Result
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

class ColumnRepositoryImpl(val columnDao: ColumnDao) : ColumnRepository {

    override fun observeColumns(boardId: Long): Flow<Result<List<KanbanColumn>, LocalDataError>> {
        return columnDao.observeColumns(boardId).map { columns ->
            Result.Success(columns.map { it.toKanbanColumn() })

        }.catch { e ->
            when (e) {
                is IOException -> Result.Error(LocalDataError.IO_ERROR)
                else -> {
                    Log.e(LogTags.COLUMN_REPO, e.stackTraceToString())
                    Result.Error(LocalDataError.UNKNOWN)
                }
            }
        }
    }
}