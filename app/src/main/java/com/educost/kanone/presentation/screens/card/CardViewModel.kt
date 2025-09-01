package com.educost.kanone.presentation.screens.card

import androidx.compose.material3.SnackbarDuration
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.educost.kanone.R
import com.educost.kanone.dispatchers.DispatcherProvider
import com.educost.kanone.domain.model.Attachment
import com.educost.kanone.domain.model.Label
import com.educost.kanone.domain.model.Task
import com.educost.kanone.domain.usecase.CreateAttachmentUseCase
import com.educost.kanone.domain.usecase.CreateLabelForCardUseCase
import com.educost.kanone.domain.usecase.CreateTaskUseCase
import com.educost.kanone.domain.usecase.DeleteAttachmentUseCase
import com.educost.kanone.domain.usecase.DeleteImageUseCase
import com.educost.kanone.domain.usecase.DeleteTaskUseCase
import com.educost.kanone.domain.usecase.GetCardColumnIdUseCase
import com.educost.kanone.domain.usecase.ObserveCardUseCase
import com.educost.kanone.domain.usecase.ObserveLabelsUseCase
import com.educost.kanone.domain.usecase.SaveImageUseCase
import com.educost.kanone.domain.usecase.UpdateCardUseCase
import com.educost.kanone.domain.usecase.UpdateLabelAssociationUseCase
import com.educost.kanone.domain.usecase.UpdateTaskUseCase
import com.educost.kanone.presentation.screens.card.utils.CardAppBarType
import com.educost.kanone.presentation.util.SnackbarAction
import com.educost.kanone.presentation.util.SnackbarEvent
import com.educost.kanone.presentation.util.UiText
import com.educost.kanone.utils.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDateTime

@HiltViewModel
class CardViewModel @Inject constructor(
    private val dispatcherProvider: DispatcherProvider,
    private val observeCardUseCase: ObserveCardUseCase,
    private val updateCardUseCase: UpdateCardUseCase,
    private val getCardColumnIdUseCase: GetCardColumnIdUseCase,
    private val createTaskUseCase: CreateTaskUseCase,
    private val updateTaskUseCase: UpdateTaskUseCase,
    private val deleteTaskUseCase: DeleteTaskUseCase,
    private val saveImageUseCase: SaveImageUseCase,
    private val createAttachmentUseCase: CreateAttachmentUseCase,
    private val deleteImageUseCase: DeleteImageUseCase,
    private val deleteAttachmentUseCase: DeleteAttachmentUseCase,
    private val observeLabelsUseCase: ObserveLabelsUseCase,
    private val createLabelForCardUseCase: CreateLabelForCardUseCase,
    private val updateLabelAssociationUseCase: UpdateLabelAssociationUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(CardUiState())
    val uiState = _uiState.asStateFlow()

    private val _sideEffectChannel = Channel<CardSideEffect>(Channel.BUFFERED)
    val sideEffectFlow = _sideEffectChannel.receiveAsFlow()

    fun onIntent(intent: CardIntent) {
        when (intent) {
            is CardIntent.ObserveCard -> observeCard(intent.cardId)
            is CardIntent.OnNavigateBack -> onNavigateBack()

            // Description
            is CardIntent.StartEditingDescription -> startEditingDescription()
            is CardIntent.OnDescriptionChanged -> onDescriptionChanged(intent.description)
            is CardIntent.SaveDescription -> saveDescription()
            is CardIntent.CancelEditingDescription -> clearAllCreateAndEditStates()

            // Create Task
            is CardIntent.StartCreatingTask -> startCreatingTask()
            is CardIntent.OnCreateTaskDescriptionChanged -> onCreateTaskDescriptionChanged(intent.description)
            is CardIntent.OnCreateTaskIsCompletedChanged -> onCreateTaskIsCompletedChanged(intent.isCompleted)
            is CardIntent.ConfirmTaskCreation -> confirmTaskCreation()
            is CardIntent.CancelCreatingTask -> clearAllCreateAndEditStates()

            // Edit Task
            is CardIntent.StartEditingTask -> startEditingTask(intent.taskId)
            is CardIntent.OnTaskDescriptionChange -> onTaskDescriptionChange(intent.description)
            is CardIntent.ConfirmTaskEdit -> confirmTaskEdit()
            is CardIntent.CancelEditingTask -> clearAllCreateAndEditStates()
            is CardIntent.OnTaskCheckedChange -> onTaskCheckedChange(
                intent.taskId,
                intent.isChecked
            )

            is CardIntent.DeleteTask -> deleteTask(intent.taskId)

            // Attachments
            is CardIntent.StartCreatingAttachment -> startCreatingAttachment()
            is CardIntent.SaveImage -> saveImage(intent.imageUri, intent.shouldAddToCover)
            is CardIntent.OpenImage -> openImage(intent.attachment)
            is CardIntent.DeleteImage -> deleteImage(intent.attachment)
            is CardIntent.CloseImage -> clearAllCreateAndEditStates()
            is CardIntent.CancelCreatingAttachment -> clearAllCreateAndEditStates()

            // Labels
            is CardIntent.OpenLabelPicker -> openLabelPicker()
            is CardIntent.CloseLabelPicker -> clearAllCreateAndEditStates()
            is CardIntent.StartCreatingLabel -> startCreatingLabel()
            is CardIntent.CreateLabel -> createLabel(intent.label)
            is CardIntent.UpdateLabelAssociation -> updateLabelAssociation(intent.label)
            is CardIntent.CancelCreatingLabel -> clearAllCreateAndEditStates()


            // Date Picker
            is CardIntent.ShowDatePicker -> showDatePicker()
            is CardIntent.HideDatePicker -> clearAllCreateAndEditStates()
            is CardIntent.OnDateSelected -> onDateSelected(intent.date)
        }
    }


    private fun observeCard(cardId: Long) {
        viewModelScope.launch(dispatcherProvider.main) {
            observeCardUseCase(cardId).collect { result ->
                when (result) {
                    is Result.Success -> {
                        _uiState.value = _uiState.value.copy(card = result.data)
                        observeBoardLabels(result.data.id)
                    }

                    is Result.Error -> sendSnackbar(
                        SnackbarEvent(
                            message = UiText.StringResource(R.string.card_snackbar_observe_card_error),
                            withDismissAction = true,
                            duration = SnackbarDuration.Long
                        )
                    )
                }
            }
        }
    }

    private fun observeBoardLabels(cardId: Long) {
        viewModelScope.launch(dispatcherProvider.main) {
            observeLabelsUseCase(cardId).collect { result ->
                when (result) {
                    is Result.Success -> {
                        _uiState.update { it.copy(boardLabels = result.data) }
                    }

                    is Result.Error -> Unit
                }
            }
        }
    }

    private fun onNavigateBack() {
        viewModelScope.launch(dispatcherProvider.main) {
            _sideEffectChannel.send(CardSideEffect.OnNavigateBack)
        }
    }


    // Description
    private fun startEditingDescription() {
        clearAllCreateAndEditStates()
        _uiState.update {
            it.copy(
                appBarType = CardAppBarType.DESCRIPTION,
                newDescription = it.card?.description
            )
        }
    }

    private fun onDescriptionChanged(description: String) {
        _uiState.update { it.copy(newDescription = description) }
    }

    private fun saveDescription() {
        viewModelScope.launch(dispatcherProvider.main) {
            val card = _uiState.value.card!!

            val newDescription = _uiState.value.newDescription
            val columnId = getColumnId(card.id)

            if (newDescription != null && columnId != null) {

                val newCard = card.copy(description = newDescription)
                val result = updateCardUseCase(newCard, columnId)

                if (result is Result.Error) sendSnackbar(
                    SnackbarEvent(
                        message = UiText.StringResource(R.string.card_snackbar_save_description_error),
                        withDismissAction = true,
                        duration = SnackbarDuration.Long
                    )
                )

            } else sendSnackbar(
                SnackbarEvent(
                    message = UiText.StringResource(R.string.card_snackbar_save_description_error),
                    withDismissAction = true,
                )
            )

            clearAllCreateAndEditStates()
        }
    }


    // Create Task
    private fun startCreatingTask() {
        clearAllCreateAndEditStates()
        _uiState.update {
            it.copy(
                appBarType = CardAppBarType.ADD_TASK,
            )
        }
    }

    private fun onCreateTaskDescriptionChanged(description: String) {
        _uiState.update {
            it.copy(createTaskState = it.createTaskState.copy(description = description))
        }
    }

    private fun onCreateTaskIsCompletedChanged(isCompleted: Boolean) {
        _uiState.update {
            it.copy(createTaskState = it.createTaskState.copy(isCompleted = isCompleted))
        }
    }

    private fun confirmTaskCreation() {
        val card = _uiState.value.card!!

        viewModelScope.launch(dispatcherProvider.main) {

            val description = _uiState.value.createTaskState.description
            val isCompleted = _uiState.value.createTaskState.isCompleted

            if (description.isBlank()) {

                sendSnackbar(
                    SnackbarEvent(
                        message = UiText.StringResource(R.string.card_snackbar_create_task_with_empty_description_error),
                        withDismissAction = true,
                    )
                )

                return@launch
            }

            val task = Task(
                id = 0,
                description = description,
                isCompleted = isCompleted,
                position = card.tasks.size
            )

            val result = createTaskUseCase(task = task, cardId = card.id)

            if (result is Result.Error) sendSnackbar(
                SnackbarEvent(
                    message = UiText.StringResource(R.string.card_snackbar_create_task_error),
                    withDismissAction = true,
                )
            )

            clearAllCreateAndEditStates()
        }
    }


    // Edit task
    private fun startEditingTask(taskId: Long) {
        clearAllCreateAndEditStates()

        val task = _uiState.value.card?.tasks?.find { it.id == taskId }

        _uiState.update {
            it.copy(
                appBarType = CardAppBarType.EDIT_TASK,
                editTaskState = EditTaskState(
                    taskId = taskId,
                    description = task?.description ?: ""
                )
            )
        }
    }

    private fun onTaskDescriptionChange(description: String) {
        _uiState.update {
            it.copy(editTaskState = it.editTaskState.copy(description = description))
        }
    }

    private fun confirmTaskEdit() {
        val card = _uiState.value.card!!

        val taskId = _uiState.value.editTaskState.taskId
        val newDescription = _uiState.value.editTaskState.description
        val task = card.tasks.find { it.id == taskId }

        if (task == null) {
            sendSnackbar(
                SnackbarEvent(
                    message = UiText.StringResource(R.string.card_snackbar_edit_task_error),
                    withDismissAction = true,
                )
            )
            clearAllCreateAndEditStates()
            return
        }

        if (newDescription.isBlank()) {
            sendSnackbar(
                SnackbarEvent(
                    message = UiText.StringResource(R.string.card_snackbar_edit_task_with_empty_description_error),
                    withDismissAction = true,
                )
            )
            return
        }

        val newTask = task.copy(description = newDescription)

        viewModelScope.launch(dispatcherProvider.main) {
            updateTaskUseCase(task = newTask, cardId = card.id)
        }

        clearAllCreateAndEditStates()
    }

    private fun onTaskCheckedChange(taskId: Long, isChecked: Boolean) {
        val card = _uiState.value.card!!
        val task = card.tasks.find { it.id == taskId }

        if (task == null) {
            sendSnackbar(
                SnackbarEvent(
                    message = UiText.StringResource(R.string.card_snackbar_task_checked_change_error),
                    withDismissAction = true,
                )
            )
            return
        }

        val newTask = task.copy(isCompleted = isChecked)

        _uiState.update { currentState ->
            currentState.copy(
                card = card.copy(
                    tasks = card.tasks.map { task ->
                        if (task.id == taskId) newTask else task
                    }
                )
            )
        }

        viewModelScope.launch(dispatcherProvider.main) {
            val result = updateTaskUseCase(task = newTask, cardId = card.id)

            if (result is Result.Error) {
                sendSnackbar(
                    SnackbarEvent(
                        message = UiText.StringResource(R.string.card_snackbar_task_checked_change_error),
                        withDismissAction = true,
                    )
                )
                _uiState.update { currentState ->
                    currentState.copy(
                        card = card.copy(
                            tasks = card.tasks.map { task ->
                                if (task.id == taskId) task else task
                            }
                        )
                    )
                }
            }
        }
    }


    // Delete task
    private fun deleteTask(taskId: Long) {
        clearAllCreateAndEditStates()
        val card = _uiState.value.card!!

        val task = card.tasks.find { it.id == taskId }

        if (task == null) {
            sendSnackbar(
                SnackbarEvent(
                    message = UiText.StringResource(R.string.card_snackbar_delete_task_error),
                    withDismissAction = true,
                )
            )
            return
        }

        viewModelScope.launch(dispatcherProvider.main) {
            val result = deleteTaskUseCase(task = task, cardId = card.id)

            when (result) {

                is Result.Success -> sendSnackbar(
                    SnackbarEvent(
                        message = UiText.StringResource(R.string.card_snackbar_delete_task_success),
                        withDismissAction = true,
                        duration = SnackbarDuration.Long,
                        action = SnackbarAction(
                            label = UiText.StringResource(R.string.undo_action),
                            action = { restoreTask(task = task, cardId = card.id) }
                        )
                    )
                )

                is Result.Error -> sendSnackbar(
                    SnackbarEvent(
                        message = UiText.StringResource(R.string.card_snackbar_delete_task_error),
                        withDismissAction = true,
                    )
                )
            }
        }
    }

    private fun restoreTask(task: Task, cardId: Long) {
        viewModelScope.launch(dispatcherProvider.main) {
            val result = createTaskUseCase(task = task, cardId = cardId)

            if (result is Result.Error) {
                sendSnackbar(
                    SnackbarEvent(
                        message = UiText.StringResource(R.string.card_snackbar_restore_task_error),
                        withDismissAction = true,
                    )
                )
            }
        }
    }


    // Date Picker
    private fun showDatePicker() {
        _uiState.update { it.copy(isPickingDate = true) }
    }

    private fun onDateSelected(date: LocalDateTime?) {
        clearAllCreateAndEditStates()

        viewModelScope.launch(dispatcherProvider.main) {
            val card = _uiState.value.card!!
            val newCard = card.copy(dueDate = date)

            val columnId = getColumnId(card.id)

            if (columnId == null) {
                sendSnackbar(
                    SnackbarEvent(
                        message = UiText.StringResource(R.string.card_snackbar_save_date_error),
                        withDismissAction = true,
                    )
                )
                return@launch
            }


            val result = updateCardUseCase(newCard, columnId)

            if (result is Result.Error) sendSnackbar(
                SnackbarEvent(
                    message = UiText.StringResource(R.string.card_snackbar_save_date_error),
                    withDismissAction = true,
                )
            )
        }
    }


    // Attachments
    private fun startCreatingAttachment() {
        clearAllCreateAndEditStates()
        _uiState.update { it.copy(isCreatingAttachment = true) }
    }

    private fun openImage(attachment: Attachment) {
        _uiState.update { it.copy(displayingAttachment = attachment) }
    }

    private fun saveImage(uri: String, shouldAddToCover: Boolean) {
        viewModelScope.launch(dispatcherProvider.main) {
            val snackbarEvent = SnackbarEvent(
                message = UiText.StringResource(R.string.card_snackbar_save_image_error),
                withDismissAction = true,
            )
            val result = saveImageUseCase(uri)

            when (result) {

                is Result.Success -> {
                    val absolutePath = result.data
                    val newAttachment = Attachment(id = 0, fileName = absolutePath)
                    createAttachment(newAttachment)

                    if (shouldAddToCover) {
                        addCover(absolutePath)
                    }

                }

                is Result.Error -> sendSnackbar(snackbarEvent)
            }

            clearAllCreateAndEditStates()
        }
    }

    private fun deleteImage(attachment: Attachment) {
        clearAllCreateAndEditStates()
        val cardId = _uiState.value.card!!.id

        viewModelScope.launch(dispatcherProvider.main) {
            val wasImageDeleted = deleteImageUseCase(attachment.fileName)

            if (!wasImageDeleted) {
                sendSnackbar(
                    SnackbarEvent(
                        message = UiText.StringResource(R.string.card_snackbar_delete_image_error),
                        withDismissAction = true,
                    )
                )
                return@launch
            }

            val wasAttachmentDeleted = deleteAttachmentUseCase(attachment, cardId)

            if (!wasAttachmentDeleted) sendSnackbar(
                SnackbarEvent(
                    message = UiText.StringResource(R.string.card_snackbar_delete_attachment_error),
                    withDismissAction = true,
                )
            )
        }
    }

    private fun createAttachment(attachment: Attachment) {
        val cardId = _uiState.value.card?.id ?: return
        viewModelScope.launch(dispatcherProvider.main) {
            val wasAttachmentCreated = createAttachmentUseCase(attachment, cardId)

            if (!wasAttachmentCreated) sendSnackbar(
                SnackbarEvent(
                    message = UiText.StringResource(R.string.card_snackbar_create_attachment_error),
                    withDismissAction = true,
                )
            )
        }

    }

    private fun addCover(absolutePath: String) {
        val card = _uiState.value.card ?: return
        val snackbarEvent = SnackbarEvent(
            message = UiText.StringResource(R.string.card_snackbar_add_cover_error),
            withDismissAction = true,
        )

        viewModelScope.launch(dispatcherProvider.main) {
            val columnId = getColumnId(card.id)
            val newCard = card.copy(thumbnailFileName = absolutePath)

            if (columnId == null) {
                sendSnackbar(snackbarEvent)
                return@launch
            }

            val result = updateCardUseCase(newCard, columnId)

            if (result is Result.Error) sendSnackbar(snackbarEvent)
        }
    }


    // Labels
    private fun openLabelPicker() {
        _uiState.update { it.copy(isLabelMenuExpanded = true) }
    }

    private fun startCreatingLabel() {
        clearAllCreateAndEditStates()
        _uiState.update { it.copy(isShowingCreateLabelDialog = true) }
    }

    private fun createLabel(label: Label) {
        val cardId = _uiState.value.card?.id ?: return

        viewModelScope.launch(dispatcherProvider.main) {
            val wasSuccessful = createLabelForCardUseCase(label, cardId)

            if (!wasSuccessful) {
                sendSnackbar(
                    SnackbarEvent(
                        message = UiText.StringResource(R.string.card_snackbar_create_label_error),
                        withDismissAction = true,
                    )
                )
            }

            clearAllCreateAndEditStates()
        }
    }

    private fun updateLabelAssociation(label: Label) {
        val cardId = uiState.value.card?.id ?: return

        viewModelScope.launch(dispatcherProvider.main) {
            println("vm being called")
            val wasSuccessful = updateLabelAssociationUseCase(label.id, cardId)
            println("vm called")

            if (!wasSuccessful) {
                sendSnackbar(
                    SnackbarEvent(
                        message = UiText.StringResource(R.string.card_snackbar_update_label_association_error),
                        withDismissAction = true,
                    )
                )
            }
        }
    }

    // Helper functions
    private fun sendSnackbar(snackbarEvent: SnackbarEvent) {
        viewModelScope.launch(dispatcherProvider.main) {
            _sideEffectChannel.send(
                CardSideEffect.ShowSnackBar(
                    snackbarEvent
                )
            )
        }
    }

    private fun clearAllCreateAndEditStates() {
        _uiState.update {
            it.copy(
                appBarType = CardAppBarType.DEFAULT,
                newDescription = null,
                createTaskState = CreateTaskState(),
                editTaskState = EditTaskState(),
                isPickingDate = false,
                isCreatingAttachment = false,
                displayingAttachment = null,
                isLabelMenuExpanded = false,
                isShowingCreateLabelDialog = false
            )
        }
    }

    private suspend fun getColumnId(cardId: Long): Long? {

        val result = getCardColumnIdUseCase(cardId)

        return if (result is Result.Success) result.data else null
    }

}