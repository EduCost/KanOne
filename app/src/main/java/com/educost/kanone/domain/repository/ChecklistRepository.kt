package com.educost.kanone.domain.repository

import com.educost.kanone.domain.error.LocalDataError
import com.educost.kanone.domain.model.Checklist
import com.educost.kanone.utils.Result
import kotlinx.coroutines.flow.Flow

interface ChecklistRepository {

    fun observeChecklists(cardIds: List<Long>): Flow<Result<List<Checklist>, LocalDataError>>
}