package com.educost.kanone.data.repository

import com.educost.kanone.data.local.CardDao
import com.educost.kanone.data.mapper.toCardEntity
import com.educost.kanone.data.mapper.toCardItem
import com.educost.kanone.domain.error.GenericError
import com.educost.kanone.domain.model.CardItem
import com.educost.kanone.domain.repository.CardRepository
import com.educost.kanone.utils.Result
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map

class CardRepositoryImpl(val cardDao: CardDao) : CardRepository {

    override fun observeCard(cardId: Long): Flow<Result<CardItem, GenericError>> {
        return cardDao.observeCard(cardId)
            .map { card ->
                Result.Success(card.toCardItem())
            }
            .catch { e ->
                e.printStackTrace()
                Result.Error(GenericError)
            }
    }

    override suspend fun createCard(
        card: CardItem,
        columnId: Long
    ): Boolean {
        return try {
            cardDao.createCard(card.toCardEntity(columnId))
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    override suspend fun updateCards(
        cards: List<CardItem>,
        columnId: Long
    ): Boolean {
        return try {
            val cardsEntities = cards.map { it.toCardEntity(columnId) }
            cardDao.updateCards(cardsEntities)
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    override suspend fun updateCard(
        card: CardItem,
        columnId: Long
    ): Boolean {
        return try {
            cardDao.updateCard(card.toCardEntity(columnId))
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    override suspend fun getCardColumnId(cardId: Long): Result<Long, GenericError> {
        return try {
            val columnId = cardDao.getCardColumnId(cardId)
            Result.Success(columnId)
        } catch (e: Exception) {
            e.printStackTrace()
            Result.Error(GenericError)
        }

    }
}