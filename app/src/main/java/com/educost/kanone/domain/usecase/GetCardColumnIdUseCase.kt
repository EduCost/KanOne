package com.educost.kanone.domain.usecase

import com.educost.kanone.domain.repository.CardRepository

class GetCardColumnIdUseCase(val cardRepository: CardRepository) {

    suspend operator fun invoke(cardId: Long) = cardRepository.getCardColumnId(cardId)

}