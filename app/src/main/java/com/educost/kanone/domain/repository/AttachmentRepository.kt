package com.educost.kanone.domain.repository

import com.educost.kanone.domain.error.GenericError
import com.educost.kanone.domain.model.Attachment
import com.educost.kanone.utils.Result

interface AttachmentRepository {

    suspend fun createAttachment(attachment: Attachment, cardId: Long): Result<Unit, GenericError>

}