package com.educost.kanone.domain.usecase

import com.educost.kanone.domain.error.FetchDataError
import com.educost.kanone.domain.model.Board
import com.educost.kanone.domain.repository.BoardRepository
import com.educost.kanone.utils.Result
import kotlinx.coroutines.flow.Flow

class ObserveAllBoardsUseCase(val boardRepository: BoardRepository) {

    suspend operator fun invoke(): Flow<Result<List<Board>, FetchDataError>>  {
        return boardRepository.observeAllBoards()
    }

}