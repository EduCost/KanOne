package com.educost.kanone.domain.repository

import com.educost.kanone.domain.model.Attachment

interface AttachmentRepository {

    suspend fun createAttachment(attachment: Attachment, cardId: Long): Boolean

    suspend fun deleteAttachment(attachment: Attachment, cardId: Long): Boolean

}