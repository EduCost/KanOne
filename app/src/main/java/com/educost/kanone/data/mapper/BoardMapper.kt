package com.educost.kanone.data.mapper

import com.educost.kanone.data.model.entity.BoardEntity
import com.educost.kanone.domain.model.Board

fun BoardEntity.toBoard(): Board = Board(
    id = this.id,
    name = this.name,
)

fun Board.toBoardEntity(): BoardEntity = BoardEntity(
    id = this.id,
    name = this.name,
)

