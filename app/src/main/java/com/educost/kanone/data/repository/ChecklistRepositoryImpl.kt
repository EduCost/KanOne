package com.educost.kanone.data.repository

import android.util.Log
import com.educost.kanone.data.local.ChecklistDao
import com.educost.kanone.data.mapper.toChecklist
import com.educost.kanone.domain.error.LocalDataError
import com.educost.kanone.domain.model.Checklist
import com.educost.kanone.domain.repository.ChecklistRepository
import com.educost.kanone.utils.LogTags
import com.educost.kanone.utils.Result
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

class ChecklistRepositoryImpl(val checklistDao: ChecklistDao) : ChecklistRepository {

    override fun observeChecklists(cardIds: List<Long>): Flow<Result<List<Checklist>, LocalDataError>> {
        return checklistDao.observeChecklists(cardIds).map { checklists ->
            Result.Success(checklists.map { it.toChecklist() })

        }.catch { e ->
            when (e) {
                is IOException -> Result.Error(LocalDataError.IO_ERROR)
                else -> {
                    Log.e(LogTags.CHECKLIST_REPO, e.stackTraceToString())
                    Result.Error(LocalDataError.UNKNOWN)
                }
            }
        }
    }
}