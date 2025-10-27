package com.educost.kanone.presentation.screens.board

import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.material3.SnackbarDuration
import androidx.compose.ui.geometry.Offset
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.educost.kanone.R
import com.educost.kanone.dispatchers.DispatcherProvider
import com.educost.kanone.domain.model.Board
import com.educost.kanone.domain.usecase.CreateCardResult
import com.educost.kanone.domain.usecase.CreateCardUseCase
import com.educost.kanone.domain.usecase.CreateColumnResult
import com.educost.kanone.domain.usecase.CreateColumnUseCase
import com.educost.kanone.domain.usecase.DeleteBoardUseCase
import com.educost.kanone.domain.usecase.DeleteColumnUseCase
import com.educost.kanone.domain.usecase.ObserveCompleteBoardUseCase
import com.educost.kanone.domain.usecase.PersistBoardPositionsUseCase
import com.educost.kanone.domain.usecase.ReorderCardsUseCase
import com.educost.kanone.domain.usecase.RestoreColumnUseCase
import com.educost.kanone.domain.usecase.UpdateBoardUseCase
import com.educost.kanone.domain.usecase.UpdateColumnUseCase
import com.educost.kanone.presentation.screens.board.mapper.toBoard
import com.educost.kanone.presentation.screens.board.mapper.toBoardUi
import com.educost.kanone.presentation.screens.board.mapper.toCardUi
import com.educost.kanone.presentation.screens.board.mapper.toColumnUi
import com.educost.kanone.presentation.screens.board.mapper.toKanbanColumn
import com.educost.kanone.presentation.screens.board.model.BoardSizes
import com.educost.kanone.presentation.screens.board.model.BoardUi
import com.educost.kanone.presentation.screens.board.model.CardUi
import com.educost.kanone.presentation.screens.board.model.ColumnUi
import com.educost.kanone.presentation.screens.board.model.Coordinates
import com.educost.kanone.presentation.screens.board.state.BoardUiState
import com.educost.kanone.presentation.screens.board.state.CardCreationState
import com.educost.kanone.presentation.screens.board.state.ColumnEditState
import com.educost.kanone.presentation.screens.board.state.DragState
import com.educost.kanone.presentation.screens.board.state.ScrollState
import com.educost.kanone.presentation.screens.board.utils.BoardAppBarType
import com.educost.kanone.presentation.screens.board.utils.CardOrder
import com.educost.kanone.presentation.screens.board.utils.OrderType
import com.educost.kanone.presentation.screens.board.utils.ThrottledDebouncedProcessor
import com.educost.kanone.presentation.screens.board.utils.setBoardCoordinates
import com.educost.kanone.presentation.screens.board.utils.setCardsCoordinates
import com.educost.kanone.presentation.screens.board.utils.setColumnsCoordinates
import com.educost.kanone.presentation.screens.board.utils.setColumnHeadersCoordinates
import com.educost.kanone.presentation.screens.board.utils.setColumnListsCoordinates
import com.educost.kanone.presentation.util.SnackbarAction
import com.educost.kanone.presentation.util.SnackbarEvent
import com.educost.kanone.presentation.util.UiText
import com.educost.kanone.utils.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BoardViewModel @Inject constructor(
    private val dispatcherProvider: DispatcherProvider,
    private val observeCompleteBoardUseCase: ObserveCompleteBoardUseCase,
    private val createColumnUseCase: CreateColumnUseCase,
    private val createCardUseCase: CreateCardUseCase,
    private val updateColumnUseCase: UpdateColumnUseCase,
    private val deleteColumnUseCase: DeleteColumnUseCase,
    private val restoreColumnUseCase: RestoreColumnUseCase,
    private val persistBoardPositionsUseCase: PersistBoardPositionsUseCase,
    private val reorderCardsUseCase: ReorderCardsUseCase,
    private val updateBoardUseCase: UpdateBoardUseCase,
    private val deleteBoardUseCase: DeleteBoardUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(BoardUiState())
    val uiState = _uiState.asStateFlow()

    private val _sideEffectChannel = Channel<BoardSideEffect>(Channel.BUFFERED)
    val sideEffectFlow = _sideEffectChannel.receiveAsFlow()

    private val scrollState = ScrollState()


    fun onIntent(intent: BoardIntent) {
        when (intent) {
            is BoardIntent.ObserveBoard -> observeBoard(intent.boardId)

            // Rename Board
            is BoardIntent.OnRenameBoardClicked -> onRenameBoardClicked()
            is BoardIntent.ConfirmBoardRename -> confirmBoardRename(intent.newName)
            is BoardIntent.CancelBoardRename -> clearEditAndCreationStates()

            // Delete Board
            is BoardIntent.OnDeleteBoardClicked -> onDeleteBoardClicked()
            is BoardIntent.ConfirmBoardDeletion -> deleteBoard()
            is BoardIntent.CancelBoardDeletion -> clearEditAndCreationStates()


            /*  Board Settings  */
            is BoardIntent.OpenBoardSettings -> openBoardSettings()
            is BoardIntent.CloseBoardSettings -> clearEditAndCreationStates()
            is BoardIntent.ToggleShowImages -> toggleShowImages()
            is BoardIntent.ToggleLayoutOrientation -> toggleLayoutOrientation()

            // Zoom
            is BoardIntent.OnZoomChange -> onZoomChange(intent.zoomChange, intent.scrollChange)
            is BoardIntent.SetZoom -> setZoom(intent.zoomValue)

            // Full screen
            is BoardIntent.EnterFullScreen -> enterFullScreen()
            is BoardIntent.ExitFullScreen -> exitFullScreen()
            /*  Board Settings  */


            // Create column
            is BoardIntent.StartCreatingColumn -> startCreatingColumn()
            is BoardIntent.OnColumnNameChanged -> onColumnNameChanged(intent.name)
            is BoardIntent.CancelColumnCreation -> clearEditAndCreationStates()
            is BoardIntent.ConfirmColumnCreation -> confirmColumnCreation()

            // Column dropdown menu
            is BoardIntent.OpenColumnDropdownMenu -> openColumnDropdownMenu(intent.columnId)
            is BoardIntent.CloseColumnDropdownMenu -> closeColumnDropdownMenu()
            is BoardIntent.OnDeleteColumnClicked -> onDeleteColumnClicked(intent.columnId)
            is BoardIntent.OnOrderByClicked -> reorderCardsInColumn(
                columnId = intent.columnId,
                orderType = intent.orderType,
                cardOrder = intent.cardOrder
            )


            /*  Edit Column  */
            is BoardIntent.ToggleExpandColumn -> toggleExpandColumn(intent.columnId)

            // Column rename
            is BoardIntent.OnRenameColumnClicked -> onRenameColumnClicked(intent.columnId)
            is BoardIntent.OnEditColumnNameChange -> onEditColumnNameChange(intent.name)
            is BoardIntent.CancelColumnRename -> clearEditAndCreationStates()
            is BoardIntent.ConfirmColumnRename -> confirmColumnRename()

            // Column color
            is BoardIntent.StartEditingColumnColor -> startEditingColumnColor(intent.columnId)
            is BoardIntent.CancelColumnColorEdit -> clearEditAndCreationStates()
            is BoardIntent.ConfirmColumnColorEdit -> confirmColorEdit(intent.newColor)
            /*  Edit Column  */


            // Create card
            is BoardIntent.StartCreatingCard -> startCreatingCard(
                columnId = intent.columnId,
                isAppendingToEnd = intent.isAppendingToEnd
            )

            is BoardIntent.OnCardTitleChange -> onCardTitleChange(intent.title)
            is BoardIntent.CancelCardCreation -> clearEditAndCreationStates()
            is BoardIntent.ConfirmCardCreation -> confirmCardCreation()


            /*  Others  */
            is BoardIntent.OnBackPressed -> onBackPressed()

            // navigation
            is BoardIntent.OnNavigateBack -> navigateBack()
            is BoardIntent.OnCardClick -> navigateToCardScreen(intent.cardId)
            is BoardIntent.NavigateToSettings -> navigateToSettings()

            // App bar dropdown menu
            is BoardIntent.OpenBoardDropdownMenu -> openBoardDropdownMenu()
            is BoardIntent.CloseBoardDropdownMenu -> clearEditAndCreationStates()
            /*  Others  */


            // Drag and drop
            is BoardIntent.OnDragStart -> onDragStart(intent.offset, intent.isOnVerticalLayout)
            is BoardIntent.OnDrag -> onDrag(intent.position, intent.isOnVerticalLayout)
            is BoardIntent.OnDragStop -> onDragStop()

            // Set coordinates
            is BoardIntent.SetBoardCoordinates -> setBoardCoordinates(intent.coordinates)
            is BoardIntent.SetColumnHeaderCoordinates -> setColumnHeaderCoordinates(
                columnId = intent.columnId,
                coordinates = intent.coordinates
            )

            is BoardIntent.SetColumnListCoordinates -> setColumnListCoordinates(
                columnId = intent.columnId,
                coordinates = intent.coordinates
            )

            is BoardIntent.SetColumnCoordinates -> setColumnCoordinates(
                columnId = intent.columnId,
                coordinates = intent.coordinates
            )

            is BoardIntent.SetCardCoordinates -> setCardCoordinates(
                cardId = intent.cardId,
                columnId = intent.columnId,
                coordinates = intent.coordinates
            )
        }
    }


    private fun observeBoard(boardId: Long) {
        viewModelScope.launch(dispatcherProvider.main) {
            observeCompleteBoardUseCase(boardId).collect { result ->
                when (result) {
                    is Result.Success -> {
                        _uiState.update { currentState ->

                            val newBoard = mapToUiState(
                                newBoard = result.data,
                                oldBoard = currentState.board
                            )

                            currentState.copy(board = newBoard)
                        }
                    }

                    is Result.Error -> sendSnackbar(
                        SnackbarEvent(
                            message = UiText.StringResource(R.string.board_snackbar_fetch_board_error)
                        )
                    )
                }
            }
        }
    }


    // Delete board
    private fun deleteBoard() {
        val board = uiState.value.board ?: return

        viewModelScope.launch(dispatcherProvider.main) {
            val wasBoardDeleted = deleteBoardUseCase(board.toBoard())

            if (wasBoardDeleted) {
                _sideEffectChannel.send(BoardSideEffect.OnNavigateBack)
            } else {
                clearEditAndCreationStates()
                sendSnackbar(
                    SnackbarEvent(
                        message = UiText.StringResource(R.string.snackbar_delete_board_error),
                        withDismissAction = true
                    )
                )
            }
        }
    }

    private fun onDeleteBoardClicked() {
        clearEditAndCreationStates()
        _uiState.update { it.copy(isShowingDeleteBoardDialog = true) }
    }


    // Rename board
    private fun confirmBoardRename(newName: String) {
        val board = uiState.value.board ?: return

        viewModelScope.launch(dispatcherProvider.main) {
            val newBoard = board.copy(name = newName).toBoard()
            val wasBoardUpdated = updateBoardUseCase(newBoard)

            if (!wasBoardUpdated) sendSnackbar(
                SnackbarEvent(
                    message = UiText.StringResource(R.string.snackbar_rename_board_error),
                    withDismissAction = true
                )
            )

            clearEditAndCreationStates()
        }
    }

    private fun onRenameBoardClicked() {
        clearEditAndCreationStates()
        _uiState.update { it.copy(isRenamingBoard = true) }
    }


    /*  Board Settings  */
    private fun openBoardSettings() {
        clearEditAndCreationStates()
        _uiState.update { it.copy(isModalSheetExpanded = true) }
    }

    private fun toggleShowImages() {
        val board = uiState.value.board ?: return
        viewModelScope.launch(dispatcherProvider.main) {
            val updatedBoard = board.copy(showImages = !board.showImages).toBoard()
            updateBoardUseCase(updatedBoard)
        }
    }

    private fun toggleLayoutOrientation() {
        val board = uiState.value.board ?: return

        viewModelScope.launch(dispatcherProvider.main) {
            val updatedBoard = board.copy(isOnVerticalLayout = !board.isOnVerticalLayout).toBoard()
            updateBoardUseCase(updatedBoard)
        }
    }


    // Zoom
    private fun onZoomChange(zoomChange: Float, scrollChange: Float) {
        val board = uiState.value.board ?: return
        _uiState.update { it.copy(isChangingZoom = true) }
        val currentZoomPercentage = board.sizes.zoomPercentage
        val updatedZoom = (currentZoomPercentage * zoomChange).coerceIn(
            minimumValue = 35f,
            maximumValue = 120f
        )
        _uiState.update {
            it.copy(
                board = board.copy(
                    sizes = BoardSizes(updatedZoom)
                )
            )
        }
        viewModelScope.launch(dispatcherProvider.main) {
            try {
                board.listState.scrollBy(scrollChange)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        persistZoom()
    }

    private fun setZoom(zoomValue: Float) {
        val board = uiState.value.board ?: return
        _uiState.update { it.copy(isChangingZoom = true) }

        _uiState.update {
            it.copy(
                board = board.copy(
                    sizes = BoardSizes(zoomValue)
                )
            )
        }

        persistZoom()
    }


    // Full screen
    private fun enterFullScreen() {
        _uiState.update { it.copy(isOnFullScreen = true) }
    }

    private fun exitFullScreen() {
        _uiState.update { it.copy(isOnFullScreen = false) }
    }
    /*  Board Settings  */


    // Create column

    private fun startCreatingColumn() {
        clearEditAndCreationStates()
        _uiState.update { it.copy(topBarType = BoardAppBarType.ADD_COLUMN) }
    }

    private fun onColumnNameChanged(name: String) {
        _uiState.update { it.copy(creatingColumnName = name) }
    }

    private fun confirmColumnCreation() {
        val board = uiState.value.board ?: return
        val newName = uiState.value.creatingColumnName

        viewModelScope.launch(dispatcherProvider.main) {
            val result = createColumnUseCase(
                columnName = newName,
                position = board.columns.size,
                boardId = board.id
            )
            if (result == CreateColumnResult.EMPTY_NAME) sendSnackbar(
                SnackbarEvent(
                    message = UiText.StringResource(R.string.board_snackbar_column_empty_name_error),
                    withDismissAction = true
                )
            )
            if (result == CreateColumnResult.GENERIC_ERROR) sendSnackbar(
                SnackbarEvent(
                    message = UiText.StringResource(R.string.board_snackbar_column_creation_error),
                    withDismissAction = true
                )
            )
            clearEditAndCreationStates()
        }
    }


    // Column dropdown menu

    private fun openColumnDropdownMenu(columnId: Long) {
        clearEditAndCreationStates()
        _uiState.update { it.copy(activeDropdownColumnId = columnId) }
    }

    private fun closeColumnDropdownMenu() {
        _uiState.update { it.copy(activeDropdownColumnId = null) }
    }

    private fun reorderCardsInColumn(columnId: Long, orderType: OrderType, cardOrder: CardOrder) {

        val board = uiState.value.board ?: return
        val column = board.columns.find { it.id == columnId }

        viewModelScope.launch(dispatcherProvider.main) {
            val wasCardsReordered = reorderCardsUseCase(
                column = column?.toKanbanColumn(),
                orderType = orderType,
                cardOrder = cardOrder
            )

            if (!wasCardsReordered) sendSnackbar(
                SnackbarEvent(
                    message = UiText.StringResource(R.string.board_snackbar_reorder_cards_error),
                    withDismissAction = true
                )
            )
        }
    }

    private fun onDeleteColumnClicked(columnId: Long) {

        val boardId = uiState.value.board?.id ?: return
        val column = uiState.value.board!!.columns.find { it.id == columnId }
        if (column == null) {
            sendSnackbar(
                SnackbarEvent(
                    message = UiText.StringResource(R.string.board_snackbar_column_delete_error),
                    withDismissAction = true
                )
            )
            return
        }

        viewModelScope.launch(dispatcherProvider.main) {
            val wasColumnDeleted = deleteColumnUseCase(
                column = column.toKanbanColumn(),
                boardId = boardId
            )

            if (wasColumnDeleted) sendSnackbar(
                SnackbarEvent(
                    message = UiText.StringResource(R.string.board_snackbar_column_deleted),
                    withDismissAction = true,
                    duration = SnackbarDuration.Long,
                    action = SnackbarAction(
                        label = UiText.StringResource(R.string.undo_action),
                        action = {
                            restoreDeletedColumn(column)
                        }
                    ),
                )
            )
            else sendSnackbar(
                SnackbarEvent(
                    message = UiText.StringResource(R.string.board_snackbar_column_delete_error),
                    withDismissAction = true
                )
            )
        }
    }


    /*  Edit Column  */
    private fun toggleExpandColumn(columnId: Long) {
        val board = uiState.value.board ?: return

        val currentColumn = board.columns.find { it.id == columnId } ?: return
        val currentExpandedState = currentColumn.isExpanded


        viewModelScope.launch(dispatcherProvider.main) {
            updateColumnUseCase(
                column = currentColumn.copy(isExpanded = !currentExpandedState).toKanbanColumn(),
                boardId = board.id
            )
        }
    }

    private var columnsIdsCollapsed = mutableListOf<Long>()
    private fun collapseAllColumns() {
        _uiState.update { currentState ->
            val board = currentState.board ?: return
            currentState.copy(
                board = board.copy(
                    columns = board.columns.map { column ->
                        if (column.isExpanded) {
                            columnsIdsCollapsed.add(column.id)
                            column.copy(isExpanded = false)
                        } else {
                            column
                        }
                    }
                )
            )
        }
    }

    private fun restoreCollapsedColumns() {
        _uiState.update { currentState ->
            val board = currentState.board ?: return
            currentState.copy(
                board = board.copy(
                    columns = board.columns.map { column ->
                        if (column.id in columnsIdsCollapsed) {
                            column.copy(isExpanded = true)
                        } else {
                            column
                        }
                    }
                )
            )
        }
        columnsIdsCollapsed.clear()
    }

    // Column rename
    private fun onRenameColumnClicked(columnId: Long) {
        clearEditAndCreationStates()
        val currentColumnName = uiState.value.board?.columns?.find { it.id == columnId }?.name

        _uiState.update {
            it.copy(
                topBarType = BoardAppBarType.RENAME_COLUMN,
                columnEditState = it.columnEditState.copy(
                    newColumnName = currentColumnName,
                    editingColumnId = columnId,
                    isRenaming = true
                )
            )
        }
    }

    private fun onEditColumnNameChange(name: String) {
        _uiState.update {
            it.copy(columnEditState = it.columnEditState.copy(newColumnName = name))
        }
    }

    private fun confirmColumnRename() {

        val boardId = uiState.value.board?.id ?: return
        val newName = uiState.value.columnEditState.newColumnName
        val columnId = uiState.value.columnEditState.editingColumnId
        val column = uiState.value.board!!.columns.find { it.id == columnId }

        if (newName.isNullOrBlank()) {
            sendSnackbar(
                SnackbarEvent(
                    message = UiText.StringResource(R.string.board_snackbar_column_empty_name_error),
                    withDismissAction = true
                )
            )
            return
        }
        if (column == null) {
            sendSnackbar(
                SnackbarEvent(
                    message = UiText.StringResource(R.string.board_snackbar_column_rename_error),
                    withDismissAction = true
                )
            )
            clearEditAndCreationStates()
            return
        }

        val newColumn = column.copy(name = newName).toKanbanColumn()

        viewModelScope.launch(dispatcherProvider.main) {
            val wasColumnUpdated = updateColumnUseCase(newColumn, boardId)

            if (!wasColumnUpdated) sendSnackbar(
                SnackbarEvent(
                    message = UiText.StringResource(R.string.board_snackbar_column_rename_error),
                    withDismissAction = true
                )
            )

        }
        clearEditAndCreationStates()
    }


    // Column color
    private fun startEditingColumnColor(columnId: Long) {
        _uiState.update {
            it.copy(
                columnEditState = ColumnEditState(
                    editingColumnId = columnId,
                    isShowingColorPicker = true
                )
            )
        }
    }

    private fun confirmColorEdit(newColor: Int) {
        val boardId = uiState.value.board?.id ?: return
        val columnId = uiState.value.columnEditState.editingColumnId
        val column = uiState.value.board!!.columns.find { it.id == columnId }

        if (column == null) {
            sendSnackbar(
                SnackbarEvent(
                    message = UiText.StringResource(R.string.board_snackbar_edit_column_color_error),
                    withDismissAction = true
                )
            )
            return
        }

        val updatedColumn = column.copy(color = newColor).toKanbanColumn()

        viewModelScope.launch(dispatcherProvider.main) {
            val wasColumnUpdated = updateColumnUseCase(updatedColumn, boardId)

            if (!wasColumnUpdated) sendSnackbar(
                SnackbarEvent(
                    message = UiText.StringResource(R.string.board_snackbar_edit_column_color_error),
                    withDismissAction = true
                )
            )
        }

        clearEditAndCreationStates()
    }

    /*  Edit Column  */


    // Create card

    private fun startCreatingCard(columnId: Long, isAppendingToEnd: Boolean) {
        clearEditAndCreationStates()
        _uiState.update {
            it.copy(
                cardCreationState = CardCreationState(
                    columnId = columnId,
                    isAppendingToEnd = isAppendingToEnd
                ),
                topBarType = BoardAppBarType.ADD_CARD
            )
        }
    }

    private fun onCardTitleChange(newTitle: String) {
        _uiState.update { it.copy(cardCreationState = it.cardCreationState.copy(title = newTitle)) }
    }

    private fun confirmCardCreation() {
        uiState.value.cardCreationState.let { cardCreationState ->

            val cardTitle = cardCreationState.title
            val columnId = cardCreationState.columnId
            val isAppendingToEnd = cardCreationState.isAppendingToEnd

            val position = if (isAppendingToEnd) {
                val targetColumn = uiState.value.board?.columns?.find { column ->
                    column.id == columnId
                }
                targetColumn?.cards?.size

            } else -1 // ensure card will be appended at first position


            viewModelScope.launch(dispatcherProvider.main) {
                val cardCreationResult = createCardUseCase(
                    title = cardTitle,
                    position = position,
                    columnId = columnId
                )

                if (cardCreationResult == CreateCardResult.EMPTY_TITLE) {
                    sendSnackbar(
                        SnackbarEvent(
                            message = UiText.StringResource(R.string.board_snackbar_card_creation_empty_name_error),
                            withDismissAction = true
                        )
                    )
                    return@launch
                }
                if (cardCreationResult == CreateCardResult.GENERIC_ERROR) sendSnackbar(
                    SnackbarEvent(
                        message = UiText.StringResource(R.string.board_snackbar_card_creation_error),
                        withDismissAction = true
                    )
                )
                clearEditAndCreationStates()
            }
        }
    }


    // Helper functions

    private fun restoreDeletedColumn(column: ColumnUi) {
        val boardId = uiState.value.board?.id ?: return
        viewModelScope.launch(dispatcherProvider.main) {
            val wasColumnRestored = restoreColumnUseCase(column.toKanbanColumn(), boardId)
            if (!wasColumnRestored) {
                sendSnackbar(
                    SnackbarEvent(
                        message = UiText.StringResource(R.string.board_snackbar_column_restore_error),
                        withDismissAction = true
                    )
                )
            }
        }
    }

    private fun clearEditAndCreationStates() {
        _uiState.update {
            it.copy(
                topBarType = BoardAppBarType.DEFAULT,
                activeDropdownColumnId = null,
                cardCreationState = CardCreationState(),
                columnEditState = ColumnEditState(),
                creatingColumnName = null,
                isBoardDropdownMenuExpanded = false,
                isRenamingBoard = false,
                isShowingDeleteBoardDialog = false,
                isChangingZoom = false,
                isModalSheetExpanded = false
            )
        }
    }

    private fun mapToUiState(newBoard: Board, oldBoard: BoardUi?): BoardUi {

        if (oldBoard == null) {
            val sortedColumns = newBoard.columns
                .map { column -> column.copy(cards = column.cards.sortedBy { it.position }) }
                .sortedBy { it.position }
            return newBoard.copy(columns = sortedColumns).toBoardUi()
        } else {

            val mappedBoard = oldBoard.copy(
                id = newBoard.id,
                name = newBoard.name,
                columns = newBoard.columns.map { column ->

                    val isNewColumn = oldBoard.columns.find {
                        it.id == column.id
                    }

                    isNewColumn?.copy(
                        id = column.id,
                        name = column.name,
                        position = column.position,
                        color = column.color,
                        isExpanded = column.isExpanded,
                        cards = column.cards.map { cards ->

                            val isNewCard = isNewColumn.cards.find {
                                it.id == cards.id
                            }

                            isNewCard?.copy(
                                id = cards.id,
                                title = cards.title,
                                description = cards.description,
                                position = cards.position,
                                color = cards.color,
                                createdAt = cards.createdAt,
                                dueDate = cards.dueDate,
                                coverFileName = cards.coverFileName,
                                tasks = cards.tasks,
                                attachments = cards.attachments,
                                labels = cards.labels
                            )
                                ?: cards.toCardUi()
                        }.sortedBy { it.position }
                    )
                        ?: column.toColumnUi()
                }.sortedBy { it.position },
                sizes = BoardSizes(zoomPercentage = newBoard.zoomPercentage),
                showImages = newBoard.showImages,
                isOnVerticalLayout = newBoard.isOnVerticalLayout
            )

            return mappedBoard

        }
    }

    private fun sendSnackbar(snackbarEvent: SnackbarEvent) {
        viewModelScope.launch(dispatcherProvider.main) {
            _sideEffectChannel.send(
                BoardSideEffect.ShowSnackBar(
                    snackbarEvent
                )
            )
        }
    }

    private fun updatePositions() {

        val board = uiState.value.board ?: return
        val columns = board.columns.map { it.toKanbanColumn() }

        viewModelScope.launch(dispatcherProvider.main) {
            persistBoardPositionsUseCase(board.id, columns)
        }
    }


    private var persistZoomJob: Job? = null
    private val persistZoomDebounceTime = 500L
    private fun persistZoom() {
        persistZoomJob?.cancel()
        persistZoomJob = viewModelScope.launch(dispatcherProvider.main) {
            delay(persistZoomDebounceTime)

            _uiState.update { it.copy(isChangingZoom = false) }
            val board = uiState.value.board ?: return@launch

            val updatedBoard = board.copy(
                sizes = board.sizes.copy(zoomPercentage = board.sizes.zoomPercentage)
            ).toBoard()

            updateBoardUseCase(updatedBoard)

        }
    }


    /*  Others  */
    private fun onBackPressed() {
        val state = uiState.value
        when {
            state.isOnFullScreen && state.hasEditStates -> clearEditAndCreationStates()
            state.isOnFullScreen -> onIntent(BoardIntent.ExitFullScreen)
            else -> clearEditAndCreationStates()
        }
    }

    private fun openBoardDropdownMenu() {
        clearEditAndCreationStates()
        _uiState.update { it.copy(isBoardDropdownMenuExpanded = true) }
    }


    // navigation
    private fun navigateToCardScreen(cardId: Long) {
        clearEditAndCreationStates()
        viewModelScope.launch(dispatcherProvider.main) {
            _sideEffectChannel.send(BoardSideEffect.NavigateToCardScreen(cardId))
        }
    }

    private fun navigateToSettings() {
        clearEditAndCreationStates()
        viewModelScope.launch(dispatcherProvider.main) {
            _sideEffectChannel.send(BoardSideEffect.NavigateToSettings)
        }
    }

    private fun navigateBack() {
        clearEditAndCreationStates()
        viewModelScope.launch(dispatcherProvider.main) {
            _sideEffectChannel.send(BoardSideEffect.OnNavigateBack)
        }
    }
    /*  Others  */


    // Drag and drop

    private fun onDragStart(offset: Offset, isOnVerticalLayout: Boolean) {
        clearEditAndCreationStates()

        _uiState.update { currentState ->

            val dragStartResult = currentState.onDragStart(offset, isOnVerticalLayout)
            val isDraggingColumn = dragStartResult.dragState.isDraggingColumn()


            if (isDraggingColumn && isOnVerticalLayout) {
                collapseAllColumns()
            }


            return@update dragStartResult
        }
    }

    private fun onDrag(offset: Offset?, isOnVerticalLayout: Boolean) {
        _uiState.update { state ->
            val isDraggingCard = state.dragState.isDraggingCard()
            val isDraggingColumn = state.dragState.isDraggingColumn()


            if (isDraggingColumn) {
                val column = state.dragState.columnBeingDragged ?: return

                handleBoardAutoScroll(column.coordinates, isOnVerticalLayout)
            }

            if (isDraggingCard) {
                val currentCard = state.dragState.cardBeingDragged ?: return
                val currentColumn = state.dragState.cardBeingDraggedColumn ?: return
                val currentColumnIndex = state.dragState.cardBeingDraggedColumnIndex ?: return

                handleColumnAutoScroll(
                    selectedCard = currentCard,
                    selectedColumn = currentColumn,
                    selectedColumnIndex = currentColumnIndex,
                    isOnVerticalLayout = isOnVerticalLayout
                )
                handleBoardAutoScroll(currentCard.coordinates, isOnVerticalLayout)

            }

            return@update state.onDrag(offset, isOnVerticalLayout)
        }
    }

    private fun onDragStop() {
        _uiState.update { it.copy(dragState = DragState()) }
        restoreCollapsedColumns()
        cancelAutoScroll()
        updatePositions()
    }


    // Auto scroll

    private fun handleColumnAutoScroll(
        selectedCard: CardUi,
        selectedColumn: ColumnUi,
        selectedColumnIndex: Int,
        isOnVerticalLayout: Boolean
    ) {
        if (scrollState.scrollingColumnIndex != selectedColumnIndex) {
            cancelVerticalScroll()
            scrollState.scrollingColumnIndex = selectedColumnIndex
        }

        val dragState = uiState.value.dragState

        val cardTop = dragState.itemBeingDraggedOffset.y
        val cardBottom = cardTop + selectedCard.coordinates.height
        val columnTop = selectedColumn.listCoordinates.position.y
        val columnBottom = columnTop + selectedColumn.listCoordinates.height

        val canScrollUp = selectedColumn.listState.canScrollBackward
        val canScrollDown = selectedColumn.listState.canScrollForward
        val isCardAboveColumn = cardTop < columnTop
        val isCardBelowColumn = cardBottom > columnBottom

        val shouldScrollUp = canScrollUp && isCardAboveColumn
        val shouldScrollDown = canScrollDown && isCardBelowColumn

        scrollState.verticalSpeed = if (shouldScrollUp) {
            (cardTop - columnTop) / 3
        } else if (shouldScrollDown) {
            (cardBottom - columnBottom) / 3
        } else {
            0f
        }

        if ((shouldScrollUp || shouldScrollDown)) {
            if (!scrollState.isVerticalScrolling) {

                scrollState.isVerticalScrolling = true

                scrollState.verticalOverScrollJob = viewModelScope.launch {
                    while (scrollState.isVerticalScrolling) {
                        try {
                            delay(16)
                            val speed = scrollState.verticalSpeed
                            selectedColumn.listState.scrollBy(speed)
                            onDrag(null, isOnVerticalLayout)

                        } catch (_: Exception) {
                            scrollState.isVerticalScrolling = false
                            break
                        }
                    }
                }
            }
        } else {
            cancelVerticalScroll()
        }
    }

    private fun handleBoardAutoScroll(itemCoordinates: Coordinates, isOnVerticalLayout: Boolean) {
        val board = uiState.value.board ?: return
        val dragState = uiState.value.dragState

        val canScrollLeftOrUp = board.listState.canScrollBackward
        val canScrollRightOrDown = board.listState.canScrollForward

        when (isOnVerticalLayout) {
            true -> {
                val itemTop = dragState.itemBeingDraggedOffset.y
                val itemBottom = itemTop + itemCoordinates.height

                val boardTop = board.coordinates.position.y
                val boardBottom = boardTop + board.coordinates.height

                val isCardAboveTopEdge = itemTop < boardTop
                val isCardBellowBottomEdge = itemBottom > boardBottom

                val shouldScrollUp = canScrollLeftOrUp && isCardAboveTopEdge
                val shouldScrollDown = canScrollRightOrDown && isCardBellowBottomEdge


                scrollState.horizontalSpeed = when {
                    shouldScrollUp -> (itemTop - boardTop) / 6
                    shouldScrollDown -> (itemBottom - boardBottom) / 6
                    else -> 0f
                }


                if (shouldScrollUp || shouldScrollDown) {
                    if (!scrollState.isHorizontalScrolling) {
                        scrollState.isHorizontalScrolling = true

                        scrollState.horizontalOverScrollJob = viewModelScope.launch {
                            while (scrollState.isHorizontalScrolling) {
                                try {
                                    delay(16)
                                    val speed = scrollState.horizontalSpeed
                                    board.listState.scrollBy(speed)
                                    onDrag(null, true)
                                } catch (_: Exception) {
                                    scrollState.isHorizontalScrolling = false
                                    break
                                }
                            }
                        }
                    }
                } else {
                    cancelHorizontalScroll()
                }
            }

            false -> {
                val itemLeft = dragState.itemBeingDraggedOffset.x
                val itemRight = itemLeft + itemCoordinates.width

                val boardLeft = board.coordinates.position.x
                val boardRight = boardLeft + board.coordinates.width

                val isCardBeyondLeftEdge = itemLeft < boardLeft
                val isCardBeyondRightEdge = itemRight > boardRight

                val shouldScrollLeft = canScrollLeftOrUp && isCardBeyondLeftEdge
                val shouldScrollRight = canScrollRightOrDown && isCardBeyondRightEdge

                scrollState.horizontalSpeed = when {
                    shouldScrollLeft -> (itemLeft - boardLeft) / 6
                    shouldScrollRight -> (itemRight - boardRight) / 6
                    else -> 0f
                }

                if (shouldScrollLeft || shouldScrollRight) {
                    if (!scrollState.isHorizontalScrolling) {
                        scrollState.isHorizontalScrolling = true

                        scrollState.horizontalOverScrollJob = viewModelScope.launch {
                            while (scrollState.isHorizontalScrolling) {
                                try {
                                    delay(16)
                                    val speed = scrollState.horizontalSpeed
                                    board.listState.scrollBy(speed)
                                    onDrag(null, false)
                                } catch (_: Exception) {
                                    scrollState.isHorizontalScrolling = false
                                    break
                                }
                            }
                        }
                    }
                } else {
                    cancelHorizontalScroll()
                }
            }
        }
    }

    private fun cancelVerticalScroll() {
        scrollState.isVerticalScrolling = false
        scrollState.verticalSpeed = 0f
        scrollState.scrollingColumnIndex = null
        scrollState.verticalOverScrollJob?.cancel()
        scrollState.verticalOverScrollJob = null
    }

    private fun cancelHorizontalScroll() {
        scrollState.isHorizontalScrolling = false
        scrollState.horizontalSpeed = 0f
        scrollState.horizontalOverScrollJob?.cancel()
        scrollState.horizontalOverScrollJob = null
    }

    private fun cancelAutoScroll() {
        cancelVerticalScroll()
        cancelHorizontalScroll()
    }


    // Set coordinates

    private fun setBoardCoordinates(coordinates: Coordinates) {
        if (uiState.value.board?.coordinates != coordinates) {
            _uiState.setBoardCoordinates(coordinates)
        }
    }


    private val headerCoordinatesProcessor = ThrottledDebouncedProcessor(
        scope = viewModelScope,
        dispatcher = dispatcherProvider.main,
        onProcess = {
            _uiState.setColumnHeadersCoordinates(it)
        }
    )
    private fun setColumnHeaderCoordinates(columnId: Long, coordinates: Coordinates) {
        val isActivelyDragging = uiState.value.dragState.isActivelyDragging()

        headerCoordinatesProcessor.submit(
            key = columnId,
            value = coordinates,
            isThrottling = isActivelyDragging
        )
    }


    private val listCoordinatesProcessor = ThrottledDebouncedProcessor(
        scope = viewModelScope,
        dispatcher = dispatcherProvider.main,
        onProcess = {
            _uiState.setColumnListsCoordinates(it)
        }
    )
    private fun setColumnListCoordinates(columnId: Long, coordinates: Coordinates) {
        val isActivelyDragging = uiState.value.dragState.isActivelyDragging()

        listCoordinatesProcessor.submit(
            key = columnId,
            value = coordinates,
            isThrottling = isActivelyDragging
        )
    }


    private val columnCoordinatesProcessor = ThrottledDebouncedProcessor(
        scope = viewModelScope,
        dispatcher = dispatcherProvider.main,
        onProcess = {
            _uiState.setColumnsCoordinates(it)
        }
    )
    private fun setColumnCoordinates(columnId: Long, coordinates: Coordinates) {
        val isActivelyDragging = uiState.value.dragState.isActivelyDragging()

        columnCoordinatesProcessor.submit(
            key = columnId,
            value = coordinates,
            isThrottling = isActivelyDragging
        )
    }


    private val cardCoordinatesProcessor = ThrottledDebouncedProcessor(
        scope = viewModelScope,
        dispatcher = dispatcherProvider.main,
        onProcess = {
            _uiState.setCardsCoordinates(it)
        }
    )
    private fun setCardCoordinates(cardId: Long, columnId: Long, coordinates: Coordinates) {
        val isActivelyDragging = uiState.value.dragState != DragState()

        cardCoordinatesProcessor.submit(
            key = cardId,
            value = Pair(columnId, coordinates),
            isThrottling = isActivelyDragging
        )
    }


    override fun onCleared() {
        super.onCleared()
        cancelAutoScroll()
        headerCoordinatesProcessor.cancel()
        listCoordinatesProcessor.cancel()
        columnCoordinatesProcessor.cancel()
        cardCoordinatesProcessor.cancel()
    }
}