package com.educost.kanone.domain.repository

import com.educost.kanone.domain.error.FetchDataError
import com.educost.kanone.domain.model.Board
import com.educost.kanone.utils.Result

interface BoardRepository {

    suspend fun getBoard(id: Long): Result<Board, FetchDataError>

    suspend fun getAllBoards(): Result<List<Board>, FetchDataError>

}
