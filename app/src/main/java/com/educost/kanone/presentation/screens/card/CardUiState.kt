package com.educost.kanone.presentation.screens.card

import com.educost.kanone.domain.model.Attachment
import com.educost.kanone.domain.model.CardItem
import com.educost.kanone.domain.model.Label
import com.educost.kanone.presentation.screens.card.utils.CardAppBarType

data class CardUiState(
    val card: CardItem? = null,
    val appBarType: CardAppBarType = CardAppBarType.DEFAULT,
    val newDescription: String? = null,
    val createTaskState: CreateTaskState = CreateTaskState(),
    val editTaskState: EditTaskState = EditTaskState(),
    val isPickingDate: Boolean = false,
    val isCreatingAttachment: Boolean = false,
    val displayingAttachment: Attachment? = null,
    val boardLabels: List<Label> = emptyList(),
    val isLabelMenuExpanded: Boolean = false,
    val isShowingCreateLabelDialog: Boolean = false,
)

data class CreateTaskState(
    val description: String = "",
    val isCompleted: Boolean = false
)

data class EditTaskState(
    val description: String = "",
    val taskId: Long? = null
)