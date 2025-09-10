package com.educost.kanone.data.repository

import com.educost.kanone.data.local.BoardDao
import com.educost.kanone.data.mapper.toBoard
import com.educost.kanone.data.mapper.toBoardEntity
import com.educost.kanone.data.mapper.toCardEntity
import com.educost.kanone.data.mapper.toColumnEntity
import com.educost.kanone.data.model.entity.CardEntity
import com.educost.kanone.domain.error.GenericError
import com.educost.kanone.domain.model.Board
import com.educost.kanone.domain.model.KanbanColumn
import com.educost.kanone.domain.repository.BoardRepository
import com.educost.kanone.utils.Result
import jakarta.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map

class BoardRepositoryImpl @Inject constructor(val boardDao: BoardDao) : BoardRepository {

    override fun observeAllBoards(): Flow<Result<List<Board>, GenericError>> {
        return boardDao.observeAllBoards().map { boards ->
            Result.Success(boards.map { it.toBoard() })
        }.catch { e ->
            e.printStackTrace()
            Result.Error(GenericError)
        }
    }

    override suspend fun createBoard(board: Board): Result<Long, GenericError> {
        return try {
            val boardId = boardDao.createBoard(board.toBoardEntity())
            Result.Success(boardId)
        } catch (e: Exception) {
            e.printStackTrace()
            Result.Error(GenericError)
        }
    }

    override suspend fun updateBoardData(
        boardId: Long,
        columns: List<KanbanColumn>
    ): Result<Unit, GenericError> {
        return try {
            val columnsToUpdate = columns.map { it.toColumnEntity(boardId) }
            val cardsToUpdate = mutableListOf<CardEntity>()
            columns.forEach { column ->
                column.cards.forEach { card ->
                    cardsToUpdate.add(card.toCardEntity(column.id))
                }
            }
            Result.Success(boardDao.updateBoardData(columnsToUpdate, cardsToUpdate))
        } catch (e: Exception) {
            e.printStackTrace()
            Result.Error(GenericError)
        }
    }

    override suspend fun updateBoard(board: Board): Boolean {
        return try {
            boardDao.updateBoard(board.toBoardEntity())
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    override suspend fun deleteBoard(board: Board): Boolean {
        return try {
            boardDao.deleteBoard(board.toBoardEntity())
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    override fun observeCompleteBoard(boardId: Long): Flow<Result<Board, GenericError>> {
        return boardDao.observeCompleteBoard(boardId).map {
            Result.Success(it.toBoard())
        }.catch { e ->
            e.printStackTrace()
            Result.Error(GenericError)
        }
    }
}