package com.educost.kanone.presentation.screens.card

import com.educost.kanone.domain.model.Attachment
import com.educost.kanone.domain.model.CardItem
import com.educost.kanone.domain.model.Label
import com.educost.kanone.presentation.screens.card.utils.CardAppBarType

data class CardUiState(
    val card: CardItem? = null,
    val boardLabels: List<Label> = emptyList(),

    // edit states
    val appBarType: CardAppBarType = CardAppBarType.DEFAULT,
    val createTaskState: CreateTaskState = CreateTaskState(),
    val editTaskState: EditTaskState = EditTaskState(),
    val displayingAttachment: Attachment? = null,
    val labelBeingEdited: Label? = null,
    val isCreatingAttachment: Boolean = false,
    val isLabelMenuExpanded: Boolean = false,
    val isShowingLabelDialog: Boolean = false,
    val isPickingDate: Boolean = false,
    val isShowingCardDeletionDialog: Boolean = false,
    val isRenamingCard: Boolean = false,


) {
    val hasEditStates = appBarType != CardAppBarType.DEFAULT ||
            createTaskState != CreateTaskState() ||
            editTaskState != EditTaskState() ||
            displayingAttachment != null ||
            labelBeingEdited != null ||
            isCreatingAttachment ||
            isLabelMenuExpanded ||
            isShowingLabelDialog ||
            isPickingDate ||
            isShowingCardDeletionDialog ||
            isRenamingCard
}

data class CreateTaskState(
    val description: String = "",
    val isCompleted: Boolean = false
)

data class EditTaskState(
    val description: String = "",
    val taskId: Long? = null
)