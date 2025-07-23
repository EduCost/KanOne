package com.educost.kanone.presentation.screens.board

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.educost.kanone.R
import com.educost.kanone.domain.error.FetchDataError
import com.educost.kanone.domain.usecase.ObserveCompleteBoardUseCase
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
    private val observeCompleteBoardUseCase: ObserveCompleteBoardUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(BoardState())
    val uiState = _uiState.asStateFlow()

    private val _sideEffectChannel = Channel<BoardSideEffect>(Channel.BUFFERED)
    val sideEffectFlow = _sideEffectChannel.receiveAsFlow()


    fun onIntent(intent: BoardIntent) {
        when (intent) {
            is BoardIntent.ObserveBoard -> observeBoard(intent.boardId)
        }
    }

    private fun observeBoard(boardId: Long) {

        _uiState.update { it.copy(isLoading = true) }

        viewModelScope.launch {
            observeCompleteBoardUseCase(boardId).collect { result ->
                when (result) {
                    is Result.Success -> {
                        _uiState.update {
                            it.copy(
                                board = result.data,
                                isLoading = false
                            )
                        }
                    }

                    is Result.Error -> {

                        when (result.error) {
                            FetchDataError.IO_ERROR -> {
                                _sideEffectChannel.send(
                                    BoardSideEffect.ShowSnackBar(
                                        SnackbarEvent(
                                            message = UiText.StringResource(R.string.fetch_io_error)
                                        )
                                    )
                                )
                            }

                            FetchDataError.UNKNOWN -> {
                                _sideEffectChannel.send(
                                    BoardSideEffect.ShowSnackBar(
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
}