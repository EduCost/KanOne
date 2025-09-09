package com.educost.kanone.data.repository

import android.util.Log
import com.educost.kanone.data.local.BoardDao
import com.educost.kanone.data.mapper.toBoard
import com.educost.kanone.data.mapper.toBoardEntity
import com.educost.kanone.data.mapper.toCardEntity
import com.educost.kanone.data.mapper.toColumnEntity
import com.educost.kanone.data.model.entity.CardEntity
import com.educost.kanone.domain.error.FetchDataError
import com.educost.kanone.domain.error.InsertDataError
import com.educost.kanone.domain.model.Board
import com.educost.kanone.domain.model.KanbanColumn
import com.educost.kanone.domain.repository.BoardRepository
import com.educost.kanone.utils.LogTags
import com.educost.kanone.utils.Result
import jakarta.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

class BoardRepositoryImpl @Inject constructor(val boardDao: BoardDao) : BoardRepository {

    override fun observeAllBoards(): Flow<Result<List<Board>, FetchDataError>> {
        return boardDao.observeAllBoards().map { boards ->
            Result.Success(boards.map { it.toBoard() })
        }.catch { e ->
            when (e) {
                is IOException -> Result.Error(FetchDataError.IO_ERROR)
                else -> {
                    Log.e(LogTags.BOARD_REPO, e.stackTraceToString())
                    Result.Error(FetchDataError.UNKNOWN)
                }
            }
        }
    }

    override suspend fun createBoard(board: Board): Result<Long, InsertDataError> {
        return try {
            val boardId = boardDao.createBoard(board.toBoardEntity())
            Result.Success(boardId)
        } catch (e: IOException) {
            Result.Error(InsertDataError.IO_ERROR)
        } catch (e: Exception) {
            Log.e(LogTags.BOARD_REPO, e.stackTraceToString())
            Result.Error(InsertDataError.UNKNOWN)
        }
    }

    override suspend fun updateBoardData(
        boardId: Long,
        columns: List<KanbanColumn>
    ): Result<Unit, InsertDataError> {
        return try {
            val columnsToUpdate = columns.map { it.toColumnEntity(boardId) }
            val cardsToUpdate = mutableListOf<CardEntity>()
            columns.forEach { column ->
                column.cards.forEach { card ->
                    cardsToUpdate.add(card.toCardEntity(column.id))
                }
            }
            Result.Success(boardDao.updateBoardData(columnsToUpdate, cardsToUpdate))
        } catch (e: IOException) {
            Result.Error(InsertDataError.IO_ERROR)
        } catch (e: Exception) {
            Log.e(LogTags.BOARD_REPO, e.stackTraceToString())
            Result.Error(InsertDataError.UNKNOWN)
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

    override fun observeCompleteBoard(boardId: Long): Flow<Result<Board, FetchDataError>> {
        return boardDao.observeCompleteBoard(boardId).map {
            Result.Success(it.toBoard())
        }.catch { e ->
            if (e is IOException) {
                Result.Error(FetchDataError.IO_ERROR)
            } else {
                Result.Error(FetchDataError.UNKNOWN)
            }
        }
    }
}