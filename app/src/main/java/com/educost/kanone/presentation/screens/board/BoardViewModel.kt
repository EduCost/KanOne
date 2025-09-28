package com.educost.kanone.presentation.screens.board

import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.lazy.LazyListState
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
import com.educost.kanone.presentation.screens.board.state.BoardState
import com.educost.kanone.presentation.screens.board.state.CardCreationState
import com.educost.kanone.presentation.screens.board.state.ColumnEditState
import com.educost.kanone.presentation.screens.board.state.DragState
import com.educost.kanone.presentation.screens.board.state.ScrollState
import com.educost.kanone.presentation.screens.board.utils.BoardAppBarType
import com.educost.kanone.presentation.screens.board.utils.CardOrder
import com.educost.kanone.presentation.screens.board.utils.OrderType
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

    private val _uiState = MutableStateFlow(BoardState())
    val uiState = _uiState.asStateFlow()

    private val _sideEffectChannel = Channel<BoardSideEffect>(Channel.BUFFERED)
    val sideEffectFlow = _sideEffectChannel.receiveAsFlow()

    private val scrollState = ScrollState()
    private var verticalOverScrollJob: Job? = null
    private var horizontalOverScrollJob: Job? = null

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
            is BoardIntent.ToggleShowImages -> setShowImages()

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
            is BoardIntent.OnBackPressed -> clearEditAndCreationStates()

            // navigation
            is BoardIntent.OnNavigateBack -> navigateBack()
            is BoardIntent.OnCardClick -> navigateToCardScreen(intent.cardId)
            is BoardIntent.NavigateToSettings -> navigateToSettings()

            // App bar dropdown menu
            is BoardIntent.OpenBoardDropdownMenu -> openBoardDropdownMenu()
            is BoardIntent.CloseBoardDropdownMenu -> clearEditAndCreationStates()
            /*  Others  */


            // Drag and drop
            is BoardIntent.OnDragStart -> onDragStart(intent.offset)
            is BoardIntent.OnDrag -> onDrag(intent.position)
            is BoardIntent.OnDragStop -> onDragStop()

            // Set coordinates
            is BoardIntent.SetBoardCoordinates -> setBoardCoordinates(intent.coordinates)
            is BoardIntent.SetColumnHeaderCoordinates -> setColumnHeaderCoordinates(
                columnId = intent.columnId,
                coordinates = intent.coordinates
            )

            is BoardIntent.SetColumnBodyCoordinates -> setColumnListCoordinates(
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

        _uiState.update { it.copy(isLoading = true) }

        viewModelScope.launch(dispatcherProvider.main) {
            observeCompleteBoardUseCase(boardId).collect { result ->
                when (result) {
                    is Result.Success -> {
                        _uiState.update { currentState ->

                            val newBoard = mapToUiState(
                                newBoard = result.data,
                                oldBoard = currentState.board
                            )

                            currentState.copy(board = newBoard, isLoading = false)
                        }
                    }

                    is Result.Error -> {
                        sendSnackbar(
                            SnackbarEvent(
                                message = UiText.StringResource(R.string.board_snackbar_fetch_board_error)
                            )
                        )
                        _uiState.update { it.copy(isLoading = false) }
                    }
                }
            }
        }
    }

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


    // App bar

    private fun openBoardDropdownMenu() {
        clearEditAndCreationStates()
        _uiState.update { it.copy(isBoardDropdownMenuExpanded = true) }
    }

    private fun onRenameBoardClicked() {
        clearEditAndCreationStates()
        _uiState.update { it.copy(isRenamingBoard = true) }
    }

    private fun onDeleteBoardClicked() {
        clearEditAndCreationStates()
        _uiState.update { it.copy(isShowingDeleteBoardDialog = true) }
    }

    private fun openBoardSettings() {
        clearEditAndCreationStates()
        _uiState.update { it.copy(isModalSheetExpanded = true) }
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


    // Edit Column

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


    // Dropdown menu

    private fun openColumnDropdownMenu(columnId: Long) {
        clearEditAndCreationStates()
        _uiState.update { it.copy(activeDropdownColumnId = columnId) }
    }

    private fun closeColumnDropdownMenu() {
        _uiState.update { it.copy(activeDropdownColumnId = null) }
    }

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
                showImages = newBoard.showImages
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

    private fun enterFullScreen() {
        _uiState.update { it.copy(isOnFullScreen = true) }
    }

    private fun exitFullScreen() {
        _uiState.update { it.copy(isOnFullScreen = false) }
    }

    private fun setShowImages() {
        val board = uiState.value.board ?: return
        viewModelScope.launch(dispatcherProvider.main) {
            val updatedBoard = board.copy(showImages = !board.showImages).toBoard()
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


    // Drag and drop

    fun onDragStart(offset: Offset) {
        clearEditAndCreationStates()
        _uiState.update { currentState ->
            val board = currentState.board ?: return

            val targetColumn = findColumnWithIndex(
                offsetX = offset.x,
                columns = board.columns,
                lazyRowState = currentState.board.listState
            ) ?: return


            val isSelectingHeader = isHeaderPressed(targetColumn.second, offset.y)
            if (isSelectingHeader) {
                val newOffset = Offset(
                    x = offset.x - targetColumn.second.headerCoordinates.width / 2,
                    y = offset.y - targetColumn.second.headerCoordinates.height / 2
                )
                return@update currentState.copy(
                    dragState = currentState.dragState.copy(
                        draggingColumn = targetColumn.second,
                        selectedColumnIndex = targetColumn.first,
                        itemOffset = newOffset
                    )
                )
            }


            val targetCard = findCardWithIndex(
                column = targetColumn.second,
                offsetY = offset.y,
            ) ?: return

            val newOffset = Offset(
                x = offset.x - targetCard.second.coordinates.width / 2,
                y = offset.y - targetCard.second.coordinates.height / 2
            )

            currentState.copy(
                dragState = currentState.dragState.copy(
                    itemOffset = newOffset,
                    draggingCard = targetCard.second,
                    draggingCardIndex = targetCard.first,
                    selectedColumn = targetColumn.second,
                    selectedColumnIndex = targetColumn.first,
                )
            )
        }
    }

    fun onDrag(position: Offset?) {
        _uiState.update { state ->
            if (state.board == null) return@update state

            // Drag column
            if (state.dragState.draggingColumn != null && state.dragState.selectedColumnIndex != null) {


                val columnIndex = state.dragState.selectedColumnIndex
                val column = state.dragState.draggingColumn
                val columns = state.board.columns

                handleHorizontalScroll(column.coordinates.width)

                val newPosition = if (position != null) {
                    Offset(
                        x = position.x - column.coordinates.width / 2,
                        y = position.y - column.headerCoordinates.height / 2
                    )
                } else state.dragState.itemOffset
                val newState = state.copy(
                    dragState = state.dragState.copy(itemOffset = newPosition)
                )

                val columnCenterX = newPosition.x + column.headerCoordinates.width / 2

                val targetColumn = findColumnWithIndex(
                    offsetX = columnCenterX,
                    columns = columns,
                    lazyRowState = state.board.listState
                ) ?: return@update newState
                if (targetColumn.first == columnIndex) return@update newState

                val targetColumnCenterX = targetColumn.second.headerCoordinates.position.x +
                        targetColumn.second.headerCoordinates.width / 2


                // Reorder columns
                if (
                    (columnCenterX > targetColumnCenterX && columnIndex < targetColumn.first) ||
                    (columnCenterX < targetColumnCenterX && columnIndex > targetColumn.first)
                ) {
                    val newColumns = columns.toMutableList().apply {
                        add(targetColumn.first, removeAt(columnIndex))
                    }
                    return@update state.copy(
                        board = state.board.copy(columns = newColumns),
                        dragState = state.dragState.copy(
                            selectedColumnIndex = targetColumn.first,
                        )
                    )
                }
                return@update newState
            }

            // Drag card
            val currentCard = state.dragState.draggingCard ?: return
            val currentCardIndex = state.dragState.draggingCardIndex ?: return
            val currentColumn = state.dragState.selectedColumn ?: return
            val currentColumnIndex = state.dragState.selectedColumnIndex ?: return

            handleVerticalScroll(
                selectedCard = currentCard,
                selectedColumn = currentColumn,
                selectedColumnIndex = currentColumnIndex
            )
            handleHorizontalScroll(currentCard.coordinates.width)

            val newPosition = if (position != null) {
                Offset(
                    x = position.x - currentCard.coordinates.width / 2,
                    y = position.y - currentCard.coordinates.height / 2
                )
            } else state.dragState.itemOffset
            val newState = state.copy(dragState = state.dragState.copy(itemOffset = newPosition))

            val cardCenterX = newPosition.x + currentCard.coordinates.width / 2
            val cardCenterY = newPosition.y + currentCard.coordinates.height / 2


            val targetColumn = findColumnWithIndex(
                offsetX = cardCenterX,
                columns = state.board.columns,
                lazyRowState = state.board.listState
            ) ?: return@update newState


            // Move card to another column
            val shouldTransferCardToAnotherColumn = currentColumnIndex != targetColumn.first
            if (shouldTransferCardToAnotherColumn) {
                val newCardIndex = determineDropIndexInColumn(
                    cardOffsetY = cardCenterY,
                    targetColumn = targetColumn.second
                )

                val sourceColumnCards = currentColumn.cards.toMutableList().apply {
                    remove(currentCard)
                }
                val targetColumnCards = targetColumn.second.cards.toMutableList().apply {
                    add(newCardIndex, currentCard)
                }

                val newColumns = state.board.columns.toMutableList().apply {
                    set(currentColumnIndex, currentColumn.copy(cards = sourceColumnCards))
                    set(targetColumn.first, targetColumn.second.copy(cards = targetColumnCards))
                }


                return@update state.copy(
                    board = state.board.copy(columns = newColumns),
                    dragState = state.dragState.copy(
                        itemOffset = newPosition,
                        selectedColumn = targetColumn.second,
                        draggingCardIndex = newCardIndex,
                        selectedColumnIndex = targetColumn.first
                    )
                )
            }


            val targetCard = findCardWithIndex(
                column = targetColumn.second,
                offsetY = cardCenterY
            ) ?: return@update newState


            // Reorder card
            val targetCardCenterY = targetCard.second.coordinates.position.y +
                    targetCard.second.coordinates.height / 2

            if (
                (currentCardIndex < targetCard.first && cardCenterY > targetCardCenterY) ||
                (currentCardIndex > targetCard.first && cardCenterY < targetCardCenterY)
            ) {
                val newColumns = state.board.columns.toMutableList()

                val newCards = newColumns[currentColumnIndex].cards.toMutableList().apply {
                    add(
                        index = targetCard.first,
                        element = removeAt(currentCardIndex)
                    )
                }.toList()
                newColumns[currentColumnIndex] =
                    newColumns[currentColumnIndex].copy(cards = newCards)


                return@update state.copy(
                    board = state.board.copy(columns = newColumns),
                    dragState = state.dragState.copy(
                        itemOffset = newPosition,
                        draggingCardIndex = targetCard.first
                    )
                )
            }


            newState
        }
    }

    fun onDragStop() {
        _uiState.update { it.copy(dragState = DragState()) }
        cancelAutoScroll()
        updatePositions()
    }

    private fun isHeaderPressed(
        column: ColumnUi,
        offsetY: Float,
    ): Boolean {
        val headerTop = column.headerCoordinates.position.y
        val headerBottom = column.headerCoordinates.position.y + column.headerCoordinates.height
        return offsetY in headerTop..headerBottom
    }

    private fun determineDropIndexInColumn(
        cardOffsetY: Float,
        targetColumn: ColumnUi
    ): Int {
        val potentialTargetIndex = findCardWithIndex(
            column = targetColumn,
            offsetY = cardOffsetY
        )?.first ?: -1

        val targetNotFound = potentialTargetIndex == -1
        val columnHasCards = targetColumn.cards.isNotEmpty()

        if (targetNotFound && columnHasCards) {
            val lastCardInColumn = targetColumn.cards.last()
            val lastCardCenterY = lastCardInColumn.coordinates.position.y +
                    lastCardInColumn.coordinates.height / 2

            return if (cardOffsetY > lastCardCenterY) {
                targetColumn.cards.size
            } else {
                0
            }
        } else if (targetNotFound) { // Didn't found card and column has no cards
            return 0
        } else {
            return potentialTargetIndex
        }
    }

    private fun findColumnWithIndex(
        offsetX: Float,
        columns: List<ColumnUi>,
        lazyRowState: LazyListState
    ): Pair<Int, ColumnUi>? {
        val columnsOnScreen = lazyRowState.layoutInfo.visibleItemsInfo.map { it.index }

        val targetColumn = columns.filterIndexed { index, column ->
            val columnStart = column.coordinates.position.x
            val columnEnd = column.coordinates.position.x + column.coordinates.width
            val columnRange = columnStart..columnEnd

            offsetX in columnRange && index in columnsOnScreen
        }.firstOrNull() ?: return null

        val targetColumnIndex = columns
            .indexOfFirst { it.id == targetColumn.id }
            .takeIf { it != -1 }
            ?: return null

        return targetColumnIndex to targetColumn
    }

    private fun findCardWithIndex(column: ColumnUi, offsetY: Float): Pair<Int, CardUi>? {
        val cardsOnScreen = column.listState.layoutInfo.visibleItemsInfo.map { it.index }

        val targetCard = column.cards.filterIndexed { index, card ->
            val cardStart = card.coordinates.position.y
            val cardEnd = card.coordinates.position.y + card.coordinates.height
            val cardRange = cardStart..cardEnd

            offsetY in cardRange && index in cardsOnScreen
        }.firstOrNull() ?: return null

        val targetCardIndex = column.cards
            .indexOfFirst { it.id == targetCard.id }
            .takeIf { it != -1 }
            ?: return null

        return targetCardIndex to targetCard
    }


    // Auto scroll

    private fun handleVerticalScroll(
        selectedCard: CardUi,
        selectedColumn: ColumnUi,
        selectedColumnIndex: Int
    ) {
        if (scrollState.scrollingColumnIndex != selectedColumnIndex) {
            cancelVerticalScroll()
            scrollState.scrollingColumnIndex = selectedColumnIndex
        }

        val dragState = uiState.value.dragState

        val cardTop = dragState.itemOffset.y
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

                verticalOverScrollJob = viewModelScope.launch {
                    while (scrollState.isVerticalScrolling) {
                        try {
                            delay(16)
                            val speed = scrollState.verticalSpeed
                            selectedColumn.listState.scrollBy(speed)
                            onDrag(null)

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

    private fun handleHorizontalScroll(itemWidth: Int) {
        val board = uiState.value.board ?: return
        val dragState = uiState.value.dragState

        val itemLeft = dragState.itemOffset.x
        val itemRight = itemLeft + itemWidth
        val boardLeft = board.coordinates.position.x
        val boardRight = boardLeft + board.coordinates.width

        val canScrollLeft = board.listState.canScrollBackward
        val canScrollRight = board.listState.canScrollForward
        val isCardBeyondLeftEdge = itemLeft < boardLeft
        val isCardBeyondRightEdge = itemRight > boardRight

        val shouldScrollLeft = canScrollLeft && isCardBeyondLeftEdge
        val shouldScrollRight = canScrollRight && isCardBeyondRightEdge

        scrollState.horizontalSpeed = if (shouldScrollLeft) {
            (itemLeft - boardLeft) / 6
        } else if (shouldScrollRight) {
            (itemRight - boardRight) / 6
        } else {
            0f
        }

        if (shouldScrollLeft || shouldScrollRight) {
            if (!scrollState.isHorizontalScrolling) {
                scrollState.isHorizontalScrolling = true

                horizontalOverScrollJob = viewModelScope.launch {
                    while (scrollState.isHorizontalScrolling) {
                        try {
                            delay(16)
                            val speed = scrollState.horizontalSpeed
                            board.listState.scrollBy(speed)
                            onDrag(null)
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

    private fun cancelVerticalScroll() {
        scrollState.isVerticalScrolling = false
        scrollState.verticalSpeed = 0f
        scrollState.scrollingColumnIndex = null
        verticalOverScrollJob?.cancel()
        verticalOverScrollJob = null
    }

    private fun cancelHorizontalScroll() {
        scrollState.isHorizontalScrolling = false
        scrollState.horizontalSpeed = 0f
        horizontalOverScrollJob?.cancel()
        horizontalOverScrollJob = null
    }

    private fun cancelAutoScroll() {
        cancelVerticalScroll()
        cancelHorizontalScroll()
    }


    // Set coordinates
    private val debounceTime = 200L
    private val throttleTime = 16L

    private fun setBoardCoordinates(coordinates: Coordinates) {
        if (uiState.value.board?.coordinates != coordinates) {
            _uiState.update { currentState ->
                currentState.copy(board = currentState.board?.copy(coordinates = coordinates))
            }
        }
    }


    private val pendingColHeaderCoordsUpdates = mutableMapOf<Long, Coordinates>()
    private var columnHeaderCoordinatesUpdateJob: Job? = null
    private var lastTimeColHeaderCoordUpdateCalled = 0L
    private fun setColumnHeaderCoordinates(columnId: Long, coordinates: Coordinates) {

        pendingColHeaderCoordsUpdates[columnId] = coordinates

        if (uiState.value.dragState != DragState()) {

            if (System.currentTimeMillis() - throttleTime < lastTimeColHeaderCoordUpdateCalled) return

            lastTimeColHeaderCoordUpdateCalled = System.currentTimeMillis()
            if (pendingColHeaderCoordsUpdates.isEmpty()) return

            val updatesToProcess = HashMap(pendingColHeaderCoordsUpdates)
            pendingColHeaderCoordsUpdates.clear()

            _uiState.update { currentState ->

                var updatedBoard = currentState.board ?: return@update currentState

                updatesToProcess.forEach { (columnId, coordinates) ->
                    updatedBoard = updatedBoard.copy(
                        columns = updatedBoard.columns.map { column ->
                            if (column.id == columnId) {
                                column.copy(headerCoordinates = coordinates)
                            } else {
                                column
                            }
                        }
                    )
                }
                currentState.copy(board = updatedBoard)
            }

        } else {

            columnHeaderCoordinatesUpdateJob?.cancel()
            columnHeaderCoordinatesUpdateJob = viewModelScope.launch(dispatcherProvider.main) {
                delay(debounceTime)

                if (pendingColHeaderCoordsUpdates.isEmpty()) return@launch

                val updatesToProcess = HashMap(pendingColHeaderCoordsUpdates)
                pendingColHeaderCoordsUpdates.clear()

                _uiState.update { currentState ->

                    var updatedBoard = currentState.board ?: return@update currentState

                    updatesToProcess.forEach { (columnId, coordinates) ->
                        updatedBoard = updatedBoard.copy(
                            columns = updatedBoard.columns.map { column ->
                                if (column.id == columnId) {
                                    column.copy(headerCoordinates = coordinates)
                                } else {
                                    column
                                }
                            }
                        )
                    }
                    currentState.copy(board = updatedBoard)
                }
            }
        }


    }


    private val pendingColListCoordsUpdates = mutableMapOf<Long, Coordinates>()
    private var columnListCoordinatesUpdateJob: Job? = null
    private var lastTimeColListCoordUpdateCalled = 0L
    private fun setColumnListCoordinates(columnId: Long, coordinates: Coordinates) {

        pendingColListCoordsUpdates[columnId] = coordinates

        if (uiState.value.dragState != DragState()) {

            if (System.currentTimeMillis() - throttleTime < lastTimeColListCoordUpdateCalled) return

            lastTimeColListCoordUpdateCalled = System.currentTimeMillis()
            if (pendingColListCoordsUpdates.isEmpty()) return

            val updatesToProcess = HashMap(pendingColListCoordsUpdates)
            pendingColListCoordsUpdates.clear()

            _uiState.update { currentState ->

                var updatedBoard = currentState.board ?: return@update currentState

                updatesToProcess.forEach { (columnId, coordinates) ->
                    updatedBoard = updatedBoard.copy(
                        columns = updatedBoard.columns.map { column ->
                            if (column.id == columnId) {
                                column.copy(listCoordinates = coordinates)
                            } else {
                                column
                            }
                        }
                    )
                }
                currentState.copy(board = updatedBoard)
            }

        } else {

            columnListCoordinatesUpdateJob?.cancel()
            columnListCoordinatesUpdateJob = viewModelScope.launch(dispatcherProvider.main) {
                delay(debounceTime)

                if (pendingColListCoordsUpdates.isEmpty()) return@launch

                val updatesToProcess = HashMap(pendingColListCoordsUpdates)
                pendingColListCoordsUpdates.clear()

                _uiState.update { currentState ->

                    var updatedBoard = currentState.board ?: return@update currentState

                    updatesToProcess.forEach { (columnId, coordinates) ->
                        updatedBoard = updatedBoard.copy(
                            columns = updatedBoard.columns.map { column ->
                                if (column.id == columnId) {
                                    column.copy(listCoordinates = coordinates)
                                } else {
                                    column
                                }
                            }
                        )
                    }
                    currentState.copy(board = updatedBoard)
                }
            }
        }


    }


    private val pendingColCoordsUpdates = mutableMapOf<Long, Coordinates>()
    private var columnCoordinatesUpdateJob: Job? = null
    private var lastTimeColCoordUpdateCalled = 0L
    private fun setColumnCoordinates(columnId: Long, coordinates: Coordinates) {

        pendingColCoordsUpdates[columnId] = coordinates

        if (uiState.value.dragState != DragState()) {

            if (System.currentTimeMillis() - throttleTime < lastTimeColCoordUpdateCalled) return

            lastTimeColCoordUpdateCalled = System.currentTimeMillis()
            if (pendingColCoordsUpdates.isEmpty()) return

            val updatesToProcess = HashMap(pendingColCoordsUpdates)
            pendingColCoordsUpdates.clear()

            _uiState.update { currentState ->

                var updatedBoard = currentState.board ?: return@update currentState

                updatesToProcess.forEach { (columnId, coordinates) ->
                    updatedBoard = updatedBoard.copy(
                        columns = updatedBoard.columns.map { column ->
                            if (column.id == columnId) {
                                column.copy(coordinates = coordinates)
                            } else {
                                column
                            }
                        }
                    )
                }
                currentState.copy(board = updatedBoard)
            }

        } else {

            columnCoordinatesUpdateJob?.cancel()
            columnCoordinatesUpdateJob = viewModelScope.launch(dispatcherProvider.main) {
                delay(debounceTime)

                if (pendingColCoordsUpdates.isEmpty()) return@launch

                val updatesToProcess = HashMap(pendingColCoordsUpdates)
                pendingColCoordsUpdates.clear()

                _uiState.update { currentState ->

                    var updatedBoard = currentState.board ?: return@update currentState

                    updatesToProcess.forEach { (columnId, coordinates) ->
                        updatedBoard = updatedBoard.copy(
                            columns = updatedBoard.columns.map { column ->
                                if (column.id == columnId) {
                                    column.copy(coordinates = coordinates)
                                } else {
                                    column
                                }
                            }
                        )
                    }
                    currentState.copy(board = updatedBoard)
                }
            }
        }


    }


    private val pendingCardCoordsUpdates = mutableMapOf<Long, Pair<Long, Coordinates>>()
    private var cardCoordinatesUpdateJob: Job? = null
    private var lastTimeCardCoordUpdateCalled = 0L
    private fun setCardCoordinates(cardId: Long, columnId: Long, coordinates: Coordinates) {

        pendingCardCoordsUpdates[cardId] = Pair(columnId, coordinates)

        if (uiState.value.dragState != DragState()) {

            if (System.currentTimeMillis() - throttleTime < lastTimeCardCoordUpdateCalled) return

            lastTimeCardCoordUpdateCalled = System.currentTimeMillis()
            if (pendingCardCoordsUpdates.isEmpty()) return

            val updatesToProcess = HashMap(pendingCardCoordsUpdates)
            pendingCardCoordsUpdates.clear()

            _uiState.update { currentState ->
                var updatedBoard = currentState.board ?: return@update currentState

                updatesToProcess.forEach { (currentCardId, idAndCoords) ->
                    val currentColumnId = idAndCoords.first
                    val newCoordinates = idAndCoords.second

                    updatedBoard = updatedBoard.copy(
                        columns = updatedBoard.columns.map { column ->
                            if (column.id == currentColumnId) {
                                column.copy(cards = column.cards.map { card ->
                                    if (card.id == currentCardId) {
                                        card.copy(coordinates = newCoordinates)
                                    } else {
                                        card
                                    }
                                })
                            } else {
                                column
                            }
                        }
                    )
                }
                currentState.copy(board = updatedBoard)
            }

        } else {

            cardCoordinatesUpdateJob?.cancel()
            cardCoordinatesUpdateJob = viewModelScope.launch(dispatcherProvider.main) {
                delay(debounceTime)

                if (pendingCardCoordsUpdates.isEmpty()) return@launch

                val updatesToProcess = HashMap(pendingCardCoordsUpdates)
                pendingCardCoordsUpdates.clear()

                _uiState.update { currentState ->
                    var updatedBoard = currentState.board ?: return@update currentState

                    updatesToProcess.forEach { (currentCardId, idAndCoords) ->
                        val currentColumnId = idAndCoords.first
                        val newCoordinates = idAndCoords.second

                        updatedBoard = updatedBoard.copy(
                            columns = updatedBoard.columns.map { column ->
                                if (column.id == currentColumnId) {
                                    column.copy(cards = column.cards.map { card ->
                                        if (card.id == currentCardId) {
                                            card.copy(coordinates = newCoordinates)
                                        } else {
                                            card
                                        }
                                    })
                                } else {
                                    column
                                }
                            }
                        )
                    }
                    currentState.copy(board = updatedBoard)
                }
            }
        }
    }


    override fun onCleared() {
        super.onCleared()
        cancelAutoScroll()
        columnHeaderCoordinatesUpdateJob?.cancel()
        columnCoordinatesUpdateJob?.cancel()
        columnListCoordinatesUpdateJob?.cancel()
        cardCoordinatesUpdateJob?.cancel()
    }
}