package com.educost.kanone.presentation.screens.board

import androidx.lifecycle.ViewModel
import com.educost.kanone.domain.model.Board
import com.educost.kanone.domain.usecase.ObserveCompleteBoardUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

class BoardViewModel @Inject constructor(
//    private val observeCompleteBoardUseCase: ObserveCompleteBoardUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(BoardState())
    val uiState = _uiState.asStateFlow()


    fun onIntent(intent: BoardIntent) {}

}