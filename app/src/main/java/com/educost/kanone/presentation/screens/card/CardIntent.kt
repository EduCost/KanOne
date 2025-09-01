package com.educost.kanone.presentation.screens.card

import com.educost.kanone.domain.model.Attachment
import com.educost.kanone.domain.model.Label
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


    // Attachments
    data object StartCreatingAttachment : CardIntent
    data class SaveImage(val imageUri: String, val shouldAddToCover: Boolean) : CardIntent
    data object CancelCreatingAttachment : CardIntent
    data class OpenImage(val attachment: Attachment) : CardIntent
    data class DeleteImage(val attachment: Attachment) : CardIntent
    data object CloseImage : CardIntent


    // Labels
    data object OpenLabelPicker : CardIntent
    data object CloseLabelPicker : CardIntent
    data object StartCreatingLabel : CardIntent
    data class CreateLabel(val label: Label) : CardIntent
    data class UpdateLabelAssociation(val label: Label) : CardIntent
    data class StartEditingLabel(val label: Label) : CardIntent
    data class ConfirmLabelEdit(val label: Label) : CardIntent
    data object CloseLabelDialog : CardIntent


    // Date Picker
    data object ShowDatePicker : CardIntent
    data object HideDatePicker : CardIntent
    data class OnDateSelected(val date: LocalDateTime?) : CardIntent




}