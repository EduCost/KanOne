package com.educost.kanone.data.repository

import com.educost.kanone.data.local.TaskDao
import com.educost.kanone.data.mapper.toTaskEntity
import com.educost.kanone.domain.logs.LogHandler
import com.educost.kanone.domain.logs.LogLevel
import com.educost.kanone.domain.logs.LogLocation
import com.educost.kanone.domain.model.Task
import com.educost.kanone.domain.repository.TaskRepository

class TaskRepositoryImpl(
    private val taskDao: TaskDao,
    private val logHandler: LogHandler
) : TaskRepository {

    override suspend fun createTask(
        task: Task,
        cardId: Long
    ): Boolean {
        return try {
            taskDao.createTask(task.toTaskEntity(cardId))
            true
        } catch (e: Exception) {
            logHandler.log(
                throwable = e,
                message = "Error creating task",
                from = LogLocation.TASK_REPOSITORY,
                level = LogLevel.ERROR
            )

            false
        }
    }

    override suspend fun updateTask(task: Task, cardId: Long): Boolean {
        return try {
            taskDao.updateTask(task.toTaskEntity(cardId))
            true
        } catch (e: Exception) {
            logHandler.log(
                throwable = e,
                message = "Error updating task",
                from = LogLocation.TASK_REPOSITORY,
                level = LogLevel.ERROR
            )

            false
        }
    }

    override suspend fun deleteTask(
        task: Task,
        cardId: Long
    ): Boolean {
        return try {
            taskDao.deleteTask(task.toTaskEntity(cardId))
            true
        } catch (e: Exception) {
            logHandler.log(
                throwable = e,
                message = "Error deleting task",
                from = LogLocation.TASK_REPOSITORY,
                level = LogLevel.ERROR
            )

            false
        }
    }

}