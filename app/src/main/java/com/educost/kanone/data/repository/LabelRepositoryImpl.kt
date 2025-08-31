package com.educost.kanone.data.repository

import com.educost.kanone.data.local.LabelDao
import com.educost.kanone.data.mapper.toLabel
import com.educost.kanone.domain.error.GenericError
import com.educost.kanone.domain.model.Label
import com.educost.kanone.domain.repository.LabelRepository
import com.educost.kanone.utils.Result
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map

class LabelRepositoryImpl(val labelDao: LabelDao) : LabelRepository {

    override fun observeLabels(cardId: Long): Flow<Result<List<Label>, GenericError>> {
        return labelDao.observeLabels(cardId).map { labels ->
            Result.Success(labels.map { it.toLabel() })

        }.catch { e ->
            e.printStackTrace()
            Result.Error(GenericError)
        }
    }
}