package com.educost.kanone.data.repository

import android.util.Log
import com.educost.kanone.data.local.CardDao
import com.educost.kanone.data.mapper.toCardItem
import com.educost.kanone.domain.error.LocalDataError
import com.educost.kanone.domain.model.CardItem
import com.educost.kanone.domain.repository.CardRepository
import com.educost.kanone.utils.LogTags
import com.educost.kanone.utils.Result
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

class CardRepositoryImpl(val cardDao: CardDao) : CardRepository {

    override fun observeCards(columnIds: List<Long>): Flow<Result<List<CardItem>, LocalDataError>> {
        return cardDao.observeCards(columnIds).map { cards ->
            Result.Success(cards.map { it.toCardItem() })

        }.catch { e ->
            when (e) {
                is IOException -> Result.Error(LocalDataError.IO_ERROR)
                else -> {
                    Log.e(LogTags.CARD_REPO, e.stackTraceToString())
                    Result.Error(LocalDataError.UNKNOWN)
                }
            }
        }
    }
}