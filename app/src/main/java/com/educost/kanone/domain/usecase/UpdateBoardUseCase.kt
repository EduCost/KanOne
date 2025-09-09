package com.educost.kanone.domain.usecase

import com.educost.kanone.domain.model.Board
import com.educost.kanone.domain.repository.BoardRepository

class UpdateBoardUseCase(val repository: BoardRepository) {

    suspend operator fun invoke(board: Board) = repository.updateBoard(board)

}