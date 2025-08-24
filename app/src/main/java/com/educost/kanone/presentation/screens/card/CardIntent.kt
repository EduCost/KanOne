package com.educost.kanone.presentation.screens.card

sealed interface CardIntent {

    data class ObserveCard(val cardId: Long) : CardIntent

    // Description
    data object StartEditingDescription : CardIntent
    data class OnDescriptionChanged(val description: String) : CardIntent
    data object SaveDescription : CardIntent
    data object CancelEditingDescription : CardIntent


    // Create Task
    data object StartCreatingTask : CardIntent
    data class OnCreateTaskDescriptionChanged(val description: String) : CardIntent
    data class OnCreateTaskIsCompletedChanged(val isCompleted: Boolean) : CardIntent
    data object ConfirmTaskCreation : CardIntent
    data object CancelCreatingTask : CardIntent

    // Edit Task
    data class StartEditingTask(val taskId: Long) : CardIntent
    data class OnTaskDescriptionChange(val taskId: Long, val description: String) : CardIntent
    data object SaveTask : CardIntent
    data object CancelEditingTask : CardIntent

    data class OnTaskCheckedChange(val taskId: Long, val checked: Boolean) : CardIntent
    data object RemoveTask : CardIntent



}