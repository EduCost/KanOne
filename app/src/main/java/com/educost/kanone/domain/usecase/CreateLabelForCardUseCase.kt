package com.educost.kanone.domain.usecase

import com.educost.kanone.domain.model.Label
import com.educost.kanone.domain.repository.LabelRepository
import com.educost.kanone.utils.Result

class CreateLabelForCardUseCase(val repository: LabelRepository) {

    suspend operator fun invoke(label: Label, cardId: Long): Boolean {
        val boardIdResult = repository.getBoardId(cardId)

        if (boardIdResult is Result.Success) {
            val boardId = boardIdResult.data
            return repository.createLabelAndAssociateWithCard(label, boardId, cardId)
        }

        return false
    }

}