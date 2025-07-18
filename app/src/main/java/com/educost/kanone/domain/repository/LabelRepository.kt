package com.educost.kanone.domain.repository

import com.educost.kanone.domain.error.FetchDataError
import com.educost.kanone.domain.model.Label
import com.educost.kanone.utils.Result
import kotlinx.coroutines.flow.Flow

interface LabelRepository {

    fun observeLabels(cardIds: List<Long>): Flow<Result<List<Label>, FetchDataError>>
}