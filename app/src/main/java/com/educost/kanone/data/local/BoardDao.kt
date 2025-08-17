package com.educost.kanone.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.educost.kanone.data.model.entity.BoardEntity
import com.educost.kanone.data.model.entity.CardEntity
import com.educost.kanone.data.model.entity.ColumnEntity
import com.educost.kanone.data.model.relation.BoardWithColumns
import kotlinx.coroutines.flow.Flow

@Dao
interface BoardDao {

    @Query("SELECT * FROM boards WHERE id = :boardId")
    suspend fun getBoard(boardId: Long): BoardEntity

    @Query("SELECT * FROM boards")
    fun observeAllBoards(): Flow<List<BoardEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun createBoard(board: BoardEntity): Long

    @Delete
    suspend fun deleteBoard(board: BoardEntity)

    @Transaction
    @Query("SELECT * FROM boards WHERE id = :boardId")
    fun observeCompleteBoard(boardId: Long): Flow<BoardWithColumns>

    @Update
    suspend fun updateColumns(columns: List<ColumnEntity>)

    @Update
    suspend fun updateCards(cards: List<CardEntity>)

    @Transaction
    suspend fun updateBoardData(columns: List<ColumnEntity>, cards: List<CardEntity>) {
        updateColumns(columns)
        updateCards(cards)
    }
}