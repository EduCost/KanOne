package com.educost.kanone.domain.usecase

import com.educost.kanone.domain.repository.CardRepository

class ObserveCardUseCase(val cardRepository: CardRepository) {

    operator fun invoke(cardId: Long) = cardRepository.observeCard(cardId)

}