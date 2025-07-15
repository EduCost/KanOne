package com.educost.kanone.data.model.relation

import androidx.room.Embedded
import androidx.room.Relation
import com.educost.kanone.data.model.entity.CardEntity
import com.educost.kanone.data.model.entity.ColumnEntity

data class ColumnsWithCards(
    @Embedded val column: ColumnEntity,

    @Relation(
        entity = CardEntity::class,
        parentColumn = "id",
        entityColumn = "column_id"
    )
    val cards: List<CardWithRelations>
)
