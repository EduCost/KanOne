package com.educost.kanone.domain.usecase

import com.educost.kanone.domain.model.KanbanColumn
import com.educost.kanone.domain.repository.CardRepository
import com.educost.kanone.presentation.screens.board.utils.CardOrder
import com.educost.kanone.presentation.screens.board.utils.OrderType

class ReorderCardsUseCase(val cardRepository: CardRepository) {

    suspend operator fun invoke(
        column: KanbanColumn?,
        orderType: OrderType,
        cardOrder: CardOrder
    ): Boolean {

        if (column == null) return false

        val cards = column.cards

        if (cards.isEmpty()) return true


        val sortedCards = when (orderType) {
            OrderType.ASCENDING -> {
                when (cardOrder) {
                    CardOrder.NAME -> cards.sortedBy { it.title.lowercase() }
                    CardOrder.DATE_CREATED -> cards.sortedBy { it.createdAt }
                    CardOrder.DUE_DATE -> {
                        val nonNullDueDates = cards.filter { it.dueDate != null }
                        val nullDueDates = cards.filter { it.dueDate == null }
                        val sortedNonNullDueDates = nonNullDueDates.sortedBy { it.dueDate }
                        sortedNonNullDueDates + nullDueDates
                    }
                }
            }
            OrderType.DESCENDING -> {
                when (cardOrder) {
                    CardOrder.NAME -> cards.sortedByDescending { it.title.lowercase() }
                    CardOrder.DATE_CREATED -> cards.sortedByDescending { it.createdAt }
                    CardOrder.DUE_DATE -> {
                        val nonNullDueDates = cards.filter { it.dueDate != null }
                        val nullDueDates = cards.filter { it.dueDate == null }
                        val sortedNonNullDueDates = nonNullDueDates.sortedByDescending { it.dueDate }
                        sortedNonNullDueDates + nullDueDates
                    }
                }
            }
        }

        val newPositions = sortedCards.mapIndexed { index, card ->
            card.copy(position = index)
        }

        return cardRepository.updateCards(newPositions, column.id)
    }
}