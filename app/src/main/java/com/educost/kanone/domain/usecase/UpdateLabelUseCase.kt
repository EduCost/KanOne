package com.educost.kanone.domain.usecase

import com.educost.kanone.domain.model.Label
import com.educost.kanone.domain.repository.LabelRepository
import com.educost.kanone.utils.Result

class UpdateLabelUseCase(val repository: LabelRepository) {

    suspend operator fun invoke(label: Label, cardId: Long): Boolean {
        val boardIdResult = repository.getBoardId(cardId)

        return if (boardIdResult is Result.Success) {
            val boardId = boardIdResult.data
            repository.updateLabel(label, boardId)
        } else {
            false
        }

    }

}