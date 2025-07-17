package com.educost.kanone.data.mapper

import com.educost.kanone.data.model.entity.AttachmentEntity
import com.educost.kanone.domain.model.Attachment

fun AttachmentEntity.toAttachment() = Attachment(
    id = this.id,
    fileName = this.fileName,
    cardId = this.cardId
)

fun Attachment.toAttachmentEntity() = AttachmentEntity(
    id = this.id,
    fileName = this.fileName,
    cardId = this.cardId
)