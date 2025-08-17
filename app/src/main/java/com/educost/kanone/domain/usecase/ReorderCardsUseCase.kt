package com.educost.kanone.domain.usecase

import com.educost.kanone.domain.error.InsertDataError
import com.educost.kanone.domain.model.KanbanColumn
import com.educost.kanone.domain.repository.CardRepository
import com.educost.kanone.presentation.screens.board.utils.CardOrder
import com.educost.kanone.presentation.screens.board.utils.OrderType
import com.educost.kanone.utils.Result

class ReorderCardsUseCase(val cardRepository: CardRepository) {

    suspend operator fun invoke(
        column: KanbanColumn,
        orderType: OrderType,
        cardOrder: CardOrder
    ): Result<Unit, InsertDataError> {

        val cards = column.cards

        val sortedCards = when (orderType) {
            OrderType.ASCENDING -> {
                when (cardOrder) {
                    CardOrder.NAME -> cards.sortedBy { it.title }
                    CardOrder.DUE_DATE -> cards.sortedBy { it.dueDate }
                    CardOrder.DATE_CREATED -> cards.sortedBy { it.createdAt }
                }
            }
            OrderType.DESCENDING -> {
                when (cardOrder) {
                    CardOrder.NAME -> cards.sortedByDescending { it.title }
                    CardOrder.DUE_DATE -> cards.sortedByDescending { it.dueDate }
                    CardOrder.DATE_CREATED -> cards.sortedByDescending { it.createdAt }
                }
            }
        }

        val newPositions = sortedCards.mapIndexed { index, card ->
            card.copy(position = index)
        }

        return cardRepository.updateCards(newPositions, column.id)
    }
}