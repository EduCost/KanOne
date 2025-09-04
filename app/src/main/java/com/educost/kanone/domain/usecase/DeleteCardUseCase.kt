package com.educost.kanone.domain.usecase

import com.educost.kanone.domain.model.CardItem
import com.educost.kanone.domain.repository.CardRepository
import com.educost.kanone.utils.Result

class DeleteCardUseCase(val repository: CardRepository) {

    suspend operator fun invoke(card: CardItem): Boolean {
        val columnIdResult = repository.getCardColumnId(card.id)

        if (columnIdResult is Result.Success) {
            val columnId = columnIdResult.data
            return repository.deleteCard(card, columnId)
        }

        return false

    }

}