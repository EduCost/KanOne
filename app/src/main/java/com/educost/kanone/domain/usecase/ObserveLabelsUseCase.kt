package com.educost.kanone.domain.usecase

import com.educost.kanone.domain.repository.LabelRepository

class ObserveLabelsUseCase(val labelRepository: LabelRepository) {

    operator fun invoke(cardId: Long) = labelRepository.observeLabels(cardId)

}