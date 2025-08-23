package com.educost.kanone.data.repository

import android.util.Log
import com.educost.kanone.data.local.ColumnDao
import com.educost.kanone.data.mapper.toAttachmentEntity
import com.educost.kanone.data.mapper.toCardEntity
import com.educost.kanone.data.mapper.toTaskEntity
import com.educost.kanone.data.mapper.toColumnEntity
import com.educost.kanone.data.mapper.toKanbanColumn
import com.educost.kanone.data.mapper.toLabelEntity
import com.educost.kanone.data.model.entity.AttachmentEntity
import com.educost.kanone.data.model.entity.TaskEntity
import com.educost.kanone.data.model.entity.LabelEntity
import com.educost.kanone.domain.error.FetchDataError
import com.educost.kanone.domain.error.InsertDataError
import com.educost.kanone.domain.model.KanbanColumn
import com.educost.kanone.domain.repository.ColumnRepository
import com.educost.kanone.utils.LogTags
import com.educost.kanone.utils.Result
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

class ColumnRepositoryImpl(val columnDao: ColumnDao) : ColumnRepository {

    override fun observeColumns(boardId: Long): Flow<Result<List<KanbanColumn>, FetchDataError>> {
        return columnDao.observeColumns(boardId).map { columns ->
            Result.Success(columns.map { it.toKanbanColumn() })

        }.catch { e ->
            when (e) {
                is IOException -> Result.Error(FetchDataError.IO_ERROR)
                else -> {
                    Log.e(LogTags.COLUMN_REPO, e.stackTraceToString())
                    Result.Error(FetchDataError.UNKNOWN)
                }
            }
        }
    }

    override suspend fun createColumn(
        column: KanbanColumn,
        boardId: Long
    ): Result<Long, InsertDataError> {
        return try {
            val columnId = columnDao.createColumn(column.toColumnEntity(boardId))
            Result.Success(columnId)
        } catch (e: Exception) {
            when (e) {
                is IOException -> Result.Error(InsertDataError.IO_ERROR)
                else -> {
                    Log.e(LogTags.COLUMN_REPO, e.stackTraceToString())
                    Result.Error(InsertDataError.UNKNOWN)
                }
            }
        }
    }

    override suspend fun updateColumn(
        column: KanbanColumn,
        boardId: Long
    ): Result<Unit, InsertDataError> {
        return try {
            columnDao.updateColumn(column.toColumnEntity(boardId))
            Result.Success(Unit)
        } catch (e: Exception) {
            when (e) {
                is IOException -> Result.Error(InsertDataError.IO_ERROR)
                else -> {
                    Log.e(LogTags.COLUMN_REPO, e.stackTraceToString())
                    Result.Error(InsertDataError.UNKNOWN)
                }
            }

        }
    }

    override suspend fun deleteColumn(
        column: KanbanColumn,
        boardId: Long
    ): Result<Unit, InsertDataError> {
        return try {
            columnDao.deleteColumn(column.toColumnEntity(boardId))
            Result.Success(Unit)
        } catch (e: Exception) {
            when (e) {
                is IOException -> Result.Error(InsertDataError.IO_ERROR)
                else -> {
                    Log.e(LogTags.COLUMN_REPO, e.stackTraceToString())
                    Result.Error(InsertDataError.UNKNOWN)
                }
            }
        }
    }

    override suspend fun restoreColumn(
        column: KanbanColumn,
        boardId: Long
    ): Result<Unit, InsertDataError> {
        return try {
            val columnEntity = column.toColumnEntity(boardId)
            val cards = column.cards.map { it.toCardEntity(columnEntity.id) }
            val labels = mutableListOf<LabelEntity>()
            val tasks = mutableListOf<TaskEntity>()
            val attachments = mutableListOf<AttachmentEntity>()

            column.cards.forEach { card ->
                card.labels.forEach { label ->
                    labels.add(label.toLabelEntity(card.id))
                }
                card.tasks.forEach { task ->
                    tasks.add(task.toTaskEntity(card.id))
                }
                card.attachments.forEach { attachment ->
                    attachments.add(attachment.toAttachmentEntity(card.id))
                }
            }

            columnDao.restoreWholeColumn(
                column = columnEntity,
                cards = cards,
                labels = labels,
                tasks = tasks,
                attachments = attachments
            )
            Result.Success(Unit)
        } catch (e: Exception) {
            when (e) {
                is IOException -> Result.Error(InsertDataError.IO_ERROR)
                else -> {
                    Log.e(LogTags.COLUMN_REPO, e.stackTraceToString())
                    Result.Error(InsertDataError.UNKNOWN)
                }
            }
        }
    }
}