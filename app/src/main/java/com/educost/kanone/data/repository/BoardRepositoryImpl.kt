package com.educost.kanone.data.repository

import com.educost.kanone.data.local.BoardDao
import com.educost.kanone.data.mapper.toBoard
import com.educost.kanone.data.mapper.toBoardEntity
import com.educost.kanone.data.mapper.toCardEntity
import com.educost.kanone.data.mapper.toColumnEntity
import com.educost.kanone.data.model.entity.CardEntity
import com.educost.kanone.domain.error.GenericError
import com.educost.kanone.domain.logs.LogHandler
import com.educost.kanone.domain.logs.LogLevel
import com.educost.kanone.domain.logs.LogLocation
import com.educost.kanone.domain.model.Board
import com.educost.kanone.domain.model.KanbanColumn
import com.educost.kanone.domain.repository.BoardRepository
import com.educost.kanone.utils.Result
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map

class BoardRepositoryImpl(
    private val boardDao: BoardDao,
    private val logHandler: LogHandler
) : BoardRepository {

    override fun observeAllBoards(): Flow<Result<List<Board>, GenericError>> {
        return boardDao.observeAllBoards().map { boards ->
            Result.Success(boards.map { it.toBoard() })
        }.catch { e ->
            e.printStackTrace()
            logHandler.log(
                throwable = e,
                message = "Error fetching all boards",
                from = LogLocation.BOARD_REPOSITORY,
                level = LogLevel.ERROR
            )

            Result.Error(GenericError)
        }
    }

    override suspend fun createBoard(board: Board): Boolean {
        return try {
            boardDao.createBoard(board.toBoardEntity())
            true
        } catch (e: Exception) {
            logHandler.log(
                throwable = e,
                message = "Error creating board",
                from = LogLocation.BOARD_REPOSITORY,
                level = LogLevel.ERROR
            )

            false
        }
    }

    override suspend fun updateBoardData(
        boardId: Long,
        columns: List<KanbanColumn>
    ): Boolean {
        return try {
            val columnsToUpdate = columns.map { it.toColumnEntity(boardId) }
            val cardsToUpdate = mutableListOf<CardEntity>()
            columns.forEach { column ->
                column.cards.forEach { card ->
                    cardsToUpdate.add(card.toCardEntity(column.id))
                }
            }
            boardDao.updateBoardData(columnsToUpdate, cardsToUpdate)
            true
        } catch (e: Exception) {
            logHandler.log(
                throwable = e,
                message = "Error updating board data",
                from = LogLocation.BOARD_REPOSITORY,
                level = LogLevel.ERROR
            )

            false
        }
    }

    override suspend fun updateBoard(board: Board): Boolean {
        return try {
            boardDao.updateBoard(board.toBoardEntity())
            true
        } catch (e: Exception) {
            logHandler.log(
                throwable = e,
                message = "Error updating board",
                from = LogLocation.BOARD_REPOSITORY,
                level = LogLevel.ERROR
            )

            false
        }
    }

    override suspend fun deleteBoard(board: Board): Boolean {
        return try {
            boardDao.deleteBoard(board.toBoardEntity())
            true
        } catch (e: Exception) {
            logHandler.log(
                throwable = e,
                message = "Error deleting board",
                from = LogLocation.BOARD_REPOSITORY,
                level = LogLevel.ERROR
            )

            false
        }
    }

    override fun observeCompleteBoard(boardId: Long): Flow<Result<Board, GenericError>> {
        return boardDao.observeCompleteBoard(boardId).map {
            Result.Success(it.toBoard())
        }.catch { e ->
            logHandler.log(
                throwable = e,
                message = "Error fetching board",
                from = LogLocation.BOARD_REPOSITORY,
                level = LogLevel.ERROR
            )

            Result.Error(GenericError)
        }
    }
}