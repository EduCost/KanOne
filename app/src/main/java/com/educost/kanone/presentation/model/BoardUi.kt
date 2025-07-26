package com.educost.kanone.presentation.model

import androidx.compose.foundation.lazy.LazyListState
import com.educost.kanone.domain.model.KanbanColumn

data class BoardUi(
    val id: Long,
    val name: String,
    val columns: List<ColumnUi>,
    val coordinates: Coordinates = Coordinates(),
    val listState: LazyListState = LazyListState()
)
