package com.educost.kanone.presentation.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.educost.kanone.R
import com.educost.kanone.domain.model.Board
import com.educost.kanone.domain.usecase.CreateBoardUseCase
import com.educost.kanone.domain.usecase.DeleteBoardUseCase
import com.educost.kanone.domain.usecase.ObserveAllBoardsUseCase
import com.educost.kanone.domain.usecase.UpdateBoardUseCase
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

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val observeAllBoardsUseCase: ObserveAllBoardsUseCase,
    private val createBoardUseCase: CreateBoardUseCase,
    private val updateBoardUseCase: UpdateBoardUseCase,
    private val deleteBoardUseCase: DeleteBoardUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState = _uiState.asStateFlow()

    private val _sideEffectChannel = Channel<HomeSideEffect>(Channel.BUFFERED)
    val sideEffectFlow = _sideEffectChannel.receiveAsFlow()

    init {
        observeAllBoards()
    }

    fun onIntent(intent: HomeIntent) {
        when (intent) {
            // Create Board
            is HomeIntent.CreateBoard -> createBoard(intent.boardName)

            // Rename Board
            is HomeIntent.RenameBoardClicked -> renameBoardClicked(intent.boardId)
            is HomeIntent.OnConfirmRenameBoard -> onConfirmRenameBoard(intent.newName)
            is HomeIntent.OnCancelRenameBoard -> onCancelRenameBoard()

            // Delete Board
            is HomeIntent.DeleteBoardClicked -> deleteBoardClicked(intent.boardId)
            is HomeIntent.OnConfirmDeleteBoard -> onConfirmDeleteBoard()
            is HomeIntent.OnCancelDeleteBoard -> onCancelDeleteBoard()

            // Navigate
            is HomeIntent.NavigateToBoardScreen -> navigateToBoardScreen(intent.boardId)
            is HomeIntent.NavigateToSettingsScreen -> navigateToSettingsScreen()
        }
    }

    private fun observeAllBoards() {

        _uiState.update { it.copy(isLoading = true) }

        viewModelScope.launch {
            observeAllBoardsUseCase().collect { boardsResult ->
                when (boardsResult) {
                    is Result.Success -> {
                        _uiState.update {
                            it.copy(
                                boards = boardsResult.data,
                                isLoading = false
                            )
                        }
                    }

                    is Result.Error -> sendSnackbar(
                        SnackbarEvent(
                            message = UiText.StringResource(R.string.home_snackbar_observe_boards_error),
                            withDismissAction = true
                        )
                    )
                }
            }
            _uiState.update { it.copy(isLoading = false) }
        }
    }

    private fun createBoard(boardName: String) {
        val newBoard = Board(
            id = 0,
            name = boardName,
            columns = emptyList()
        )
        viewModelScope.launch {
            val wasBoardCreated = createBoardUseCase(newBoard)

            if (!wasBoardCreated) sendSnackbar(
                SnackbarEvent(
                    message = UiText.StringResource(R.string.home_snackbar_creating_board_error)
                )
            )
        }
    }


    // Rename Board
    private fun renameBoardClicked(boardId: Long) {
        _uiState.update { it.copy(boardBeingRenamed = boardId) }
    }

    private fun onConfirmRenameBoard(newName: String) {
        val boardId = uiState.value.boardBeingRenamed
        val board = uiState.value.boards.find { it.id == boardId }

        if (board == null) {
            sendSnackbar(
                SnackbarEvent(
                    message = UiText.StringResource(R.string.snackbar_rename_board_error),
                    withDismissAction = true
                )
            )
            return
        }

        val newBoard = board.copy(name = newName)

        viewModelScope.launch {
            val wasBoardRenamed = updateBoardUseCase(newBoard)

            if (!wasBoardRenamed) sendSnackbar(
                SnackbarEvent(
                    message = UiText.StringResource(R.string.snackbar_rename_board_error),
                    withDismissAction = true
                )
            )

            onCancelRenameBoard()
        }
    }

    private fun onCancelRenameBoard() {
        _uiState.update { it.copy(boardBeingRenamed = null) }
    }


    // Delete Board
    private fun deleteBoardClicked(boardId: Long) {
        _uiState.update { it.copy(boardBeingDeleted = boardId) }
    }

    private fun onConfirmDeleteBoard() {
        val boardId = uiState.value.boardBeingDeleted
        val board = uiState.value.boards.find { it.id == boardId }

        if (board == null) {
            sendSnackbar(
                SnackbarEvent(
                    message = UiText.StringResource(R.string.snackbar_delete_board_error),
                    withDismissAction = true
                )
            )
            return
        }

        viewModelScope.launch {
            val wasBoardDeleted = deleteBoardUseCase(board)

            if (!wasBoardDeleted) sendSnackbar(
                SnackbarEvent(
                    message = UiText.StringResource(R.string.snackbar_delete_board_error),
                    withDismissAction = true
                )
            )

            onCancelDeleteBoard()
        }
    }

    private fun onCancelDeleteBoard() {
        _uiState.update { it.copy(boardBeingDeleted = null) }
    }


    // Navigate
    private fun navigateToBoardScreen(boardId: Long) {
        viewModelScope.launch {
            _sideEffectChannel.send(HomeSideEffect.NavigateToBoardScreen(boardId))
        }
    }

    private fun navigateToSettingsScreen() {
        viewModelScope.launch {
            _sideEffectChannel.send(HomeSideEffect.OnNavigateToSettings)
        }
    }


    // Utils
    private fun sendSnackbar(snackbarEvent: SnackbarEvent) {
        viewModelScope.launch {
            _sideEffectChannel.send(
                HomeSideEffect.ShowSnackBar(
                    snackbarEvent
                )
            )
        }
    }
}