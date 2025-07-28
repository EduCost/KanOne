package com.educost.kanone.presentation.screens.board

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.educost.kanone.R
import com.educost.kanone.dispatchers.DispatcherProvider
import com.educost.kanone.domain.error.FetchDataError
import com.educost.kanone.domain.model.KanbanColumn
import com.educost.kanone.domain.usecase.CreateColumnUseCase
import com.educost.kanone.domain.usecase.ObserveCompleteBoardUseCase
import com.educost.kanone.presentation.mapper.toBoardUi
import com.educost.kanone.presentation.mapper.toCardUi
import com.educost.kanone.presentation.mapper.toColumnUi
import com.educost.kanone.presentation.model.Coordinates
import com.educost.kanone.presentation.screens.board.components.BoardAppBarType
import com.educost.kanone.presentation.theme.Palette
import com.educost.kanone.presentation.util.SnackbarEvent
import com.educost.kanone.presentation.util.UiText
import com.educost.kanone.utils.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
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
    private val createColumnUseCase: CreateColumnUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(BoardState())
    val uiState = _uiState.asStateFlow()

    private val _sideEffectChannel = Channel<BoardSideEffect>(Channel.BUFFERED)
    val sideEffectFlow = _sideEffectChannel.receiveAsFlow()


    fun onIntent(intent: BoardIntent) {
        when (intent) {
            is BoardIntent.ObserveBoard -> observeBoard(intent.boardId)

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

                            val newBoard = result.data

                            if (currentState.board == null) {

                                currentState.copy(
                                    board = newBoard.toBoardUi(),
                                    isLoading = false

                                )

                            } else {

                                val board = currentState.board.copy(
                                    id = newBoard.id,
                                    name = newBoard.name,
                                    columns = newBoard.columns.map { column ->

                                        val isNewColumn = currentState.board.columns.find {
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

                                currentState.copy(board = board, isLoading = false)
                            }
                        }
                    }

                    is Result.Error -> {
                        _sideEffectChannel.send(
                            BoardSideEffect.ShowSnackBar(
                                SnackbarEvent(
                                    message = UiText.StringResource(R.string.board_snackbar_fetch_board_error)
                                )
                            )
                        )
                        _uiState.update { it.copy(isLoading = false) }
                    }
                }
            }
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
        _uiState.update { it.copy(topBarType = BoardAppBarType.DEFAULT, creatingColumnName = null) }
    }

    private fun confirmColumnCreation() {
        uiState.value.board?.let { board ->
            val column = KanbanColumn(
                id = 0,
                name = uiState.value.creatingColumnName ?: "",
                position = board.columns.size,
                color = Palette.NONE,
                cards = emptyList()
            )
            viewModelScope.launch(dispatcherProvider.main) {
                when (createColumnUseCase(column = column, boardId = board.id)) {
                    is Result.Error -> {
                        _sideEffectChannel.send(
                            BoardSideEffect.ShowSnackBar(
                                SnackbarEvent(
                                    message = UiText.StringResource(R.string.board_snackbar_column_creation_error),
                                    withDismissAction = true
                                )
                            )
                        )
                    }

                    is Result.Success -> Unit
                }
            }

        } ?: viewModelScope.launch(dispatcherProvider.main) {
            _sideEffectChannel.send(
                BoardSideEffect.ShowSnackBar(
                    SnackbarEvent(
                        message = UiText.StringResource(R.string.board_snackbar_column_creation_error),
                        withDismissAction = true
                    )
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
}