package com.educost.kanone.domain.usecase

import com.educost.kanone.domain.model.Attachment
import com.educost.kanone.domain.repository.AttachmentRepository

class DeleteAttachmentUseCase(val repository: AttachmentRepository) {

    suspend operator fun invoke(attachment: Attachment, cardId: Long): Boolean {
        return repository.deleteAttachment(attachment, cardId)
    }

}