package com.educost.kanone.data.repository

import com.educost.kanone.data.local.TaskDao
import com.educost.kanone.data.mapper.toTaskEntity
import com.educost.kanone.domain.model.Task
import com.educost.kanone.domain.repository.TaskRepository

class TaskRepositoryImpl(val taskDao: TaskDao) : TaskRepository {

    override suspend fun createTask(
        task: Task,
        cardId: Long
    ): Boolean {
        return try {
            taskDao.createTask(task.toTaskEntity(cardId))
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    override suspend fun updateTask(task: Task, cardId: Long): Boolean {
        return try {
            taskDao.updateTask(task.toTaskEntity(cardId))
            true
        } catch (e: Exception) {
            e.printStackTrace()
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
            e.printStackTrace()
            false
        }
    }

}