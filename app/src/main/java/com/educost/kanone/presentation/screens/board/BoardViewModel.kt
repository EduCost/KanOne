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
import com.educost.kanone.domain.model.CardItem
import com.educost.kanone.domain.model.KanbanColumn
import com.educost.kanone.domain.usecase.CreateCardUseCase
import com.educost.kanone.domain.usecase.CreateColumnUseCase
import com.educost.kanone.domain.usecase.ObserveCompleteBoardUseCase
import com.educost.kanone.presentation.screens.board.components.BoardAppBarType
import com.educost.kanone.presentation.screens.board.mapper.toBoardUi
import com.educost.kanone.presentation.screens.board.mapper.toCardUi
import com.educost.kanone.presentation.screens.board.mapper.toColumnUi
import com.educost.kanone.presentation.screens.board.model.BoardUi
import com.educost.kanone.presentation.screens.board.model.CardUi
import com.educost.kanone.presentation.screens.board.model.ColumnUi
import com.educost.kanone.presentation.screens.board.model.Coordinates
import com.educost.kanone.presentation.screens.board.state.BoardState
import com.educost.kanone.presentation.screens.board.state.CardCreationState
import com.educost.kanone.presentation.screens.board.state.DragState
import com.educost.kanone.presentation.screens.board.state.ScrollState
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
import java.time.LocalDateTime
import javax.inject.Inject

@HiltViewModel
class BoardViewModel @Inject constructor(
    private val dispatcherProvider: DispatcherProvider,
    private val observeCompleteBoardUseCase: ObserveCompleteBoardUseCase,
    private val createColumnUseCase: CreateColumnUseCase,
    private val createCardUseCase: CreateCardUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(BoardState())
    val uiState = _uiState.asStateFlow()

    private val _sideEffectChannel = Channel<BoardSideEffect>(Channel.BUFFERED)
    val sideEffectFlow = _sideEffectChannel.receiveAsFlow()

    private val scrollState = ScrollState()

    private var verticalOverScrollJob: Job? = null

    fun onIntent(intent: BoardIntent) {
        when (intent) {
            is BoardIntent.ObserveBoard -> observeBoard(intent.boardId)

            // Drag and drop
            is BoardIntent.OnDrag -> onDrag(intent.position)
            is BoardIntent.OnDragStart -> onDragStart(intent.offset)
            is BoardIntent.OnDragStop -> onDragStop()

            // Create card
            is BoardIntent.CancelCardCreation -> cancelCardCreation()
            is BoardIntent.ConfirmCardCreation -> confirmCardCreation()
            is BoardIntent.OnCardTitleChange -> onCardTitleChange(intent.title)
            is BoardIntent.StartCreatingCard -> startCreatingCard(intent.columnId)

            // Create column
            is BoardIntent.StartCreatingColumn -> startCreatingColumn()
            is BoardIntent.OnColumnNameChanged -> onColumnNameChanged(intent.name)
            is BoardIntent.CancelColumnCreation -> cancelColumnCreation()
            is BoardIntent.ConfirmColumnCreation -> confirmColumnCreation()

            // Set coordinates
            is BoardIntent.SetBoardCoordinates -> setBoardCoordinates(intent.coordinates)
            is BoardIntent.SetColumnHeaderCoordinates -> setColumnHeaderCoordinates(
                columnId = intent.columnId,
                coordinates = intent.coordinates
            )

            is BoardIntent.SetColumnBodyCoordinates -> setColumnBodyCoordinates(
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

    // Drag and drop
    fun onDragStart(offset: Offset) {

        _uiState.update { currentState ->
            val board = currentState.board ?: return

            val targetColumn = findColumnWithIndex(
                offsetX = offset.x,
                columns = board.columns,
                lazyRowState = currentState.board.listState
            ) ?: return

            val newOffset = Offset(
                x = offset.x - targetColumn.second.headerCoordinates.width / 2,
                y = offset.y - targetColumn.second.headerCoordinates.height / 2
            )

            val isSelectingHeader = isHeaderPressed(targetColumn.second, offset.y)
            if (isSelectingHeader) {
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

                val newPosition = if (position != null) {
                    Offset(
                        x = position.x - column.coordinates.width / 2,
                        y = position.y - column.headerCoordinates.height / 2
                    )
                } else state.dragState.itemOffset
                val newState =
                    state.copy(dragState = state.dragState.copy(itemOffset = newPosition))

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


            val targetCard = findCardWithIndex(
                column = targetColumn.second,
                offsetY = cardCenterY
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
        cancelVerticalScroll()
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
        } else if (targetNotFound && !columnHasCards) {
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
        val columnTop = selectedColumn.bodyCoordinates.position.y
        val columnBottom = columnTop + selectedColumn.bodyCoordinates.height

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

                        } catch (e: Exception) {
                            scrollState.isVerticalScrolling = false
                            break
                        }
                    }
                }
            }
        } else {
            cancelVerticalScroll()
        }
        /*val speed = if (shouldScrollUp) {
            (cardTop - columnTop) / 3
        } else if (shouldScrollDown) {
            (cardBottom - columnBottom) / 3
        } else {
            0f
        }
        _uiState.update { it.copy(scrollState = it.scrollState.copy(verticalSpeed = speed)) }

        if ((shouldScrollUp || shouldScrollDown)) {
            if (!uiState.value.scrollState.isVerticalScrolling) {

                _uiState.update { it.copy(scrollState = it.scrollState.copy(isVerticalScrolling = true)) }

                verticalOverScrollJob = viewModelScope.launch {
                    while (_uiState.value.scrollState.isVerticalScrolling) {
                        try {
                            delay(16)
                            val speed = uiState.value.scrollState.verticalSpeed
                            selectedColumn.listState.scrollBy(speed)
                            onDrag(null)

                        } catch (e: Exception) {
                            _uiState.update {
                                it.copy(
                                    scrollState = it.scrollState.copy(
                                        isVerticalScrolling = false
                                    )
                                )
                            }
                            break
                        }
                    }
                }
            }
        } else {
            cancelVerticalScroll()
        }*/
    }

    private fun cancelVerticalScroll() {
        scrollState.isVerticalScrolling = false
        scrollState.verticalSpeed = 0f
        scrollState.scrollingColumnIndex = null
        verticalOverScrollJob?.cancel()
        verticalOverScrollJob = null
    }

    // Create card
    private fun cancelCardCreation() {
        _uiState.update {
            it.copy(
                cardCreationState = CardCreationState(),
                topBarType = BoardAppBarType.DEFAULT
            )
        }
    }

    private fun confirmCardCreation() {
        uiState.value.cardCreationState.let {

            val position = uiState.value.board?.columns?.find { column ->
                column.id == it.columnId
            }?.cards?.size

            if (it.title == null || it.columnId == null || position == null) {
                viewModelScope.launch(dispatcherProvider.main) {
                    sendSnackbar(
                        SnackbarEvent(
                            message = UiText.StringResource(R.string.board_snackbar_card_creation_error),
                            duration = SnackbarDuration.Long,
                            withDismissAction = true
                        )
                    )
                }
            } else {
                val card = CardItem(
                    id = 0,
                    title = it.title,
                    description = null,
                    position = position,
                    color = null,
                    createdAt = LocalDateTime.now(),
                    dueDate = null,
                    thumbnailFileName = null,
                    checklists = emptyList(),
                    attachments = emptyList(),
                    labels = emptyList()
                )
                viewModelScope.launch(dispatcherProvider.main) {
                    val result = createCardUseCase(card = card, columnId = it.columnId)

                    when (result) {
                        is Result.Error -> sendSnackbar(
                            SnackbarEvent(
                                message = UiText.StringResource(R.string.board_snackbar_card_creation_error),
                                duration = SnackbarDuration.Long,
                                withDismissAction = true
                            )
                        )

                        is Result.Success -> Unit
                    }
                }
            }
            cancelCardCreation()
        }
    }

    private fun onCardTitleChange(newTitle: String) {
        _uiState.update { it.copy(cardCreationState = it.cardCreationState.copy(title = newTitle)) }
    }

    private fun startCreatingCard(columnId: Long) {
        _uiState.update {
            it.copy(
                cardCreationState = CardCreationState(columnId = columnId),
                topBarType = BoardAppBarType.ADD_CARD
            )
        }
    }

    // Create column
    private fun startCreatingColumn() {
        _uiState.update { it.copy(topBarType = BoardAppBarType.ADD_COLUMN) }
    }

    private fun onColumnNameChanged(name: String) {
        _uiState.update { it.copy(creatingColumnName = name) }
    }

    private fun cancelColumnCreation() {
        _uiState.update {
            it.copy(
                topBarType = BoardAppBarType.DEFAULT,
                creatingColumnName = null
            )
        }
    }

    private fun confirmColumnCreation() {
        uiState.value.board?.let { board ->
            val column = KanbanColumn(
                id = 0,
                name = uiState.value.creatingColumnName ?: "",
                position = board.columns.size,
                color = null,
                cards = emptyList()
            )
            viewModelScope.launch(dispatcherProvider.main) {
                when (createColumnUseCase(column = column, boardId = board.id)) {
                    is Result.Error -> sendSnackbar(
                        SnackbarEvent(
                            message = UiText.StringResource(R.string.board_snackbar_column_creation_error),
                            withDismissAction = true
                        )
                    )

                    is Result.Success -> Unit
                }
            }

        } ?: viewModelScope.launch(dispatcherProvider.main) {
            sendSnackbar(
                SnackbarEvent(
                    message = UiText.StringResource(R.string.board_snackbar_column_creation_error),
                    withDismissAction = true
                )
            )
        }

        cancelColumnCreation()
    }


    // Set coordinates
    private fun setBoardCoordinates(coordinates: Coordinates) {
        _uiState.update { currentState ->
            currentState.copy(board = currentState.board?.copy(coordinates = coordinates))
        }
    }

    private fun setColumnHeaderCoordinates(columnId: Long, coordinates: Coordinates) {
        _uiState.update { currentState ->
            val updatedBoard = currentState.board?.copy(
                columns = currentState.board.columns.map { column ->
                    if (column.id == columnId) {
                        column.copy(headerCoordinates = coordinates)
                    } else {
                        column
                    }
                }
            )

            currentState.copy(board = updatedBoard)
        }
    }

    private fun setColumnBodyCoordinates(columnId: Long, coordinates: Coordinates) {
        _uiState.update { currentState ->
            val updatedBoard = currentState.board?.copy(
                columns = currentState.board.columns.map { column ->
                    if (column.id == columnId) {
                        column.copy(bodyCoordinates = coordinates)
                    } else {
                        column
                    }
                }
            )

            currentState.copy(board = updatedBoard)
        }
    }

    private fun setColumnCoordinates(columnId: Long, coordinates: Coordinates) {
        _uiState.update { currentState ->
            val updatedBoard = currentState.board?.copy(
                columns = currentState.board.columns.map { column ->
                    if (column.id == columnId) {
                        column.copy(coordinates = coordinates)
                    } else {
                        column
                    }
                }
            )

            currentState.copy(board = updatedBoard)
        }
    }

    private fun setCardCoordinates(cardId: Long, columnId: Long, coordinates: Coordinates) {
        _uiState.update { currentState ->
            val updatedBoard = currentState.board?.copy(
                columns = currentState.board.columns.map { column ->
                    if (column.id == columnId) {
                        column.copy(cards = column.cards.map { card ->
                            if (card.id == cardId) {
                                card.copy(coordinates = coordinates)
                            } else {
                                card
                            }
                        })
                    } else {
                        column
                    }
                }
            )

            currentState.copy(board = updatedBoard)
        }
    }

    // Helper functions
    private fun mapToUiState(newBoard: Board, oldBoard: BoardUi?): BoardUi {

        if (oldBoard == null) {
            return newBoard.toBoardUi()
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
                                thumbnailFileName = cards.thumbnailFileName,
                                checklists = cards.checklists,
                                attachments = cards.attachments,
                                labels = cards.labels
                            )
                                ?: cards.toCardUi()
                        }
                    )
                        ?: column.toColumnUi()
                }
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
}