package com.educost.kanone.domain.usecase

import com.educost.kanone.domain.model.KanbanColumn
import com.educost.kanone.domain.repository.BoardRepository

class PersistBoardPositionsUseCase(val boardRepository: BoardRepository) {

    suspend operator fun invoke(boardId: Long, columns: List<KanbanColumn>): Boolean {

        val updatedColumns = columns.mapIndexed { columnIdx, column ->

            val updatedCards = column.cards.mapIndexed { cardIdx, card ->
                card.copy(position = cardIdx)
            }

            column.copy(
                position = columnIdx,
                cards = updatedCards
            )
        }

        return boardRepository.updateBoardData(boardId, updatedColumns)
    }

}