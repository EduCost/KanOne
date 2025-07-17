package com.educost.kanone.domain.repository

import com.educost.kanone.domain.error.LocalDataError
import com.educost.kanone.domain.model.Board
import com.educost.kanone.utils.Result
import kotlinx.coroutines.flow.Flow

interface BoardRepository {

    suspend fun getBoard(id: Long): Result<Board, LocalDataError>

    suspend fun getAllBoards(): Result<List<Board>, LocalDataError>

}
