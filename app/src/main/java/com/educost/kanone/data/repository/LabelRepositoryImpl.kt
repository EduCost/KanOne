package com.educost.kanone.data.repository

import android.util.Log
import com.educost.kanone.data.local.LabelDao
import com.educost.kanone.data.mapper.toLabel
import com.educost.kanone.domain.error.FetchDataError
import com.educost.kanone.domain.model.Label
import com.educost.kanone.domain.repository.LabelRepository
import com.educost.kanone.utils.LogTags
import com.educost.kanone.utils.Result
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

class LabelRepositoryImpl(val labelDao: LabelDao) : LabelRepository {

    override fun observeLabels(cardIds: List<Long>): Flow<Result<List<Label>, FetchDataError>> {
        return labelDao.observeLabels(cardIds).map { labels ->
            Result.Success(labels.map { it.toLabel() })

        }.catch { e ->
            when (e) {
                is IOException -> Result.Error(FetchDataError.IO_ERROR)
                else -> {
                    Log.e(LogTags.LABEL_REPO, e.stackTraceToString())
                    Result.Error(FetchDataError.UNKNOWN)
                }
            }
        }
    }
}