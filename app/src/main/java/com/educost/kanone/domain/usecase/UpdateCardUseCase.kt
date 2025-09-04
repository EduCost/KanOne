package com.educost.kanone.domain.usecase

import com.educost.kanone.domain.model.CardItem
import com.educost.kanone.domain.repository.CardRepository
import com.educost.kanone.utils.Result

class UpdateCardUseCase(val cardRepository: CardRepository) {

    suspend operator fun invoke(card: CardItem): Boolean {
        val columnIdResult = cardRepository.getCardColumnId(card.id)

        if (columnIdResult is Result.Success) {
            val columnId = columnIdResult.data

            val wasCardUpdated = cardRepository.updateCard(card, columnId)

            if (wasCardUpdated) return true

        }

        return false
    }

}