package com.educost.kanone.data.repository

import com.educost.kanone.data.local.AttachmentDao
import com.educost.kanone.data.mapper.toAttachmentEntity
import com.educost.kanone.domain.error.GenericError
import com.educost.kanone.domain.model.Attachment
import com.educost.kanone.domain.repository.AttachmentRepository
import com.educost.kanone.utils.Result
import jakarta.inject.Inject

class AttachmentRepositoryImpl @Inject constructor(val attachmentDao: AttachmentDao) : AttachmentRepository {

    override suspend fun createAttachment(attachment: Attachment, cardId: Long): Result<Unit, GenericError> {
        return try {
            attachmentDao.createAttachment(attachment.toAttachmentEntity(cardId))
            Result.Success(Unit)
        } catch (e: Exception) {
            e.printStackTrace()
            Result.Error(GenericError)
        }
    }

}