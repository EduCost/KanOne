package com.educost.kanone.data.repository

import android.util.Log
import com.educost.kanone.data.local.AttachmentDao
import com.educost.kanone.data.mapper.toAttachment
import com.educost.kanone.data.mapper.toAttachmentEntity
import com.educost.kanone.domain.error.FetchDataError
import com.educost.kanone.domain.error.GenericError
import com.educost.kanone.domain.model.Attachment
import com.educost.kanone.domain.repository.AttachmentRepository
import com.educost.kanone.utils.LogTags
import com.educost.kanone.utils.Result
import jakarta.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

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