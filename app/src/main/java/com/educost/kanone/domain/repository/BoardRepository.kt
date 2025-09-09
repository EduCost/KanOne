package com.educost.kanone.domain.repository

import com.educost.kanone.domain.error.FetchDataError
import com.educost.kanone.domain.error.InsertDataError
import com.educost.kanone.domain.model.Board
import com.educost.kanone.domain.model.KanbanColumn
import com.educost.kanone.utils.Result
import kotlinx.coroutines.flow.Flow

interface BoardRepository {

    fun observeAllBoards(): Flow<Result<List<Board>, FetchDataError>>

    fun observeCompleteBoard(boardId: Long): Flow<Result<Board, FetchDataError>>

    suspend fun createBoard(board: Board): Result<Long, InsertDataError>

    suspend fun updateBoardData(boardId: Long, columns: List<KanbanColumn>): Result<Unit, InsertDataError>

    suspend fun updateBoard(board: Board): Boolean
}
