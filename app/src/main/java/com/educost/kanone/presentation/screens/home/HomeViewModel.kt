package com.educost.kanone.presentation.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.educost.kanone.R
import com.educost.kanone.domain.error.FetchDataError
import com.educost.kanone.domain.error.InsertDataError
import com.educost.kanone.domain.model.Board
import com.educost.kanone.domain.usecase.CreateBoardUseCase
import com.educost.kanone.domain.usecase.ObserveAllBoardsUseCase
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
    private val createBoardUseCase: CreateBoardUseCase
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
            is HomeIntent.CreateBoard -> createBoard(intent.board)
            is HomeIntent.ShowCreateBoardDialog -> showCreateBoardDialog()
            is HomeIntent.DismissCreateBoardDialog -> dismissCreateBoardDialog()
            is HomeIntent.OnNewBoardNameChange -> onNewBoardNameChange(intent.newBoardName)
            is HomeIntent.NavigateToBoardScreen -> navigateToBoardScreen(intent.boardId)
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

                    is Result.Error -> {

                        when (boardsResult.error) {
                            FetchDataError.IO_ERROR -> {
                                _sideEffectChannel.send(
                                    HomeSideEffect.ShowSnackBar(
                                        SnackbarEvent(
                                            message = UiText.StringResource(R.string.fetch_io_error)
                                        )
                                    )
                                )
                            }

                            FetchDataError.UNKNOWN -> {
                                _sideEffectChannel.send(
                                    HomeSideEffect.ShowSnackBar(
                                        SnackbarEvent(
                                            message = UiText.StringResource(R.string.unknown_error)
                                        )
                                    )
                                )
                            }
                        }

                        _uiState.update { it.copy(isLoading = false) }
                    }
                }
            }
        }
    }

    private fun createBoard(board: Board) {
        viewModelScope.launch {
            val result = createBoardUseCase(board)
            when (result) {
                is Result.Error -> {

                    _sideEffectChannel.send(
                        HomeSideEffect.ShowSnackBar(
                            SnackbarEvent(
                                message = UiText.StringResource(R.string.error_creating_board)
                            )
                        )
                    )

                }

                is Result.Success -> Unit
            }
            dismissCreateBoardDialog()
        }
    }

    private fun showCreateBoardDialog() {
        _uiState.update { it.copy(showCreateBoardDialog = true) }
    }

    private fun dismissCreateBoardDialog() {
        _uiState.update {
            it.copy(
                showCreateBoardDialog = false,
                newBoardName = ""
            )
        }
    }

    private fun onNewBoardNameChange(newBoardName: String) {
        _uiState.update { it.copy(newBoardName = newBoardName) }
    }

    private fun navigateToBoardScreen(boardId: Long) {
        viewModelScope.launch {
            _sideEffectChannel.send(HomeSideEffect.NavigateToBoardScreen(boardId))
        }
    }
}