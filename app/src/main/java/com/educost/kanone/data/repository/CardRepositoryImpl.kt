package com.educost.kanone.data.repository

import android.util.Log
import com.educost.kanone.data.local.CardDao
import com.educost.kanone.data.mapper.toCardEntity
import com.educost.kanone.data.mapper.toCardItem
import com.educost.kanone.domain.error.FetchDataError
import com.educost.kanone.domain.error.InsertDataError
import com.educost.kanone.domain.model.CardItem
import com.educost.kanone.domain.repository.CardRepository
import com.educost.kanone.utils.LogTags
import com.educost.kanone.utils.Result
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

class CardRepositoryImpl(val cardDao: CardDao) : CardRepository {

    override fun observeCard(cardId: Long): Flow<Result<CardItem, FetchDataError>> {
        return cardDao.observeCard(cardId)
            .map { card ->
                Result.Success(card.toCardItem())
            }
            .catch { e ->
                when (e) {
                    is IOException -> Result.Error(FetchDataError.IO_ERROR)

                    else -> {
                        Log.e(LogTags.CARD_REPO, e.stackTraceToString())
                        Result.Error(FetchDataError.UNKNOWN)
                    }
                }
            }
    }

    override suspend fun createCard(
        card: CardItem,
        columnId: Long
    ): Result<Long, InsertDataError> {
        return try {
            val cardId = cardDao.createCard(card.toCardEntity(columnId))
            Result.Success(cardId)
        } catch (e: Exception) {
            when (e) {
                is IOException -> Result.Error(InsertDataError.IO_ERROR)
                else -> {
                    Log.e(LogTags.CARD_REPO, e.stackTraceToString())
                    Result.Error(InsertDataError.UNKNOWN)
                }
            }
        }
    }

    override suspend fun updateCards(
        cards: List<CardItem>,
        columnId: Long
    ): Result<Unit, InsertDataError> {
        return try {
            val cardsEntities = cards.map { it.toCardEntity(columnId) }
            cardDao.updateCards(cardsEntities)
            Result.Success(Unit)
        } catch (e: Exception) {
            when (e) {
                is IOException -> Result.Error(InsertDataError.IO_ERROR)
                else -> {
                    Log.e(LogTags.CARD_REPO, e.stackTraceToString())
                    Result.Error(InsertDataError.UNKNOWN)
                }
            }
        }
    }
}