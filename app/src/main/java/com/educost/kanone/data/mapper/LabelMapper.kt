package com.educost.kanone.data.mapper

import com.educost.kanone.data.model.entity.LabelEntity
import com.educost.kanone.domain.model.Label

fun LabelEntity.toLabel() = Label(
    id = this.id,
    name = this.name,
    color = this.color
)

fun Label.toLabelEntity(boardId: Long) = LabelEntity(
    id = this.id,
    name = this.name,
    color = this.color,
    boardId = boardId
)