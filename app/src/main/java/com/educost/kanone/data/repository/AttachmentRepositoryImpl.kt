package com.educost.kanone.data.repository

import android.util.Log
import com.educost.kanone.data.local.AttachmentDao
import com.educost.kanone.data.mapper.toAttachment
import com.educost.kanone.domain.error.LocalDataError
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

    override fun observeAttachments(cardsIds: List<Long>): Flow<Result<List<Attachment>, LocalDataError>> {
        return attachmentDao.observeAttachments(cardsIds).map { attachments ->
            Result.Success(attachments.map { it.toAttachment() })
        }.catch { e ->
            when (e) {
                is IOException -> Result.Error(LocalDataError.IO_ERROR)
                else -> {
                    Log.e(LogTags.ATTACHMENT_REPO, e.stackTraceToString())
                    Result.Error(LocalDataError.UNKNOWN)
                }
            }
        }
    }

}