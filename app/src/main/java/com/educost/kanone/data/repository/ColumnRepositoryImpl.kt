package com.educost.kanone.data.repository

import com.educost.kanone.data.local.ColumnDao
import com.educost.kanone.data.mapper.toAttachmentEntity
import com.educost.kanone.data.mapper.toCardEntity
import com.educost.kanone.data.mapper.toColumnEntity
import com.educost.kanone.data.mapper.toKanbanColumn
import com.educost.kanone.data.mapper.toLabelEntity
import com.educost.kanone.data.mapper.toTaskEntity
import com.educost.kanone.data.model.entity.AttachmentEntity
import com.educost.kanone.data.model.entity.LabelEntity
import com.educost.kanone.data.model.entity.TaskEntity
import com.educost.kanone.domain.error.GenericError
import com.educost.kanone.domain.logs.LogHandler
import com.educost.kanone.domain.logs.LogLevel
import com.educost.kanone.domain.logs.LogLocation
import com.educost.kanone.domain.model.KanbanColumn
import com.educost.kanone.domain.repository.ColumnRepository
import com.educost.kanone.utils.Result
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map

class ColumnRepositoryImpl(
    private val columnDao: ColumnDao,
    private val logHandler: LogHandler
) : ColumnRepository {

    override fun observeColumns(boardId: Long): Flow<Result<List<KanbanColumn>, GenericError>> {
        return columnDao.observeColumns(boardId).map { columns ->
            Result.Success(columns.map { it.toKanbanColumn() })

        }.catch { e ->
            logHandler.log(
                throwable = e,
                message = "Error fetching column",
                from = LogLocation.COLUMN_REPOSITORY,
                level = LogLevel.ERROR
            )

            Result.Error(GenericError)
        }
    }

    override suspend fun createColumn(
        column: KanbanColumn,
        boardId: Long
    ): Boolean {
        return try {
            columnDao.createColumn(column.toColumnEntity(boardId))
            true
        } catch (e: Exception) {
            logHandler.log(
                throwable = e,
                message = "Error creating column",
                from = LogLocation.COLUMN_REPOSITORY,
                level = LogLevel.ERROR
            )

            false
        }
    }

    override suspend fun updateColumn(
        column: KanbanColumn,
        boardId: Long
    ): Boolean {
        return try {
            columnDao.updateColumn(column.toColumnEntity(boardId))
            true
        } catch (e: Exception) {
            logHandler.log(
                throwable = e,
                message = "Error updating column",
                from = LogLocation.COLUMN_REPOSITORY,
                level = LogLevel.ERROR
            )

            false
        }
    }

    override suspend fun deleteColumn(
        column: KanbanColumn,
        boardId: Long
    ): Boolean {
        return try {
            columnDao.deleteColumn(column.toColumnEntity(boardId))
            true
        } catch (e: Exception) {
            logHandler.log(
                throwable = e,
                message = "Error deleting column",
                from = LogLocation.COLUMN_REPOSITORY,
                level = LogLevel.ERROR
            )

            false
        }
    }

    override suspend fun restoreColumn(
        column: KanbanColumn,
        boardId: Long
    ): Boolean {
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
            true
        } catch (e: Exception) {
            logHandler.log(
                throwable = e,
                message = "Error restoring column",
                from = LogLocation.COLUMN_REPOSITORY,
                level = LogLevel.ERROR
            )
            false
        }
    }
}