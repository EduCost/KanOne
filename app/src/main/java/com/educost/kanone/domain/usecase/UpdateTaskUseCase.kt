package com.educost.kanone.domain.usecase

import com.educost.kanone.domain.model.Task
import com.educost.kanone.domain.repository.TaskRepository

class UpdateTaskUseCase(val taskRepository: TaskRepository) {

    suspend operator fun invoke(task: Task, cardId: Long) = taskRepository.updateTask(task, cardId)

}