package com.educost.kanone.domain.usecase

import com.educost.kanone.domain.error.FetchDataError
import com.educost.kanone.domain.error.InsertDataError
import com.educost.kanone.domain.model.Board
import com.educost.kanone.domain.repository.BoardRepository
import com.educost.kanone.utils.Result

class CreateBoardUseCase(val boardRepository: BoardRepository) {

    suspend operator fun invoke(board: Board): Result<Long, InsertDataError> {
        return boardRepository.createBoard(board)
    }
}
