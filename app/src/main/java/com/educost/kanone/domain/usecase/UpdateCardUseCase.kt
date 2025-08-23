package com.educost.kanone.domain.usecase

import com.educost.kanone.domain.model.CardItem
import com.educost.kanone.domain.repository.CardRepository

class UpdateCardUseCase(val cardRepository: CardRepository) {

    suspend operator fun invoke(card: CardItem, columnId: Long) =
        cardRepository.updateCard(card, columnId)

}