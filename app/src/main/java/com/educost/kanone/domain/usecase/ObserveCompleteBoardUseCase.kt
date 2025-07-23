package com.educost.kanone.domain.usecase

import com.educost.kanone.domain.repository.BoardRepository

class ObserveCompleteBoardUseCase(
    private val boardRepository: BoardRepository
) {

    operator fun invoke(boardId: Long) = boardRepository.observeCompleteBoard(boardId)
}