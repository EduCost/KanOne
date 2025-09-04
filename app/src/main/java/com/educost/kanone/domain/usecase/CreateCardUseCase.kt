package com.educost.kanone.domain.usecase

import com.educost.kanone.domain.model.CardItem
import com.educost.kanone.domain.repository.CardRepository

class CreateCardUseCase(val cardRepository: CardRepository) {

    suspend operator fun invoke(card: CardItem, columnId: Long): Boolean {
        return cardRepository.createCard(card, columnId)
    }
}