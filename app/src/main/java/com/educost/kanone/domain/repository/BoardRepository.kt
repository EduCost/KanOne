package com.educost.kanone.domain.repository

import com.educost.kanone.domain.error.LocalDataError
import com.educost.kanone.domain.model.Board
import com.educost.kanone.utils.Result
import kotlinx.coroutines.flow.Flow

interface BoardRepository {

    fun observeCompleteBoard(boardId: Long): Flow<Result<Board, LocalDataError>>

    suspend fun getBoard(boardId: Long): Result<Board, LocalDataError>

}
