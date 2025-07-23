package com.educost.kanone.presentation.screens.board

import com.educost.kanone.domain.model.Attachment
import com.educost.kanone.domain.model.Board
import com.educost.kanone.domain.model.Checklist
import com.educost.kanone.domain.model.Label
import com.educost.kanone.presentation.model.CardUi
import com.educost.kanone.presentation.model.ColumnUi

data class BoardState(
    val board: Board? = null,
    val isLoading: Boolean = false,
    val error: String? = null,
    val columns: List<ColumnUi> = emptyList(),
    val cards: List<CardUi> = emptyList(),
    val attachments: List<Attachment> = emptyList(),
    val checklists: List<Checklist> = emptyList(),
    val labels: List<Label> = emptyList()
)
