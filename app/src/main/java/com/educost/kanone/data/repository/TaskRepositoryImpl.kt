package com.educost.kanone.data.repository

import android.util.Log
import com.educost.kanone.data.local.TaskDao
import com.educost.kanone.data.mapper.toTask
import com.educost.kanone.data.mapper.toTaskEntity
import com.educost.kanone.domain.error.FetchDataError
import com.educost.kanone.domain.error.InsertDataError
import com.educost.kanone.domain.model.Task
import com.educost.kanone.domain.repository.TaskRepository
import com.educost.kanone.utils.LogTags
import com.educost.kanone.utils.Result
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

class TaskRepositoryImpl(val taskDao: TaskDao) : TaskRepository {

    override suspend fun createTask(
        task: Task,
        cardId: Long
    ): Result<Unit, InsertDataError> {
        return try {
            taskDao.createTask(task.toTaskEntity(cardId))
            Result.Success(Unit)
        } catch (_: IOException) {
            Result.Error(InsertDataError.IO_ERROR)
        } catch (e: Exception) {
            Log.e(LogTags.TASK_REPO, e.stackTraceToString())
            Result.Error(InsertDataError.UNKNOWN)
        }
    }

    override suspend fun updateTask(task: Task, cardId: Long): Result<Unit, InsertDataError> {
        return try {
            taskDao.updateTask(task.toTaskEntity(cardId))
            Result.Success(Unit)
        } catch (_: IOException) {
            Result.Error(InsertDataError.IO_ERROR)
        } catch (e: Exception) {
            Log.e(LogTags.TASK_REPO, e.stackTraceToString())
            Result.Error(InsertDataError.UNKNOWN)
        }
    }

}