package com.educost.kanone.data.model.relation

import androidx.room.Embedded
import androidx.room.Relation
import com.educost.kanone.data.model.entity.BoardEntity
import com.educost.kanone.data.model.entity.ColumnEntity

data class BoardWithColumns(
    @Embedded
    val board: BoardEntity,

    @Relation(
        entity = ColumnEntity::class,
        parentColumn = "id",
        entityColumn = "boardId"
    )
    val columns: List<ColumnEntity>
)

