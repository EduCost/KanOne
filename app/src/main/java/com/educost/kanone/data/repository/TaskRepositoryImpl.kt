package com.educost.kanone.data.repository

import android.util.Log
import com.educost.kanone.data.local.TaskDao
import com.educost.kanone.data.mapper.toTask
import com.educost.kanone.domain.error.FetchDataError
import com.educost.kanone.domain.model.Task
import com.educost.kanone.domain.repository.TaskRepository
import com.educost.kanone.utils.LogTags
import com.educost.kanone.utils.Result
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

class TaskRepositoryImpl(val taskDao: TaskDao) : TaskRepository {

    override fun observeTasks(cardIds: List<Long>): Flow<Result<List<Task>, FetchDataError>> {
        return taskDao.observeTasks(cardIds).map { task ->
            Result.Success(task.map { it.toTask() })

        }.catch { e ->
            when (e) {
                is IOException -> Result.Error(FetchDataError.IO_ERROR)
                else -> {
                    Log.e(LogTags.TASK_REPO, e.stackTraceToString())
                    Result.Error(FetchDataError.UNKNOWN)
                }
            }
        }
    }
}