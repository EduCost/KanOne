package com.educost.kanone.data.repository

import com.educost.kanone.data.local.BoardDao
import com.educost.kanone.data.mapper.toBoard
import com.educost.kanone.domain.error.LocalDataError
import com.educost.kanone.domain.model.Board
import com.educost.kanone.domain.repository.BoardRepository
import com.educost.kanone.utils.Result
import jakarta.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

class BoardRepositoryImpl @Inject constructor(val boardDao: BoardDao) : BoardRepository {


}