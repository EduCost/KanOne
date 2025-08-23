package com.educost.kanone.domain.repository

import com.educost.kanone.domain.error.FetchDataError
import com.educost.kanone.domain.model.Task
import com.educost.kanone.utils.Result
import kotlinx.coroutines.flow.Flow

interface TaskRepository {

    fun observeTasks(cardIds: List<Long>): Flow<Result<List<Task>, FetchDataError>>
}