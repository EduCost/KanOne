package com.educost.kanone.data.repository

import com.educost.kanone.data.local.CardDao
import com.educost.kanone.data.mapper.toCardEntity
import com.educost.kanone.data.mapper.toCardItem
import com.educost.kanone.domain.error.GenericError
import com.educost.kanone.domain.logs.LogHandler
import com.educost.kanone.domain.logs.LogLevel
import com.educost.kanone.domain.logs.LogLocation
import com.educost.kanone.domain.model.CardItem
import com.educost.kanone.domain.repository.CardRepository
import com.educost.kanone.utils.Result
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map

class CardRepositoryImpl(
    private val cardDao: CardDao,
    private val logHandler: LogHandler
) : CardRepository {

    override fun observeCard(cardId: Long): Flow<Result<CardItem, GenericError>> {
        return cardDao.observeCard(cardId)
            .map { card ->
                Result.Success(card.toCardItem())
            }
            .catch { e ->
                logHandler.log(
                    throwable = e,
                    message = "Error fetching card",
                    from = LogLocation.CARD_REPOSITORY,
                    level = LogLevel.ERROR
                )

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
            logHandler.log(
                throwable = e,
                message = "Error creating card",
                from = LogLocation.CARD_REPOSITORY,
                level = LogLevel.ERROR
            )

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
            logHandler.log(
                throwable = e,
                message = "Error updating cards",
                from = LogLocation.CARD_REPOSITORY,
                level = LogLevel.ERROR
            )

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
            logHandler.log(
                throwable = e,
                message = "Error updating card",
                from = LogLocation.CARD_REPOSITORY,
                level = LogLevel.ERROR
            )

            false
        }
    }

    override suspend fun getCardColumnId(cardId: Long): Result<Long, GenericError> {
        return try {
            val columnId = cardDao.getCardColumnId(cardId)
            Result.Success(columnId)
        } catch (e: Exception) {
            logHandler.log(
                throwable = e,
                message = null,
                from = LogLocation.CARD_REPOSITORY,
                level = LogLevel.ERROR
            )
            Result.Error(GenericError)
        }

    }

    override suspend fun deleteCard(
        card: CardItem,
        columnId: Long
    ): Boolean {
        return try {
            cardDao.deleteCard(card.toCardEntity(columnId))
            true
        } catch (e: Exception) {
            logHandler.log(
                throwable = e,
                message = "Error deleting card",
                from = LogLocation.CARD_REPOSITORY,
                level = LogLevel.ERROR
            )

            false
        }
    }

    override suspend fun getCard(cardId: Long): Result<CardItem, GenericError> {
        return try {
            val card = cardDao.getCard(cardId)
            Result.Success(card.toCardItem())
        } catch (e: Exception) {
            logHandler.log(
                throwable = e,
                message = "Error getting card",
                from = LogLocation.CARD_REPOSITORY,
                level = LogLevel.ERROR
            )

            Result.Error(GenericError)
        }
    }
}