package com.educost.kanone.domain.usecase

import com.educost.kanone.domain.repository.LabelRepository

class UpdateLabelAssociationUseCase(val repository: LabelRepository) {

    suspend operator fun invoke(labelId: Long, cardId: Long): Boolean {
        val isLabelAssociated = repository.hasLabelAssociation(labelId, cardId)

        if (isLabelAssociated) {
            val wasAssociationDeleted = repository.deleteLabelAssociation(labelId, cardId)
            return wasAssociationDeleted
        } else {
            val wasAssociationCreated = repository.associateLabelWithCard(labelId, cardId)
            return wasAssociationCreated
        }
    }

}