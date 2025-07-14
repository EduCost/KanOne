package com.educost.kanone.data.model.relation

import androidx.room.Embedded
import androidx.room.Relation
import com.educost.kanone.data.model.entity.CardEntity
import com.educost.kanone.data.model.entity.ColumnEntity

data class ColumnsWithCards(
    @Embedded val columns: ColumnEntity,

    @Relation(
        entity = CardEntity::class,
        parentColumn = "id",
        entityColumn = "columnId"
    )
    val cards: List<CardWithRelations>
)
