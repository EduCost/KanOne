package com.educost.kanone.presentation.screens.board.utils

import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import com.educost.kanone.presentation.screens.board.BoardIntent

fun Modifier.enableBoardDragAndDrop(
    isOnVerticalLayout: Boolean,
    onIntent: (BoardIntent) -> Unit
): Modifier {
    return this.pointerInput(isOnVerticalLayout) {
        detectDragGesturesAfterLongPress(
            onDrag = { change, _ ->
                onIntent(BoardIntent.OnDrag(change.position, isOnVerticalLayout))
            },
            onDragStart = { offset ->
                onIntent(BoardIntent.OnDragStart(offset, isOnVerticalLayout))
            },
            onDragEnd = {
                onIntent(BoardIntent.OnDragStop)
            },
            onDragCancel = {
                onIntent(BoardIntent.OnDragStop)
            }
        )
    }
}