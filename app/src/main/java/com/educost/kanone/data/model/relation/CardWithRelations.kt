package com.educost.kanone.data.model.relation

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation
import com.educost.kanone.data.model.entity.AttachmentEntity
import com.educost.kanone.data.model.entity.CardEntity
import com.educost.kanone.data.model.entity.TaskEntity
import com.educost.kanone.data.model.entity.LabelCardCrossRef
import com.educost.kanone.data.model.entity.LabelEntity

data class CardWithRelations(
    @Embedded val card: CardEntity,

    @Relation(
        parentColumn = "id",
        entityColumn = "card_id"
    )
    val tasks: List<TaskEntity>,

    @Relation(
        parentColumn = "id",
        entityColumn = "card_id"
    )
    val attachments: List<AttachmentEntity>,

    @Relation(
        parentColumn = "id",
        entityColumn = "id",
        associateBy = Junction(
            value = LabelCardCrossRef::class,
            parentColumn = "card_id",
            entityColumn = "label_id"
        )
    )
    val labels: List<LabelEntity>
)
