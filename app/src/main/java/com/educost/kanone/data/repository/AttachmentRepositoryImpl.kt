package com.educost.kanone.data.repository

import com.educost.kanone.data.local.AttachmentDao
import com.educost.kanone.data.mapper.toAttachmentEntity
import com.educost.kanone.domain.logs.LogHandler
import com.educost.kanone.domain.logs.LogLevel
import com.educost.kanone.domain.logs.LogLocation
import com.educost.kanone.domain.model.Attachment
import com.educost.kanone.domain.repository.AttachmentRepository

class AttachmentRepositoryImpl(
    private val attachmentDao: AttachmentDao,
    private val logHandler: LogHandler
) : AttachmentRepository {

    override suspend fun createAttachment(attachment: Attachment, cardId: Long): Boolean {
        return try {
            attachmentDao.createAttachment(attachment.toAttachmentEntity(cardId))
            true
        } catch (e: Exception) {
            logHandler.log(
                throwable = e,
                message = "Error creating attachment",
                from = LogLocation.ATTACHMENT_REPOSITORY,
                level = LogLevel.ERROR
            )

            false
        }
    }

    override suspend fun deleteAttachment(attachment: Attachment, cardId: Long): Boolean {
        return try {
            attachmentDao.deleteAttachment(attachment.toAttachmentEntity(cardId))
            true
        } catch (e: Exception) {
            logHandler.log(
                throwable = e,
                message = "Error deleting attachment",
                from = LogLocation.ATTACHMENT_REPOSITORY,
                level = LogLevel.ERROR
            )

            false
        }
    }

}