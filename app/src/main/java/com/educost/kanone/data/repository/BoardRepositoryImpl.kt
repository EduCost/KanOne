package com.educost.kanone.data.repository

import android.util.Log
import com.educost.kanone.data.local.BoardDao
import com.educost.kanone.data.mapper.toBoard
import com.educost.kanone.domain.error.FetchDataError
import com.educost.kanone.domain.model.Board
import com.educost.kanone.domain.repository.BoardRepository
import com.educost.kanone.utils.LogTags
import com.educost.kanone.utils.Result
import jakarta.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

class BoardRepositoryImpl @Inject constructor(val boardDao: BoardDao) : BoardRepository {

    override suspend fun getBoard(id: Long): Result<Board, FetchDataError> {
        return try {
            val board = boardDao.getBoard(id)
            Result.Success(board.toBoard())
        } catch (e: IOException) {
            Result.Error(FetchDataError.IO_ERROR)
        } catch (e: Exception) {
            Log.e(LogTags.BOARD_REPO, e.stackTraceToString())
            Result.Error(FetchDataError.UNKNOWN)
        }
    }

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
}