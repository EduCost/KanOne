package com.educost.kanone.domain.usecase

import com.educost.kanone.domain.error.InsertDataError
import com.educost.kanone.domain.model.CardItem
import com.educost.kanone.domain.repository.CardRepository
import com.educost.kanone.utils.Result

class CreateCardUseCase(val cardRepository: CardRepository) {

    suspend operator fun invoke(card: CardItem, columnId: Long): Result<Long, InsertDataError> {
        return cardRepository.createCard(card, columnId)
    }
}