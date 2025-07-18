package com.educost.kanone.domain.repository

import com.educost.kanone.domain.error.FetchDataError
import com.educost.kanone.domain.model.CardItem
import com.educost.kanone.utils.Result
import kotlinx.coroutines.flow.Flow

interface CardRepository {

    fun observeCards(columnIds: List<Long>): Flow<Result<List<CardItem>, FetchDataError>>
}