package com.educost.kanone.domain.usecase

import com.educost.kanone.domain.error.GenericError
import com.educost.kanone.domain.model.Attachment
import com.educost.kanone.domain.repository.AttachmentRepository
import com.educost.kanone.utils.Result

class CreateAttachmentUseCase(val repository: AttachmentRepository) {

    suspend operator fun invoke(attachment: Attachment, cardId: Long): Result<Unit, GenericError> {
        return repository.createAttachment(attachment, cardId)
    }

}