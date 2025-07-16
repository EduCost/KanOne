package com.educost.kanone.data.repository

import com.educost.kanone.data.local.BoardDao
import com.educost.kanone.data.mapper.toBoard
import com.educost.kanone.domain.error.LocalDataError
import com.educost.kanone.domain.model.Board
import com.educost.kanone.domain.repository.BoardRepository
import com.educost.kanone.utils.Result
import jakarta.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

class BoardRepositoryImpl @Inject constructor(val boardDao: BoardDao) : BoardRepository {

    override fun observeCompleteBoard(boardId: Long): Flow<Result<Board, LocalDataError>> {
        return boardDao.observeCompleteBoard(boardId).map {
            Result.Success(it.toBoard())
        }.catch { e ->
            if (e is IOException) {
                Result.Error(LocalDataError.IO_ERROR)
            } else {
                Result.Error(LocalDataError.UNKNOWN)
            }
        }
    }

    override suspend fun getBoard(boardId: Long): Result<Board, LocalDataError> {
        return try {
            Result.Success(boardDao.getBoard(boardId).toBoard())
        } catch (e: IOException) {
            Result.Error(LocalDataError.IO_ERROR)
        } catch (e: Exception) {
            Result.Error(LocalDataError.UNKNOWN)
        }
    }
}