package com.educost.kanone.domain.usecase

import com.educost.kanone.domain.repository.CardRepository
import com.educost.kanone.utils.Result
import jakarta.inject.Inject

class UpdateMarkdownUseCase @Inject constructor(
    private val cardRepository: CardRepository,
    private val updateCardUseCase: UpdateCardUseCase
) {
    suspend operator fun invoke(cardId: Long, markdown: String): Boolean {
        val cardResult = cardRepository.getCard(cardId)

        if (cardResult is Result.Success) {
            val updatedCard = cardResult.data.copy(description = markdown)

            return updateCardUseCase(updatedCard)
        }

        return false
    }
}