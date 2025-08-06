package com.educost.kanone.presentation.screens.board.state

data class ScrollState(
    var isHorizontalScrolling: Boolean = false,
    var isVerticalScrolling: Boolean = false,
    var horizontalSpeed: Float = 0f,
    var verticalSpeed: Float = 0f,
    var scrollingColumnIndex: Int? = null
)