package com.educost.kanone.domain.repository

import com.educost.kanone.domain.error.FetchDataError
import com.educost.kanone.domain.error.InsertDataError
import com.educost.kanone.domain.model.CardItem
import com.educost.kanone.utils.Result
import kotlinx.coroutines.flow.Flow

interface CardRepository {

    fun observeCard(cardId: Long): Flow<Result<CardItem, FetchDataError>>

    suspend fun createCard(card: CardItem, columnId: Long): Result<Long, InsertDataError>

    suspend fun updateCards(cards: List<CardItem>, columnId: Long): Result<Unit, InsertDataError>
}