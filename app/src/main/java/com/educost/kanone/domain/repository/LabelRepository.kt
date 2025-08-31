package com.educost.kanone.domain.repository

import com.educost.kanone.domain.error.GenericError
import com.educost.kanone.domain.model.Label
import com.educost.kanone.utils.Result
import kotlinx.coroutines.flow.Flow

interface LabelRepository {

    fun observeLabels(cardId: Long): Flow<Result<List<Label>, GenericError>>

}