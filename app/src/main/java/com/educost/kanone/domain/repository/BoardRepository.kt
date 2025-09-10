package com.educost.kanone.domain.repository

import com.educost.kanone.domain.error.GenericError
import com.educost.kanone.domain.model.Board
import com.educost.kanone.domain.model.KanbanColumn
import com.educost.kanone.utils.Result
import kotlinx.coroutines.flow.Flow

interface BoardRepository {

    fun observeAllBoards(): Flow<Result<List<Board>, GenericError>>

    fun observeCompleteBoard(boardId: Long): Flow<Result<Board, GenericError>>

    suspend fun createBoard(board: Board): Result<Long, GenericError>

    suspend fun updateBoardData(boardId: Long, columns: List<KanbanColumn>): Result<Unit, GenericError>

    suspend fun updateBoard(board: Board): Boolean

    suspend fun deleteBoard(board: Board): Boolean
}
