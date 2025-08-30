package com.educost.kanone.data.repository

import com.educost.kanone.data.local.AttachmentDao
import com.educost.kanone.data.mapper.toAttachmentEntity
import com.educost.kanone.domain.model.Attachment
import com.educost.kanone.domain.repository.AttachmentRepository

class AttachmentRepositoryImpl(val attachmentDao: AttachmentDao) : AttachmentRepository {

    override suspend fun createAttachment(attachment: Attachment, cardId: Long): Boolean {
        return try {
            attachmentDao.createAttachment(attachment.toAttachmentEntity(cardId))
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    override suspend fun deleteAttachment(attachment: Attachment, cardId: Long): Boolean {
        return try {
            attachmentDao.deleteAttachment(attachment.toAttachmentEntity(cardId))
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

}