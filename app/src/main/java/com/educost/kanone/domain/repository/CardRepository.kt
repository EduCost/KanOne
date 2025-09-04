package com.educost.kanone.domain.repository

import com.educost.kanone.domain.error.GenericError
import com.educost.kanone.domain.model.CardItem
import com.educost.kanone.utils.Result
import kotlinx.coroutines.flow.Flow

interface CardRepository {

    fun observeCard(cardId: Long): Flow<Result<CardItem, GenericError>>

    suspend fun createCard(card: CardItem, columnId: Long): Boolean

    suspend fun updateCards(cards: List<CardItem>, columnId: Long): Boolean

    suspend fun updateCard(card: CardItem, columnId: Long): Boolean

    suspend fun getCardColumnId(cardId: Long): Result<Long, GenericError>
}