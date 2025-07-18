package com.educost.kanone.domain.repository

import com.educost.kanone.domain.error.FetchDataError
import com.educost.kanone.domain.model.Attachment
import com.educost.kanone.utils.Result
import kotlinx.coroutines.flow.Flow

interface AttachmentRepository {

    fun observeAttachments(cardsIds: List<Long>): Flow<Result<List<Attachment>, FetchDataError>>
}