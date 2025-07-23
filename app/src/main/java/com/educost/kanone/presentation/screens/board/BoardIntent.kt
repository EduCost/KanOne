package com.educost.kanone.presentation.screens.board

sealed interface BoardIntent {
    data class ObserveBoard(val boardId: Long) : BoardIntent
}