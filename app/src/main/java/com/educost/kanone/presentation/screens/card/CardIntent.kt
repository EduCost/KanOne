package com.educost.kanone.presentation.screens.card

import java.time.LocalDateTime

sealed interface CardIntent {

    data class ObserveCard(val cardId: Long) : CardIntent

    data object OnNavigateBack : CardIntent


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
    data object ConfirmTaskEdit : CardIntent
    data object CancelEditingTask : CardIntent

    data class OnTaskCheckedChange(val taskId: Long, val isChecked: Boolean) : CardIntent
    data class DeleteTask(val taskId: Long) : CardIntent


    // Date Picker
    data object ShowDatePicker : CardIntent
    data object HideDatePicker : CardIntent
    data class OnDateSelected(val date: LocalDateTime?) : CardIntent




}