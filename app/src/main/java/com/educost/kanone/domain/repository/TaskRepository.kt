package com.educost.kanone.domain.repository

import com.educost.kanone.domain.error.FetchDataError
import com.educost.kanone.domain.error.InsertDataError
import com.educost.kanone.domain.model.Task
import com.educost.kanone.utils.Result
import kotlinx.coroutines.flow.Flow

interface TaskRepository {

    suspend fun createTask(task: Task, cardId: Long): Result<Unit, InsertDataError>

    suspend fun updateTask(task: Task, cardId: Long): Result<Unit, InsertDataError>

}