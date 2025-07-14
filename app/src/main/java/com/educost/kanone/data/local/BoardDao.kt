package com.educost.kanone.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.educost.kanone.data.model.entity.BoardEntity
import com.educost.kanone.data.model.relation.BoardWithColumns
import kotlinx.coroutines.flow.Flow

@Dao
interface BoardDao {

    @Transaction
    @Query("SELECT * FROM boards WHERE id = :boardId")
    fun observeCompleteBoard(boardId: Long): Flow<BoardWithColumns>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBoard(board: BoardEntity)

    @Delete
    suspend fun deleteBoard(board: BoardEntity)
}