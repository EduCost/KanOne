package com.educost.kanone.domain.usecase

import com.educost.kanone.domain.model.CardItem
import com.educost.kanone.domain.repository.CardRepository
import java.time.LocalDateTime

class CreateCardUseCase(val cardRepository: CardRepository) {

    suspend operator fun invoke(title: String?, position: Int?, columnId: Long?): CreateCardResult {

        if (title.isNullOrBlank()) return CreateCardResult.EMPTY_TITLE
        if (columnId == null) return CreateCardResult.GENERIC_ERROR
        if (position == null) return CreateCardResult.GENERIC_ERROR

        val card = CardItem(
            id = 0,
            title = title,
            position = position,
            createdAt = LocalDateTime.now()
        )

        val wasCardCreated = cardRepository.createCard(card, columnId)

        if (!wasCardCreated) return CreateCardResult.GENERIC_ERROR

        return CreateCardResult.SUCCESS
    }
}

enum class CreateCardResult {
    SUCCESS,
    EMPTY_TITLE,
    GENERIC_ERROR
}