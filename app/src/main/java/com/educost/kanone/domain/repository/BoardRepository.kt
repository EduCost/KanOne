package com.educost.kanone.domain.repository

import com.educost.kanone.domain.error.FetchDataError
import com.educost.kanone.domain.model.Board
import com.educost.kanone.utils.Result
import kotlinx.coroutines.flow.Flow

interface BoardRepository {

    suspend fun getBoard(id: Long): Result<Board, FetchDataError>

    fun observeAllBoards(): Flow<Result<List<Board>, FetchDataError>>

}
