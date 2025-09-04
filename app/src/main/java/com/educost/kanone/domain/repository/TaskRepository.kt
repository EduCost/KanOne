package com.educost.kanone.domain.repository

import com.educost.kanone.domain.model.Task

interface TaskRepository {

    suspend fun createTask(task: Task, cardId: Long): Boolean

    suspend fun updateTask(task: Task, cardId: Long): Boolean

    suspend fun deleteTask(task: Task, cardId: Long): Boolean

}