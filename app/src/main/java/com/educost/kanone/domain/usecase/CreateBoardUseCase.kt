package com.educost.kanone.domain.usecase

import com.educost.kanone.domain.model.Board
import com.educost.kanone.domain.repository.BoardRepository

class CreateBoardUseCase(val boardRepository: BoardRepository) {

    suspend operator fun invoke(board: Board) = boardRepository.createBoard(board)

}
