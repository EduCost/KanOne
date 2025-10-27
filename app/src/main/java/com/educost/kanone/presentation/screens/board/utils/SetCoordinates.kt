package com.educost.kanone.presentation.screens.board.utils

import com.educost.kanone.presentation.screens.board.model.Coordinates
import com.educost.kanone.presentation.screens.board.state.BoardUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

fun MutableStateFlow<BoardUiState>.setBoardCoordinates(newCoordinates: Coordinates) {
    this.update { currentState ->
        currentState.copy(
            board = currentState.board?.copy(coordinates = newCoordinates)
        )
    }
}


fun MutableStateFlow<BoardUiState>.setColumnHeadersCoordinates(columnsBatch: HashMap<Long, Coordinates>) {
    this.update { currentState ->
        val board = currentState.board ?: return@update currentState
        val columns = board.columns

        val updatedColumn = columns.map { column ->
            if (column.id in columnsBatch.keys) {
                column.copy(headerCoordinates = columnsBatch[column.id]!!)
            } else {
                column
            }
        }

        currentState.copy(
            board = board.copy(columns = updatedColumn)
        )
    }
}

fun MutableStateFlow<BoardUiState>.setColumnListsCoordinates(columnsBatch: HashMap<Long, Coordinates>) {
    this.update { currentState ->
        val board = currentState.board ?: return@update currentState
        val columns = board.columns

        val updatedColumn = columns.map { column ->
            if (column.id in columnsBatch.keys) {
                column.copy(listCoordinates = columnsBatch[column.id]!!)
            } else {
                column
            }
        }

        currentState.copy(
            board = board.copy(columns = updatedColumn)
        )
    }
}

fun MutableStateFlow<BoardUiState>.setColumnsCoordinates(columnBatch: HashMap<Long, Coordinates>) {
    this.update { currentState ->
        val board = currentState.board ?: return@update currentState
        val columns = board.columns

        val updatedColumn = columns.map { column ->
            if (column.id in columnBatch.keys) {
                column.copy(coordinates = columnBatch[column.id]!!)
            } else {
                column
            }
        }

        currentState.copy(
            board = board.copy(columns = updatedColumn)
        )
    }
}

fun MutableStateFlow<BoardUiState>.setCardsCoordinates(cardBatch: HashMap<Long, Pair<Long, Coordinates>>) {
    this.update { currentState ->
        val board = currentState.board ?: return@update currentState
        val columns = board.columns


        val updatedColumns = columns.map { column ->
            val updatedCards = column.cards.map { card ->
                if (card.id in cardBatch.keys) {
                    card.copy(coordinates = cardBatch[card.id]!!.second)
                } else {
                    card
                }
            }
            column.copy(cards = updatedCards)
        }


        currentState.copy(
            board = board.copy(columns = updatedColumns)
        )
    }
}




















